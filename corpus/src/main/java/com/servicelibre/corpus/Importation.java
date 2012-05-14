package com.servicelibre.corpus;

import java.io.File;
import java.util.List;

import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.beust.jcommander.JCommander;
import com.servicelibre.corpus.entity.CatégorieListe;
import com.servicelibre.corpus.entity.Corpus;
import com.servicelibre.corpus.entity.DocMetadata;
import com.servicelibre.corpus.entity.Liste;
import com.servicelibre.corpus.entity.ListeMot;
import com.servicelibre.corpus.entity.Mot;
import com.servicelibre.corpus.repository.CatégorieListeRepository;
import com.servicelibre.corpus.repository.CorpusRepository;
import com.servicelibre.corpus.repository.DocMetadataRepository;
import com.servicelibre.corpus.repository.ListeMotRepository;
import com.servicelibre.corpus.repository.ListeRepository;
import com.servicelibre.corpus.repository.MotPrononciationRepository;
import com.servicelibre.corpus.repository.MotRepository;
import com.servicelibre.corpus.repository.PrononciationRepository;

public class Importation {

	private static Logger logger = LoggerFactory.getLogger(Importation.class);

	ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "application-context.xml", "system-context.xml" });

	CorpusRepository corpusRepo = (CorpusRepository) context.getBean("corpusRepository");
	CatégorieListeRepository catégorieListeRepo = (CatégorieListeRepository) context.getBean("catégorieListeRepository");
	ListeRepository listeRepo = (ListeRepository) context.getBean("listeRepository");
	ListeMotRepository listeMotRepo = (ListeMotRepository) context.getBean("listeMotRepository");
	MotPrononciationRepository motPrononciationRepo = (MotPrononciationRepository) context.getBean("motPrononciationRepository");
	DocMetadataRepository docMetadataRepo = (DocMetadataRepository) context.getBean("docMetadataRepository");

	MotRepository motRepo = (MotRepository) context.getBean("motRepository");
	PrononciationRepository prononciationRepo = (PrononciationRepository) context.getBean("prononciationRepository");

	public void importeCorpus(String nomDuCorpus, File corpusFichier) {

		logger.info("Démarrage de l'importation de corpus. ");

		// Charger le document XML
		SAXReader reader = new SAXReader();
		Document document = DocumentHelper.createDocument();
		try {
			document = reader.read(corpusFichier);
		} catch (DocumentException e) {
			logger.error("Problème lors de la lecture du fichier de données du corpus {}: {}", corpusFichier, e.getMessage());
			e.printStackTrace();
			return;
		}

		// Récupération du nom du corpus
		Element corpusElem = (Element) document.selectSingleNode("/corpus");
		String nomDuCorpusXML = corpusElem.selectSingleNode("nom").getText();

		// Importation d'un corpus « tel quel » ou importation d'un corpus sous un autre nom
		if (nomDuCorpus == null || nomDuCorpus.equals(nomDuCorpusXML)) {
			nomDuCorpus = nomDuCorpusXML;
		}

		logger.info("Importation du corpus « {} » (depuis les données du corpus {}). ", nomDuCorpus, nomDuCorpusXML);

		// Est-ce que le corpus à importer existe déjà?
		Corpus corpus = corpusRepo.findByNom(nomDuCorpus);

		if (corpus != null) {
			// Mise à jour d'un corpus existant
			// Suppression du corpus existant
			logger.info("Suppression du corpus existant: {}.", nomDuCorpus);

			corpusRepo.delete(corpus);

		}

		// Importation d'un nouveau corpus
		logger.info("Importation du corpus {}.", nomDuCorpus);

		corpus = getCorpus(corpusElem);

		corpus.setNom(nomDuCorpus);

		corpus = corpusRepo.save(corpus);

		// Importation métadonnées
		importeDocMétaDonnées(corpusElem, corpus);

		// Importation listes de mots (+ catégorie, mots, prononications, etc.)
		importeListes(corpusElem, corpus);

		logger.info("Fin de l'exportation du corpus « {} ». ", nomDuCorpus);

	}

	@SuppressWarnings("unchecked")
	private void importeListes(Element corpusElem, Corpus corpus) {

		List<Element> catégorieListeElems = (List<Element>) corpusElem.selectNodes("catégorieListes/catégorieListe");

		for (Element catégorieListeElem : catégorieListeElems) {

			CatégorieListe catégorieListe = new CatégorieListe();
			catégorieListe.setCorpus(corpus);
			catégorieListe.setNom(catégorieListeElem.selectSingleNode("nom").getText());
			catégorieListe.setDescription(catégorieListeElem.selectSingleNode("description").getText());
			catégorieListe.setOrdre(Integer.parseInt(catégorieListeElem.selectSingleNode("ordre").getText()));

			catégorieListe = catégorieListeRepo.save(catégorieListe);

			// importation des listes pour cette catégorie
			List<Element> listeElems = (List<Element>) catégorieListeElem.selectNodes("listes/liste");

			for (Element listeElem : listeElems) {

				Liste liste = new Liste();
				liste.setCatégorieListe(catégorieListe);
				liste.setNom(listeElem.selectSingleNode("nom").getText());
				liste.setDescription(listeElem.selectSingleNode("description").getText());
				liste.setOrdre(Integer.parseInt(listeElem.selectSingleNode("ordre").getText()));

				liste = listeRepo.save(liste);

				// Importer les mots (la relation mot - liste)
				List<Element> motElems = (List<Element>) listeElem.selectNodes("mots/mot");

				for (Element motElem : motElems) {
					// Recherche le mot dans la DB
					String lemme = motElem.elementText("lemme");
					String motString = motElem.elementText("mot");
					String catgram = motElem.elementText("catgram");
					String genre = motElem.elementText("genre");
					Mot mot = motRepo.findByLemmeAndMotAndCatgramAndGenre(lemme, motString, catgram, genre);

					if (mot == null) {
						logger.error("Mot {} de la liste {} introuvable dans la base de données.  Ne sera pas ajouté à la liste.", mot, liste);
					} else {
						listeMotRepo.save(new ListeMot(mot, liste));
					}
				}

			}

		}

	}

	private void importeDocMétaDonnées(Element corpusElem, Corpus corpus) {

		@SuppressWarnings("unchecked")
		List<Element> docMetaDonnéeElems = (List<Element>) corpusElem.selectNodes("docmetadonnées/docmetadonnée");
		for (Element docMétadonnéeElem : docMetaDonnéeElems) {
			DocMetadata docMetaData = new DocMetadata();
			docMetaData.setCorpus(corpus);
			docMetaData.setNom(docMétadonnéeElem.selectSingleNode("nom").getText());
			docMetaData.setDescription(docMétadonnéeElem.selectSingleNode("description").getText());
			docMetaData.setFiltre(Boolean.parseBoolean(docMétadonnéeElem.selectSingleNode("filtre").getText()));
			docMetaData.setPrimaire(Boolean.parseBoolean(docMétadonnéeElem.selectSingleNode("primaire").getText()));
			docMetaData.setChampIndex(docMétadonnéeElem.selectSingleNode("champindex").getText());
			docMetaData.setOrdre(Integer.parseInt(docMétadonnéeElem.selectSingleNode("ordre").getText()));

			docMetaData = docMetadataRepo.save(docMetaData);

		}

	}

	private Corpus getCorpus(Element corpusElem) {

		Corpus corpus = new Corpus();

		corpus.setNom(corpusElem.selectSingleNode("nom").getText());
		corpus.setDescription(corpusElem.selectSingleNode("description").getText());
		corpus.setDossierData(corpusElem.selectSingleNode("dossierdata").getText());
		corpus.setAnalyseurRechercheFQCN(corpusElem.selectSingleNode("analyseurrecherchefqcn").getText());
		corpus.setAnalyseurLexicalFQCN(corpusElem.selectSingleNode("analyseurlexicalfqcn").getText());
		corpus.setParDéfaut(Boolean.parseBoolean(corpusElem.selectSingleNode("pardéfaut").getText()));

		return corpus;
	}

	@SuppressWarnings("unchecked")
	private void importeMots(File motsFichier) {

		// Charger le document XML
		SAXReader reader = new SAXReader();

		Document document = DocumentHelper.createDocument();
		try {
			document = reader.read(motsFichier);
		} catch (DocumentException e) {
			logger.error("Problème lors de la lecture du fichier de données des mots {}: {}", motsFichier, e.getMessage());
			e.printStackTrace();
			return;
		}

		logger.info("Démarrage de l'importation des mots({})");

		// Vérifier l'existence du mot. Si existe: mettre à jour les autres champs. Sinon, ajouter
		List<Element> motElems = (List<Element>) document.selectNodes("/mots/mot");
		for (Element motElem : motElems) {

			// Recherche le mot dans la DB
			String lemme = motElem.elementText("lemme");
			String motString = motElem.elementText("mot");
			String catgram = motElem.elementText("catgram");
			String genre = motElem.elementText("genre");
			Mot mot = motRepo.findByLemmeAndMotAndCatgramAndGenre(lemme, motString, catgram, genre);

			if (mot == null) {
				logger.info("Mot {} introuvable dans la base de données. Ajout du nouveau mot.", mot);
				mot = new Mot(motString, lemme, Boolean.parseBoolean(motElem.elementText("isLemme")), catgram, genre, "", "", false, "");

			}
			else {
				logger.debug("Le mot {} existe déjà.  Mise à jour des données.", mot);
			}
			mot.setLemme(Boolean.parseBoolean(motElem.elementText("isLemme")));
			mot.setGenre(genre);
			mot.setAutreGraphie(motElem.elementText("autreGraphie"));
			mot.setCatgramPrésicion(motElem.elementText("catgramPrésicion"));
			mot.setNombre(motElem.elementText("nombre"));
			mot.setNote(motElem.elementText("note"));
			mot.setRo(Boolean.parseBoolean(motElem.elementText("ro")));

			mot = motRepo.save(mot);
			

			// Ajout des prononciations + lien avec mot (motPrononciation)

		}

		logger.info("Fin de l'importation des mots");
	}

	private void importePrononciations(File prononciationsFichier) {

		logger.info("Démarrage de l'importation des prononciations({})");

		logger.info("Fin de l'importation des prononciations");
	}

	public static void main(String[] argv) {

		ImportationLigneCommande cmd = new ImportationLigneCommande();

		JCommander jCommander = new JCommander(cmd, argv);

		if (cmd.corpusFichier == null && cmd.motsFichier == null && cmd.prononciationsFichier == null) {
			jCommander.usage();
			System.exit(-1);
		}

		Importation importation = new Importation();

		if (cmd.corpusFichier != null) {
			importation.importeCorpus(cmd.nomDuCorpus, cmd.corpusFichier);
		}

		if (cmd.motsFichier != null) {
			importation.importeMots(cmd.motsFichier);
		}

		if (cmd.prononciationsFichier != null) {
			importation.importePrononciations(cmd.prononciationsFichier);
		}

	}

}
