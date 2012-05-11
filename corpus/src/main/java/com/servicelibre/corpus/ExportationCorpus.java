package com.servicelibre.corpus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.beust.jcommander.JCommander;
import com.servicelibre.corpus.entity.Corpus;
import com.servicelibre.corpus.entity.DocMetadata;
import com.servicelibre.corpus.repository.CorpusRepository;

public class ExportationCorpus {

	private static Logger logger = LoggerFactory.getLogger(ExportationCorpus.class);

	ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "application-context.xml", "system-context.xml" });

	CorpusRepository corpusRepo = (CorpusRepository) context.getBean("corpusRepository");

	public void exporteCorpus(String nomDuCorpus) {

		// Recherche du corpus à exporter au format XML
		Corpus corpus = corpusRepo.findByNom(nomDuCorpus);

		if (corpus == null) {
			logger.error("Le corpus « {} » n'existe pas dans la base de données.  Annulation de l'exportation.", nomDuCorpus);
			System.exit(-1);
		}

		logger.info("Démarrage de l'exportation du corpus « {} ». ", nomDuCorpus);

		Document doc = DocumentHelper.createDocument();

		Element root = doc.addElement("corpus");

		ajouteCorpusInfo(root, corpus);
		
		ajouteCorpusDocMetadonnées(root, corpus);
		
		ajouteCorpusCatégorieListes(root, corpus);

		

		
		try {
			affiche(doc, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		logger.info("Fin de l'exportation du corpus « {} ». ", nomDuCorpus);

	}


	private void ajouteCorpusCatégorieListes(Element root, Corpus corpus) {
		//corpus.
		
	}


	private void ajouteCorpusDocMetadonnées(Element root, Corpus corpus) {
		
		List<DocMetadata> métadonnéesDoc = corpus.getMétadonnéesDoc();
		
		Element docmétadonnées = root.addElement("docmetadonnées");
		
		for (DocMetadata docMetadata : métadonnéesDoc) {
			Element docmétadonnée = docmétadonnées.addElement("docmetadonnée");
			docmétadonnée.addElement("nom").addText(docMetadata.getNom());
			docmétadonnée.addElement("champindex").addText(docMetadata.getChampIndex());
			docmétadonnée.addElement("ordre").addText(Integer.toString(docMetadata.getOrdre()));
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
		
		XMLWriter writer = new XMLWriter(new FileWriter(fichierSortie));
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

		if (cmd.nomDuCorpus == null) {
			jCommander.usage();
			System.exit(-1);
		}

		ExportationCorpus exportation = new ExportationCorpus();
		exportation.exporteCorpus(cmd.nomDuCorpus);

	}

}
