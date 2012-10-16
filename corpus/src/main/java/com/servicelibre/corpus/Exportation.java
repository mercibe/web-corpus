package com.servicelibre.corpus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Sort;

import com.beust.jcommander.JCommander;
import com.servicelibre.entities.corpus.CatégorieListe;
import com.servicelibre.entities.corpus.Corpus;
import com.servicelibre.entities.corpus.DocMetadata;
import com.servicelibre.entities.corpus.Liste;
import com.servicelibre.entities.corpus.ListeMot;
import com.servicelibre.entities.corpus.Mot;
import com.servicelibre.entities.corpus.MotPrononciation;
import com.servicelibre.entities.corpus.Prononciation;
import com.servicelibre.repositories.corpus.CatégorieListeRepository;
import com.servicelibre.repositories.corpus.CorpusRepository;
import com.servicelibre.repositories.corpus.ListeMotRepository;
import com.servicelibre.repositories.corpus.ListeRepository;
import com.servicelibre.repositories.corpus.MotPrononciationRepository;
import com.servicelibre.repositories.corpus.MotRepository;
import com.servicelibre.repositories.corpus.PrononciationRepository;

public class Exportation {

	private static Logger logger = LoggerFactory.getLogger(Exportation.class);

	ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "application-context.xml", "system-context.xml" });

	CorpusRepository corpusRepo = (CorpusRepository) context.getBean("corpusRepository");
	CatégorieListeRepository catégorieListeRepo = (CatégorieListeRepository) context.getBean("catégorieListeRepository");
	ListeRepository listeRepo = (ListeRepository) context.getBean("listeRepository");
	ListeMotRepository listeMotRepo = (ListeMotRepository) context.getBean("listeMotRepository");
	MotPrononciationRepository motPrononciationRepo = (MotPrononciationRepository) context.getBean("motPrononciationRepository");

	MotRepository motRepo = (MotRepository) context.getBean("motRepository");
	PrononciationRepository prononciationRepo = (PrononciationRepository) context.getBean("prononciationRepository");

	// Optionnel : n'est pas spécifique à un corpus
	// List<Mot> mots = motRepo.findAll();
	// List<Prononciation> findAll = prononciationRepo.findAll();

	public void exporteCorpus(String nomDuCorpus, File corpusFichier) {

		// Recherche du corpus à exporter au format XML
		Corpus corpus = corpusRepo.findByNom(nomDuCorpus);

		if (corpus == null) {
			logger.error("Le corpus « {} » n'existe pas dans la base de données.  Annulation de l'exportation.", nomDuCorpus);
			System.exit(-1);
		}

		if (corpusFichier == null) {
			corpusFichier = new File("/tmp/" + corpus.getNom() + ".xml");
		}

		logger.info("Démarrage de l'exportation du corpus « {} ». ", nomDuCorpus);

		Document doc = DocumentHelper.createDocument();

		Element root = doc.addElement("corpus");

		ajouteCorpusInfo(root, corpus);

		ajouteCorpusDocMetadonnées(root, corpus);

		ajouteCorpusCatégorieListes(root, corpus);

		try {
			// affiche(doc, false);
			sauvegarde(doc, corpusFichier);
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.info("Fin de l'exportation du corpus « {} ». ", nomDuCorpus);

	}

	private void exporteMots(File motsFichier) {

		List<Mot> mots = motRepo.findAll();

		if (motsFichier == null) {
			motsFichier = new File("/tmp/mots.xml");
		}

		logger.info("Démarrage de l'exportation des mots({})", mots.size());

		Document doc = DocumentHelper.createDocument();

		Element root = doc.addElement("mots");

		for (Mot mot : mots) {

			Element motElem = root.addElement("mot");

			motElem.addElement("lemme").addText(mot.getLemme());
			motElem.addElement("mot").addText(mot.getMot());
			motElem.addElement("catgram").addText(mot.getCatgram());
			motElem.addElement("genre").addText(mot.getGenre());

			Element autreGraphieElem = motElem.addElement("autregraphie");
			if (mot.getAutreGraphie() != null) {
				autreGraphieElem.addText(mot.getAutreGraphie());
			}

			motElem.addElement("catgram_precision").addText(mot.getCatgramPrésicion());
			motElem.addElement("islemme").setText(Boolean.toString(mot.isLemme()));

			Element nombreElem = motElem.addElement("nombre");
			if (mot.getNombre() != null) {
				nombreElem.addText(mot.getNombre());
			}

			Element noteElem = motElem.addElement("note");
			if (mot.getNote() != null) {
				noteElem.addText(mot.getNote());
			}

			motElem.addElement("ro").addText(Boolean.toString(mot.isRo()));

			// Ajout des prononciations
			Element prononciationsElem = motElem.addElement("prononciations");
			Set<MotPrononciation> motPrononciations = mot.getMotPrononciations();
			for (MotPrononciation motPrononciation : motPrononciations) {
				Element prononciationElem = prononciationsElem.addElement("prononciation");
				prononciationElem.addElement("api").addText(motPrononciation.getPrononciation().prononciation);
				Element motPrononciationNoteElem = prononciationElem.addElement("note_motPrononciation");
				if (motPrononciation.getNote() != null) {
					motPrononciationNoteElem.addText(motPrononciation.getNote());
				}
			}

		}

		try {
			// affiche(doc, false);
			sauvegarde(doc, motsFichier);
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.info("Fin de l'exportation des mots");
	}

	private void exportePrononciations(File prononciationsFichier) {
		List<Prononciation> prononciations = prononciationRepo.findAll();

		if (prononciationsFichier == null) {
			prononciationsFichier = new File("/tmp/prononciations.xml");
		}

		logger.info("Démarrage de l'exportation des prononciations({})", prononciations.size());

		Document doc = DocumentHelper.createDocument();

		Element root = doc.addElement("prononciations");

		for (Prononciation prononciation : prononciations) {

			List<MotPrononciation> motPrononciations = motPrononciationRepo.findByPrononciation(prononciation);// prononciation.getMotPrononciations();

			if (motPrononciations.size() > 0) {

				Element prononciationElem = root.addElement("prononciation");

				prononciationElem.addElement("prononciation").addText(prononciation.getPrononciation());

				// Ajout des mots
				Element motsElem = prononciationElem.addElement("mots");
				for (MotPrononciation motPrononciation : motPrononciations) {
					Element motElem = motsElem.addElement("mot");
					Mot mot = motPrononciation.getMot();
					motElem.addElement("lemme").addText(mot.getLemme());
					motElem.addElement("mot").addText(mot.getMot());
					motElem.addElement("catgram").addText(mot.getCatgram());
					motElem.addElement("genre").addText(mot.getGenre());
					Element noteMotPrononciationElem = motElem.addElement("note_motPrononciation");
					if (motPrononciation.getNote() != null) {
						noteMotPrononciationElem.addText(motPrononciation.getNote());
					}
				}

			}
		}

		try {
			// affiche(doc, false);
			sauvegarde(doc, prononciationsFichier);
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.info("Fin de l'exportation des prononciations");
	}

	private void ajouteCorpusCatégorieListes(Element root, Corpus corpus) {

		List<CatégorieListe> catégorieListes = catégorieListeRepo.findByCorpus(corpus, new Sort("ordre"));

		Element catégorieListesElem = root.addElement("catégorieListes");
		for (CatégorieListe catégorieListe : catégorieListes) {
			Element catégorieListeElem = catégorieListesElem.addElement("catégorieListe");
			catégorieListeElem.addElement("nom").addText(catégorieListe.getNom());
			catégorieListeElem.addElement("description").addText(catégorieListe.getDescription());
			catégorieListeElem.addElement("ordre").addText(Integer.toString(catégorieListe.getOrdre()));

			// Ajout des listes
			Element listesElem = catégorieListeElem.addElement("listes");
			List<Liste> listes = listeRepo.findByCatégorie(catégorieListe, new Sort("ordre"));
			for (Liste liste : listes) {
				Element listeElem = listesElem.addElement("liste");
				listeElem.addElement("nom").addText(liste.getNom());
				listeElem.addElement("description").addText(liste.getDescription());
				listeElem.addElement("ordre").addText(Integer.toString(liste.getOrdre()));

				// Ajout des mots de la liste. Un mot = lemme + mot + catgram + genre (identifiant unique)
				Element motsElem = listeElem.addElement("mots");
				List<ListeMot> listeMots = listeMotRepo.findByListe(liste);
				for (ListeMot listeMot : listeMots) {
					Element motElem = motsElem.addElement("mot");
					Element noteListeMotElem = motElem.addElement("note_listemot");
					if (listeMot.getNote() != null) {
						noteListeMotElem.addText(listeMot.getNote());
					}
					addMotToElem(motElem, listeMot.getMot());
				}
			}

		}

	}

	private void addMotToElem(Element elem, Mot mot) {
		elem.addElement("lemme").addText(mot.getLemme());
		elem.addElement("mot").addText(mot.getMot());
		elem.addElement("catgram").addText(mot.getCatgram());
		elem.addElement("genre").addText(mot.getGenre());
	}

	private void ajouteCorpusDocMetadonnées(Element root, Corpus corpus) {

		List<DocMetadata> métadonnéesDoc = corpus.getMétadonnéesDoc();

		Element docmétadonnéesElem = root.addElement("docmetadonnées");

		for (DocMetadata docMetadata : métadonnéesDoc) {
			Element docmétadonnéeElem = docmétadonnéesElem.addElement("docmetadonnée");
			docmétadonnéeElem.addElement("nom").addText(docMetadata.getNom());
			
			Element descriptionElem = docmétadonnéeElem.addElement("description");
			if(docMetadata.getDescription() != null)
			{
				descriptionElem.addText(docMetadata.getDescription());
			}
			docmétadonnéeElem.addElement("filtre").addText(Boolean.toString(docMetadata.isFiltre()));
			docmétadonnéeElem.addElement("primaire").addText(Boolean.toString(docMetadata.isPrimaire()));
			docmétadonnéeElem.addElement("champindex").addText(docMetadata.getChampIndex());
			docmétadonnéeElem.addElement("ordre").addText(Integer.toString(docMetadata.getOrdre()));
		}
	}

	private void ajouteCorpusInfo(Element root, Corpus corpus) {

		root.addElement("nom").addText(corpus.getNom());
		root.addElement("description").addText(corpus.getDescription());
		root.addElement("analyseurlexicalfqcn").addText(corpus.getAnalyseurLexicalFQCN());
		root.addElement("analyseurrecherchefqcn").addText(corpus.getAnalyseurRechercheFQCN());
		root.addElement("dossierdata").addText(corpus.getDossierData());
		root.addElement("pardéfaut").addText(Boolean.toString(corpus.isParDéfaut()));

	}

	public CorpusRepository getCorpusRepo() {
		return corpusRepo;
	}

	public void setCorpusRepo(CorpusRepository corpusRepo) {
		this.corpusRepo = corpusRepo;
	}

	public void sauvegarde(Document document, File fichierSortie) throws IOException {

		logger.info("Sauvegarde du document XML {}", fichierSortie);
		XMLWriter writer = new XMLWriter(new FileWriter(fichierSortie), OutputFormat.createPrettyPrint());
		writer.write(document);
		writer.close();

	}

	public void affiche(Document document, boolean compact) throws IOException {

		XMLWriter writer;
		OutputFormat format;

		if (compact) {
			// Affichage compact sur to System.out
			format = OutputFormat.createCompactFormat();
		} else {

			// Affiche le doocument sur System.out
			format = OutputFormat.createPrettyPrint();

		}
		writer = new XMLWriter(System.out, format);
		writer.write(document);

	}

	public static void main(String[] argv) {

		ExportationLigneCommande cmd = new ExportationLigneCommande();

		JCommander jCommander = new JCommander(cmd, argv);

		if (cmd.nomDuCorpus == null && cmd.exportationDesMots && cmd.exportationDesPrononciations) {
			jCommander.usage();
			System.exit(-1);
		}

		Exportation exportation = new Exportation();

		if (cmd.nomDuCorpus != null) {
			exportation.exporteCorpus(cmd.nomDuCorpus, cmd.corpusFichier);
		}

		if (cmd.exportationDesMots) {
			exportation.exporteMots(cmd.motsFichier);
		}

		if (cmd.exportationDesPrononciations) {
			exportation.exportePrononciations(cmd.prononciationsFichier);
		}

	}

}
