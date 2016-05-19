package com.servicelibre.corpus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.servicelibre.entities.corpus.Corpus;
import com.servicelibre.entities.corpus.Liste;

public class ImportationListeAutreMot extends ImportationListe {

	static Logger logger = LoggerFactory.getLogger(ImportationListeAutreMot.class);
	
	public static void main(String[] args) {

		String configLocation = System.getProperty("configLocation", "/system-context.xml");

		ImportationListe importation = new ImportationListeAutreMot();
		importation.setCtx(new ClassPathXmlApplicationContext(configLocation));
		ImportationListe.setConfigLocation(configLocation);

		Corpus corpus = importation.getCorpus("MELS-CJ");
		
		Liste liste = importation.getListe("Autres mots du corpus", "", corpus);
		// Récupérer l'ID de la liste
		logger.debug("Il faut ajouter des mots à la liste {} ({})", liste.getNom(),liste.getId());
		
		// Parcourir le fichier texte, un mot par ligne
		
		// rechercher le ou les mots dans la BD (id)
		
		// insérer via JDBC template dans ListeMot (id liste+id mot)
		
		
		
		
		
		System.out.println(corpus.getDescription());
		
		
	}

}
