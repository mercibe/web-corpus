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

import com.servicelibre.entities.corpus.CatégorieListe;
import com.servicelibre.entities.corpus.Corpus;
import com.servicelibre.entities.corpus.Liste;
import com.servicelibre.entities.corpus.ListeMot;
import com.servicelibre.entities.corpus.Mot;
import com.servicelibre.repositories.corpus.CatégorieListeRepository;
import com.servicelibre.repositories.corpus.CorpusRepository;
import com.servicelibre.repositories.corpus.ListeMotRepository;
import com.servicelibre.repositories.corpus.ListeRepository;
import com.servicelibre.repositories.corpus.MotRepository;

/**
 * Outil d'importation de listes
 * 
 * @author benoitm
 * 
 */
@Deprecated
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

	private CatégorieListe catégorieImportation;

	public int execute(Liste infoListe) {

		logger.info("Exécution de l'importation de la liste {}", infoListe);

		File fichierSource = infoListe.getFichierSource();
		// Chargement du fichier en liste
		if (fichierSource != null && fichierSource.exists()) {

			MotRepository motRepo = (MotRepository) ctx.getBean("motRepository");
			ListeMotRepository listeMotRepo = (ListeMotRepository) ctx.getBean("listeMotRepository");

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

					Liste liste = getOrCreateListe(infoListe);

					if (liste == null) {
						return -1;
					}

					for (Mot mot : mots) {
						logger.info("Traitement du mot [{}]", mot.getMot());
						Mot motCourant = motRepo.findByLemmeAndMotAndCatgramAndGenre(mot.lemme, mot.getMot(), mot.getCatgram(),
								mot.getGenre());

						// Le mot existe-t-il déjà?
						if (motCourant == null) {
							// Ajouter un nouveau mot
							cptMotAjouté++;

							if (!simulation) {
								logger.info("Ajout du mot [{}]", mot.lemme);
							
								mot = motRepo.save(mot);
							} else {
								logger.info("Simulation de l'ajout du mot [{}]", mot.lemme);
							}

							motCourant = mot;
						}

						if (!simulation) {
							// Le mot est présent dans la base, lui associer sa liste
							logger.info("Ajout de l'étiquette (liste) [{}] au mot [{}]", liste.getNom(), mot.lemme);
							ListeMot listeMot = new ListeMot(motCourant, liste);
							listeMot = listeMotRepo.save(listeMot);
							// motCourant.ajouteListe(liste);
						} else {
							logger.info("Simulation de l'ajout de l'étiquette (liste) [{}] au mot [{}]", liste.getNom(), mot.lemme);
						}

						cptNouvelleÉtiquette++;
					}
				}

				logger.info("{} mots ont été ajoutés à la liste {}. {} nouvelles étiquettes.", new Object[] { cptMotAjouté, infoListe,
						cptNouvelleÉtiquette });

			} catch (IOException e) {
				logger.error("Erreur lors de la lecture de la liste des mots {}", fichierSource, e);
			}

		} else {
			logger.error("Fichier null ou inexistant: {}", fichierSource);
		}

		return 0;
	}

	public Liste getOrCreateListe(Liste liste) {

		ListeRepository listeRepository = (ListeRepository) ctx.getBean("listeRepository");

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
			dbListe = listeRepository.findByNom(liste.getNom());
			if (dbListe != null) {
				logger.info("La liste {} a été trouvé dans la base de données.", dbListe);
				System.err.println("Chargement de la liste en cache: " + dbListe.getNom());
				listesCache.put(dbListe.getNom(), dbListe);
			}
		}

		if (dbListe == null) {

			logger.info("Création de la liste {} dans la base de données.", liste);

			Corpus createCorpus = getOrCreateCorpus(this.corpus);
			this.catégorieImportation.setCorpus(createCorpus);

			catégorieImportation = getOrCreateCatégorieListe(this.catégorieImportation);
			liste.setCatégorieListe(catégorieImportation);

			// récupération de l'ordre le plus élevé
			Number maxOrdre = listeRepository.findMaxOrdre();
			int ordre = maxOrdre == null ? 0 : maxOrdre.intValue();

			liste.setOrdre(ordre + 10);

			if (simulation) {
				logger.info("Simulation de la sauvegarde de la liste {} dans la base de données.", liste);
			} else {
				logger.info("Création de la liste {} dans la base de données.", liste);
				liste = listeRepository.save(liste);
			}

			listesCache.put(liste.getNom(), dbListe);

			return liste;

		} else {
			// récupération des champs transient éventuels
			dbListe.setFichierSource(liste.getFichierSource());
			dbListe.setFichierEncoding(liste.getFichierEncoding());
			return dbListe;
		}

	}

	@Transactional
	private Corpus getOrCreateCorpus(Corpus corpus) {
		CorpusRepository corpusRepo = (CorpusRepository) ctx.getBean("corpusRepository");

		if (corpus == null || corpus.getNom().isEmpty()) {
			logger.error("Il faut préciser un corpus!");
			return null;
		}

		// Est-ce que le corpus existe-déjà?
		Corpus dbCorpus = corpusRepo.findByNom(corpus.getNom());

		if (dbCorpus == null) {
			if (simulation) {
				logger.info("Simulation de la création du corpus {} dans la base de données.", corpus);
			} else {
				corpus = corpusRepo.save(corpus);
				logger.info("Création du corpus {} dans la base de données.", corpus);
			}
			return corpus;
		} else {
			logger.info("Le corpus {} a été trouvé dans la base de données.", corpus);
			return dbCorpus;
		}

	}

	@Transactional
	private CatégorieListe getOrCreateCatégorieListe(CatégorieListe catégorie) {
		CatégorieListeRepository catégoreListeRepo = (CatégorieListeRepository) ctx.getBean("catégorieListeRepository");

		if (catégorie == null || catégorie.getNom().isEmpty()) {
			logger.error("Il faut préciser une catégorie!");
			return null;
		}

		// Est-ce que la catégorie existe-déjà?
		CatégorieListe dbCatégorieListe = catégoreListeRepo.findByNom(catégorie.getNom());

		if (dbCatégorieListe == null) {
			if (simulation) {
				logger.info("Simulation de la création de la catégorie {} dans la base de données.", catégorie);
			} else {
				catégorie = catégoreListeRepo.save(catégorie);
				logger.info("Création de la catégorie {} dans la base de données.", catégorie);
			}
			return catégorie;
		} else {
			logger.info("La catégorie {} a été trouvée dans la base de données.", catégorie);
			return dbCatégorieListe;
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

	public CatégorieListe getCatégorieImportation() {
		return catégorieImportation;
	}

	public void setCatégorieImportation(CatégorieListe catégorieImportation) {
		this.catégorieImportation = catégorieImportation;
	}

}
