package com.servicelibre.corpus.prononciation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.servicelibre.repositories.corpus.MotRepository;

/**
 * Outil d'importation des prononcistions
 * 
 * format des lignes du fichier:
 * 
 * mot\tprononciation\tnomFichier
 * 
 * @author benoitm
 * 
 */

public class PrononciationImport {

	private static Logger logger = LoggerFactory.getLogger(PrononciationImport.class);

	private static final String SÉPARATEUR = "\\t";

	@Autowired
	MotRepository motRepository;

	public int execute(Reader in) {

		BufferedReader reader = new BufferedReader(in);
		String ligne = "";

		try {

			int prononciationCpt = 0;
			int liaisonCpt = 0;
			
			while ((ligne = reader.readLine()) != null) {
				String[] cols = ligne.split(SÉPARATEUR);

				liaisonCpt += motRepository.ajoutePrononciation(cols[0], cols[1]);

				prononciationCpt++;

				if (prononciationCpt % 1000 == 0) {
					logger.info("{} prononciations déjà importées...", prononciationCpt);
				}
				
			}
			
			logger.info("{} prononciations ont été ajoutées à la table des prononciations. {} liaisons avec des mots.", prononciationCpt, liaisonCpt);
			
		} catch (IOException e) {
			logger.error("Erreur lors de la lecture de la liste des prononciations",  e);
		}

		return 0;
	}

	public int execute(File fichierSource) {

		logger.info("Exécution de l'importation des prononciations");

		// Chargement du fichier en liste de lignes
		if (fichierSource != null && fichierSource.exists()) {
				
				try {
					return execute(new FileReader(fichierSource));
				} catch (FileNotFoundException e) {
					logger.error("Erreur lors de la lecture de la liste des prononciations {}", fichierSource, e);
				}

		} else {
			logger.error("Fichier null ou inexistant: {}", fichierSource);
		}

		return 0;
	}

	public MotRepository getMotRepository() {
		return motRepository;
	}

	public void setMotRepository(MotRepository motRepository) {
		this.motRepository = motRepository;
	}

}
