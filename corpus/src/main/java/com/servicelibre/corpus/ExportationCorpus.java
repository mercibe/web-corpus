package com.servicelibre.corpus;

import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.beust.jcommander.JCommander;
import com.servicelibre.corpus.entity.Corpus;
import com.servicelibre.corpus.repository.CorpusRepository;


public class ExportationCorpus {
	
	private static Logger logger = LoggerFactory.getLogger(ExportationCorpus.class);
	
	ApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"application-context.xml", "system-context.xml"});
	
	CorpusRepository corpusRepo = (CorpusRepository) context.getBean("corpusRepository");
	
	public void exporteCorpus(String nomDuCorpus) {

		
		// Recherche du corpus à exporter au format XML
		Corpus corpus = corpusRepo.findByNom(nomDuCorpus);
		
		if(corpus == null) {
			logger.error("Le corpus « {} » n'existe pas dans la base de données.  Annulation de l'exportation.", nomDuCorpus);
			System.exit(-1);
		}
		
		logger.info("Démarrage de l'exportation du corpus « {} ». ", nomDuCorpus);
		
		Document doc = DocumentHelper.createDocument();
		
		Element root = doc.addElement("corpus");
		
		
		
		
	}
	
	
	public CorpusRepository getCorpusRepo() {
		return corpusRepo;
	}

	public void setCorpusRepo(CorpusRepository corpusRepo) {
		this.corpusRepo = corpusRepo;
	}




	public static void main(String[] argv) {
		
		ExportationLigneCommande cmd = new ExportationLigneCommande();
		
		JCommander jCommander = new JCommander(cmd, argv);
		
		if(cmd.nomDuCorpus == null) {
			jCommander.usage();
			System.exit(-1);
		}
		
		ExportationCorpus exportation = new ExportationCorpus();
		exportation.exporteCorpus(cmd.nomDuCorpus);
		
		
	}


	
	
	
}
