package com.servicelibre.corpus.liste;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.entity.Corpus;
import com.servicelibre.corpus.entity.Liste;
import com.servicelibre.corpus.entity.Mot;
import com.servicelibre.corpus.manager.CorpusManager;
import com.servicelibre.corpus.manager.ListeManager;
import com.servicelibre.corpus.manager.MotManager;

/**
 * Outil d'importation de listes
 * 
 * @author benoitm
 * 
 */
public class ListeImport {

	private static Logger logger = LoggerFactory.getLogger(ListeImport.class);

	public enum Action {
		REMPLACER, AJOUTER
	};

	private List<Liste> listes;

	private ApplicationContext ctx;
	
	private LigneSplitter ligneSplitter;
	
	private Corpus corpus;
	
	private boolean simulation = true;
	
	private Map<String, Liste> listesCache = new HashMap<String, Liste>();

	@Transactional
	public int execute(Liste infoListe) {

		logger.info("Exécution de l'importation de la liste {}", infoListe);

		File fichierSource = infoListe.getFichierSource();
		// Chargement du fichier en liste
		if (fichierSource != null && fichierSource.exists()) {

			MotManager mm = (MotManager) ctx.getBean("motManager");

			// Suppression des mots qui existeraient déjà pour cette liste
			// logger.info("Suppression des mots de la liste {}.",
			// currentListe);
			// int deleteCount = mm.removeAllFrom(currentListe);

			try {
				List<String> lignes = FileUtils.readLines(fichierSource);

				int cptMotAjouté = 0;
				int cptNouvelleÉtiquette = 0;
				for (String ligne : lignes) {
					// Ignore les lignes vides
					if (ligne.isEmpty()) {
						continue;
					}

					List<Mot> mots = ligneSplitter.splitLigne(ligne);
					
					Liste liste = mots.get(0).getListe();
					if(liste == null || liste.getNom() == null) {
						liste = getOrCreateListe(infoListe);
					}
					else {
						liste = getOrCreateListe(liste);
					}
					
					if (liste == null) {
						return -1;
					}
					
					for (Mot mot : mots) {
						logger.info("Traitement du mot [{}]", mot.getMot());
						Mot motCourant = mm.findByMot(mot.lemme, mot.getMot(),
								mot.getCatgram(), mot.getGenre());
						
						// Le mot existe-t-il déjà?
						if (motCourant == null) {
							// Ajouter un nouveau mot
							cptMotAjouté++;

							if (!simulation) {
								logger.info("Ajout du mot [{}]", mot.lemme);
								// Définition de la liste primaire du mot, si la liste est primaire
								if(liste.isListesPrimaire()) {
									mot.setListe(liste);
								}
								else {
									mot.setListe(null);
								}
								
								mm.save(mot);
							}
							else {
								logger.info("Simulation de l'ajout du mot [{}]", mot.lemme);
							}

							motCourant = mot;
						}

						if (!simulation) {
							// Le mot est présent dans la base, lui associer sa liste
							logger.info("Ajout de l'étiquette (liste) [{}] au mot [{}]", liste.getNom(), mot.lemme);
							motCourant.ajouteListe(liste);
						}
						else {
							logger.info("Simulation de l'ajout de l'étiquette (liste) [{}] au mot [{}]", liste.getNom(), mot.lemme);
						}
						
						cptNouvelleÉtiquette++;
					}
				}

				logger.info(
						"{} mots ont été ajoutés à la liste {}. {} nouvelles étiquettes.",
						new Object[] { cptMotAjouté, infoListe,
								cptNouvelleÉtiquette });

			} catch (IOException e) {
				logger.error(
						"Erreur lors de la lecture de la liste des mots {}",
						fichierSource, e);
			}

		} else {
			logger.error("Fichier null ou inexistant: {}", fichierSource);
		}

		return 0;
	}

	public Liste getOrCreateListe(Liste liste) {

		ListeManager lm = (ListeManager) ctx.getBean("listeManager");

		if (liste == null) {
			logger.error("Pour importer une liste, il faut préciser cette liste!");
			return null;
		}

		// Est-ce que la liste existe déjà?
		Liste dbListe = null;
		
		// En cache?
		dbListe = listesCache.get(liste.getNom());
		
		if (dbListe == null) {
			// Dans la DB ?
			dbListe = lm.findByNom(liste.getNom());
			if (dbListe != null) {
				logger.info("La liste {} a été trouvé dans la base de données.",dbListe);
				System.err.println("Chargement de la liste en cache: " + dbListe.getNom());
				listesCache.put(dbListe.getNom(), dbListe);
			}
		}

		if (dbListe == null) {
			
			logger.info("Création de la liste {} dans la base de données.",
					liste);
			
			liste.setCorpus(getOrCreateCorpus(this.corpus));

			// récupération de l'ordre le plus élevé
			int maxOrdre = lm.findMaxOrdre();
			liste.setOrdre(maxOrdre + 10);

			
			if(simulation) {
				logger.info("Simulation de la sauvegarde de la liste {} dans la base de données.", liste);
			}
			else {
				logger.info("Création de la liste {} dans la base de données.",
						liste);
				lm.save(liste);
			}
			
			liste.setListesPrimaire(liste.isListesPrimaire());

			listesCache.put(liste.getNom(), dbListe);
			
			return liste;
			
		} else {
			// récupération des champs transient éventuels
			dbListe.setFichierSource(liste.getFichierSource());
			dbListe.setFichierEncoding(liste.getFichierEncoding());
			dbListe.setListesPrimaire(liste.isListesPrimaire());
			return dbListe;
		}

	}

	private Corpus getOrCreateCorpus(Corpus corpus) {
		CorpusManager cm = (CorpusManager) ctx.getBean("corpusManager");

		if (corpus == null || corpus.getNom().isEmpty()) {
			logger.error("Il faut préciser un corpus!");
			return null;
		}

		// Est-ce que le corpus existe-déjà?
		Corpus dbCorpus = cm.findByNom(corpus.getNom());

		if (dbCorpus == null) {
			if(simulation) {
				logger.info("Simulation de la création du corpus {} dans la base de données.",
						corpus);
			}
			else {
				cm.save(corpus);
				logger.info("Création du corpus {} dans la base de données.",
						corpus);
			}
			return corpus;
		} else {
			logger.info("Le corpus {} a été trouvé dans la base de données.",
					corpus);
			return dbCorpus;
		}

	}

	public List<Liste> getListes() {
		return listes;
	}

	public void setListes(List<Liste> listes) {
		this.listes = listes;
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.ctx = applicationContext;

	}

	public Corpus getCorpus() {
		return corpus;
	}

	public void setCorpus(Corpus corpus) {
		this.corpus = corpus;
	}

	public LigneSplitter getLigneSplitter() {
		return ligneSplitter;
	}

	public void setLigneSplitter(LigneSplitter ligneSplitter) {
		this.ligneSplitter = ligneSplitter;
	}

	public boolean isSimulation() {
		return simulation;
	}

	public void setSimulation(boolean simulation) {
		this.simulation = simulation;
	}

	
}
