package com.servicelibre.corpus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;

import com.beust.jcommander.JCommander;
import com.servicelibre.entities.corpus.CatégorieListe;
import com.servicelibre.entities.corpus.Corpus;
import com.servicelibre.entities.corpus.DocMetadata;
import com.servicelibre.entities.corpus.Liste;
import com.servicelibre.entities.corpus.ListeMot;
import com.servicelibre.entities.corpus.Mot;
import com.servicelibre.repositories.corpus.CatégorieListeRepository;
import com.servicelibre.repositories.corpus.CorpusRepository;
import com.servicelibre.repositories.corpus.DocMetadataRepository;
import com.servicelibre.repositories.corpus.ListeMotRepository;
import com.servicelibre.repositories.corpus.ListeRepository;
import com.servicelibre.repositories.corpus.MotPrononciationRepository;
import com.servicelibre.repositories.corpus.MotRepository;
import com.servicelibre.repositories.corpus.PrononciationRepository;

public class Importation {

	private static Logger logger = LoggerFactory.getLogger(Importation.class);

	public enum Mode {
		MAJ, REMPLACE_TOUT
	}

	ApplicationContext context;

	CorpusRepository corpusRepo;
	CatégorieListeRepository catégorieListeRepo;
	ListeRepository listeRepo;
	ListeMotRepository listeMotRepo;
	MotPrononciationRepository motPrononciationRepo;
	DocMetadataRepository docMetadataRepo;
	MotRepository motRepo;
	PrononciationRepository prononciationRepo;

	public Importation() {
		super();
		setContext(new ClassPathXmlApplicationContext(new String[] { "application-context.xml", "system-context.xml" }));
	}

	public Importation(ApplicationContext context) {
		setContext(context);
	}

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

	public void importeMots(File motsFichier, Mode mode) {
		try {
			importeMots(new FileReader(motsFichier), mode);
		} catch (FileNotFoundException e) {
			logger.error("Problème lors de la lecture du fichier de données des mots {}: {}", motsFichier, e.getMessage());
		}
	}
	
	/**
	 * Importe une liste de mots
	 * Note: toutes les listes primaires spécifiées éventuellement pour les mots doivent exister dans le système avant d'exécuter l'importation.
	 * @param reader
	 * @param mode
	 */
	public void importeMots(Reader reader, Mode mode) {

		// Charger le document XML
		SAXReader saxReader = new SAXReader();
		Map<String, Liste> cacheListePartitionPrimaire = new HashMap<String, Liste>(10);

		Document document = DocumentHelper.createDocument();
		try {
			document = saxReader.read(reader);
		} catch (DocumentException e) {
			logger.error("Problème lors de la lecture du flux de données des mots: {}", e.getMessage());
			e.printStackTrace();
			return;
		}

		if (mode == Mode.REMPLACE_TOUT) {
			motRepo.deleteAll();
		}

		// Vérifier l'existence du mot. Si existe: mettre à jour les autres champs. Sinon, ajouter
		List<Element> motElems = (List<Element>) document.selectNodes("/mots/mot");
		int nbMotsÀImporter = motElems.size();
		int nbMotsImportés = 0;

		logger.info("Démarrage de l'importation des mots({})", nbMotsÀImporter);

		for (Element motElem : motElems) {

			String lemme = motElem.elementText("lemmeGraphie");
			String motString = motElem.elementText("motGraphie");
			String catgram = motElem.elementText("catgram");
			String genre = motElem.elementText("genre");
			Mot mot = null;

			if (mode == Mode.MAJ) {
				// Recherche le mot dans la DB
				mot = motRepo.findByLemmeAndMotAndCatgramAndGenre(lemme, motString, catgram, genre);

				if (mot == null) {
					logger.info("Mot {} introuvable dans la base de données. Ajout du nouveau mot.", mot);
					mot = new Mot(motString, lemme, Boolean.parseBoolean(motElem.elementText("estUnLemme")), catgram, genre, "", "", false, "");

				} else {
					logger.debug("Le mot {} existe déjà.  Mise à jour des données.", mot);
				}
			} else {
				mot = new Mot(motString, lemme, Boolean.parseBoolean(motElem.elementText("estUnLemme")), catgram, genre, "", "", false, "");
			}

			mot.setCatgramAffichage(motElem.elementText("catgramAffichage"));
			mot.setEstUnLemme(Boolean.parseBoolean(motElem.elementText("estUnLemme")));
			mot.setGenre(genre);
			mot.setAutreGraphie(motElem.elementText("motAutreGraphie"));
			mot.setCatgramPrécision(motElem.elementText("catgramPrésicion"));
			mot.setMotNote(motElem.elementText("motNote"));
			mot.setLemmeNote(motElem.elementText("lemmeNote"));
			mot.setNombre(motElem.elementText("nombre"));
			mot.setNote(motElem.elementText("note"));
			mot.setRo(Boolean.parseBoolean(motElem.elementText("estGraphieRO")));

			// Rechercher la liste de partition primaire éventuelle (+ mise en cache)
			String nomPartition = motElem.elementText("partition");
			if (nomPartition != null) {
				// est-elle en cache?
				Liste listePartitionPrimaire = cacheListePartitionPrimaire.get(nomPartition);
				if (listePartitionPrimaire == null) {
					listePartitionPrimaire = listeRepo.findByNom(nomPartition);
					
					// TODO: la créer si elle n'existe pas?  Créer éventuellemet une catégorie de liste de partitionement? Quid sélecion du corpus?
					cacheListePartitionPrimaire.put(nomPartition, listePartitionPrimaire);
				}
				mot.setListePartitionPrimaire(listePartitionPrimaire);
			}

			try {
				mot = motRepo.save(mot);
				

				if (mot.getListePartitionPrimaire() != null) {
					listeMotRepo.save(new ListeMot(mot, mot.getListePartitionPrimaire()));
				}

				nbMotsImportés++;
				if (nbMotsImportés % 100 == 0) {
					logger.info("Progression de l'importation: {}/{}", nbMotsImportés, nbMotsÀImporter);
				}
				
			} catch (DataIntegrityViolationException e) {
				logger.error("Le mot {} existe déjà!", mot.getMot());
			}


			// Ajout des prononciations + lien avec mot (motPrononciation)

		}

		logger.info("Fin de l'importation des {} mots.", nbMotsÀImporter);

	}

	@SuppressWarnings("unchecked")
	public void importeMots(File motsFichier) {
		importeMots(motsFichier, Mode.MAJ);
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
			importation.importeMots(cmd.motsFichier, cmd.modeImportation);
		}

		if (cmd.prononciationsFichier != null) {
			importation.importePrononciations(cmd.prononciationsFichier);
		}

	}

	public ApplicationContext getContext() {
		return context;
	}

	public void setContext(ApplicationContext context) {

		this.context = context;

		corpusRepo = (CorpusRepository) context.getBean("corpusRepository");
		catégorieListeRepo = (CatégorieListeRepository) context.getBean("catégorieListeRepository");
		listeRepo = (ListeRepository) context.getBean("listeRepository");
		listeMotRepo = (ListeMotRepository) context.getBean("listeMotRepository");
		motPrononciationRepo = (MotPrononciationRepository) context.getBean("motPrononciationRepository");
		docMetadataRepo = (DocMetadataRepository) context.getBean("docMetadataRepository");

		motRepo = (MotRepository) context.getBean("motRepository");
		prononciationRepo = (PrononciationRepository) context.getBean("prononciationRepository");

	}

	public CorpusRepository getCorpusRepo() {
		return corpusRepo;
	}

	public void setCorpusRepo(CorpusRepository corpusRepo) {
		this.corpusRepo = corpusRepo;
	}

	public CatégorieListeRepository getCatégorieListeRepo() {
		return catégorieListeRepo;
	}

	public void setCatégorieListeRepo(CatégorieListeRepository catégorieListeRepo) {
		this.catégorieListeRepo = catégorieListeRepo;
	}

	public ListeRepository getListeRepo() {
		return listeRepo;
	}

	public void setListeRepo(ListeRepository listeRepo) {
		this.listeRepo = listeRepo;
	}

	public ListeMotRepository getListeMotRepo() {
		return listeMotRepo;
	}

	public void setListeMotRepo(ListeMotRepository listeMotRepo) {
		this.listeMotRepo = listeMotRepo;
	}

	public MotPrononciationRepository getMotPrononciationRepo() {
		return motPrononciationRepo;
	}

	public void setMotPrononciationRepo(MotPrononciationRepository motPrononciationRepo) {
		this.motPrononciationRepo = motPrononciationRepo;
	}

	public DocMetadataRepository getDocMetadataRepo() {
		return docMetadataRepo;
	}

	public void setDocMetadataRepo(DocMetadataRepository docMetadataRepo) {
		this.docMetadataRepo = docMetadataRepo;
	}

	public MotRepository getMotRepo() {
		return motRepo;
	}

	public void setMotRepo(MotRepository motRepo) {
		this.motRepo = motRepo;
	}

	public PrononciationRepository getPrononciationRepo() {
		return prononciationRepo;
	}

	public void setPrononciationRepo(PrononciationRepository prononciationRepo) {
		this.prononciationRepo = prononciationRepo;
	}

}
