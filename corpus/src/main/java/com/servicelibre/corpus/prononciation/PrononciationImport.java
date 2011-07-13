package com.servicelibre.corpus.prononciation;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.persistence.NoResultException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.entity.Mot;
import com.servicelibre.corpus.entity.Prononciation;
import com.servicelibre.corpus.manager.MotManager;
import com.servicelibre.corpus.manager.PrononciationManager;

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
    MotManager motManager;

    @Autowired
    PrononciationManager prononciationManager;

    public int execute(File fichierSource) {

	logger.info("Exécution de l'importation des prononciations");

	// Chargement du fichier en liste de lignes
	if (fichierSource != null && fichierSource.exists()) {

	    try {
		List<String> lignes = FileUtils.readLines(fichierSource);

		int prononciationCpt = 0;
		int liaisonCpt = 0;

		for (String ligne : lignes) {

		    String[] cols = ligne.split(SÉPARATEUR);

		   

		    liaisonCpt += sauve(cols);

		    prononciationCpt++;

		    if (prononciationCpt % 1000 == 0) {
			logger.info("{} prononciations déjà importées...", prononciationCpt);
		    }
		}

		logger.info("{} prononciations ont été ajoutées à la table des prononciations. {} liaisons avec des mots.", prononciationCpt, liaisonCpt);

	    } catch (IOException e) {
		logger.error("Erreur lors de la lecture de la liste des mots {}", fichierSource, e);
	    }

	} else {
	    logger.error("Fichier null ou inexistant: {}", fichierSource);
	}

	return 0;
    }

    @Transactional
    private int sauve(String[] cols) {

	int liaisonCpt = 0;
		
	Prononciation prononciation = null;
	
	prononciation = prononciationManager.findByPrononciation(cols[1]);
	
	if(prononciation == null) {
	    prononciation = new Prononciation(cols[1]);
	}
	
	logger.debug("importation de la prononciation {}", prononciation.prononciation);
	prononciationManager.save(prononciation);
	
	// Rechercher le/les éventuels mot/forme associés et lier
	List<Mot> mots = motManager.findByMot(cols[0]);
	for (Mot mot : mots) {
	    mot.ajoutePrononciation(prononciation);
	    logger.debug("liaison de la prononciation {} à la forme {}", prononciation.prononciation, mot);
	    motManager.save(mot);
	    liaisonCpt++;
	}
	
	
	return liaisonCpt;
    }

}
