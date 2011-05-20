package com.servicelibre.corpus.liste;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.manager.CorpusManager;
import com.servicelibre.corpus.manager.ListeManager;
import com.servicelibre.corpus.manager.MotManager;

/**
 * Outil d'importation de listes
 * 
 * @author benoitm
 * 
 */
public class ListeImport{
	
	private static Logger logger = LoggerFactory.getLogger(ListeImport.class);

	private List<Liste> listes;

	private ApplicationContext ctx;

	@Transactional
	public int execute(Liste currentListe) {

		logger.info("Exécution de l'importation de la liste {}", currentListe);

		getOrCreateCorpus(currentListe);

		if (currentListe.corpus == null) {
			return -1;
		}

		currentListe = getOrCreateListe(currentListe);

		if (currentListe == null) {
			return -1;
		}

		LigneSplitter splitter = currentListe.getLigneSplitter();

		File fichierSource = currentListe.getFichierSource();
		// Chargement du fichier en liste
		if (fichierSource != null && fichierSource.exists()) {

			MotManager mm = (MotManager) ctx.getBean("motManager");

			// Suppression des mots qui existeraient déjà pour cette liste
			int deleteCount = mm.deleteFromListe(currentListe);
			logger.info("{} mots ont été supprimés de la liste {}.", deleteCount, currentListe);

			try {
				List<String> lignes = FileUtils.readLines(fichierSource);

				int cptMot = 0;
				for (String ligne : lignes) {
					List<Mot> mots = splitter.splitLigne(ligne, currentListe);
					for (Mot mot : mots) {
						mm.save(mot);
						cptMot++;
					}
				}

				logger.info("{} mots ont été ajoutés à la liste {}.", cptMot, currentListe);

			} catch (IOException e) {
				logger.error("Erreur lors de la lecture de la liste des mots {}", fichierSource, e);
			}

		} else {
			logger.error("Fichier null ou inexistant: {}", fichierSource);
		}

		return 0;
	}

	private Liste getOrCreateListe(Liste currentListe) {

		ListeManager lm = (ListeManager) ctx.getBean("listeManager");

		if (currentListe == null) {
			logger.error("Pour importer une liste, il faut préciser cette liste!");
			return null;
		}

		// Est-ce que la liste existe déjà?
		Liste dbListe = lm.findByNom(currentListe.getNom());

		if (dbListe == null) {
			logger.info("Création de la liste {} dans la base de données.", currentListe);
			lm.save(currentListe);
			return currentListe;
		} else {

			// récupération des champs transient éventuels
			dbListe.setFichierSource(currentListe.getFichierSource());
			dbListe.setLigneSplitter(currentListe.getLigneSplitter());
			logger.info("La liste {} a été trouvé dans la base de données.", currentListe);
			return dbListe;
		}

	}

	private void getOrCreateCorpus(Liste currentListe) {
		CorpusManager cm = (CorpusManager) ctx.getBean("corpusManager");

		if (currentListe.corpus == null) {
			logger.error("Pour importer une liste, il faut préciser son corpus!");
			return;
		}

		// Est-ce que le corpus existe-déjà?
		Corpus dbCorpus = cm.findByNom(currentListe.corpus.getNom());

		if (dbCorpus == null) {
			cm.save(currentListe.corpus);
			logger.info("Création du corpus {} dans la base de données.", currentListe.corpus);
		} else {
			currentListe.corpus = dbCorpus;
			logger.info("Le corpus {} a été trouvé dans la base de données.", currentListe.corpus);
		}

	}

	

	public List<Liste> getListes() {
		return listes;
	}

	public void setListes(List<Liste> listes) {
		this.listes = listes;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;
		
	}


}
