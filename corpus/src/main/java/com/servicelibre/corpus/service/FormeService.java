package com.servicelibre.corpus.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.servicelibre.corpus.analysis.CatgramsPivot;
import com.servicelibre.corpus.analysis.LemmeNaturalComparator;
import com.servicelibre.corpus.analysis.MotInfo;
import com.servicelibre.corpus.analysis.MotInfoNaturalComparator;
import com.servicelibre.corpus.liste.LigneSplitter;

public class FormeService {

	private static final int NB_FORMES = 365000;

	private final static Logger logger = LoggerFactory.getLogger(FormeService.class);

	private static final String FORMES_NOMFICHIER = "formes.txt";

	String formesFilePath;
	LigneSplitter ligneSplitter;

	private InputStream formesStream;

	private CatgramsPivot catgramsPivot = new CatgramsPivot();

	private List<List<MotInfo>> motsInfos = new ArrayList<List<MotInfo>>(NB_FORMES);
	private List<String> mots = new ArrayList<String>(NB_FORMES);

	private List<List<MotInfo>> lemmesInfos = new ArrayList<List<MotInfo>>(NB_FORMES);
	private List<String> lemmes = new ArrayList<String>(NB_FORMES);

	private MotInfoNaturalComparator<MotInfo> motComp = new MotInfoNaturalComparator<MotInfo>();
	private LemmeNaturalComparator<MotInfo> lemmeComp = new LemmeNaturalComparator<MotInfo>();

	public FormeService() {
		super();
	}

	public FormeService(String formesFilePath) {
		super();
		this.formesFilePath = formesFilePath;
	}

	/**
	 * Retourne toutes les informations disponibles pour un mot donné (éventuellement plusieurs lemmes différents)
	 * @param mot
	 * @return
	 */
	public List<MotInfo> getMotInfo(String mot) {
		List<MotInfo> info = new ArrayList<MotInfo>();

		// Recherche du mot dans les formes triées
		int idx = Collections.binarySearch(mots, mot);

		// renvoyer toutes les occurences (candidats possibles)
		if (idx >= 0) {
			for (MotInfo motInfo : motsInfos.get(idx)) {
					info.add(new MotInfo(motInfo.getMot(), motInfo.getLemme(), motInfo.getCatgram(), motInfo.getNote(), motInfo
							.getFreqMot(), motInfo.getFreqLemme(), motInfo.isLemme(), motInfo.getGenre(), motInfo.getNombre(), motInfo.getPersonne()));
			}
		}

		return info;

	}

	/**
	 * Retourner tous les graphies uniques des lemmes possibles d'un mot (forme) donné.
	 * @param mot
	 * @return
	 */
	public List<String> getLemmes(String mot) {
		Set<String> info = new HashSet<String>();

		// Recherche du mot dans les formes triées
		int idx = Collections.binarySearch(mots, mot);

		// renvoyer toutes les occurences (candidats possibles)
		if (idx >= 0) {
			for (MotInfo motInfo : motsInfos.get(idx)) {
				info.add(motInfo.getLemme());
			}
		}

		return new ArrayList<String>(info);

	}

	/**
	 * Retourne toutes les formes d'un lemme donné
	 * @param lemme
	 * @return
	 */
	public List<MotInfo> getFormesMotInfo(String lemme) {
		List<MotInfo> info = new ArrayList<MotInfo>();

		// Recherche du lemme dans les formes triées
		int idx = Collections.binarySearch(lemmes, lemme.toLowerCase());

		// renvoyer toutes les occurences (candidats possibles)
		if (idx >= 0) {
			for (MotInfo motInfo : lemmesInfos.get(idx)) {

				info.add(new MotInfo(motInfo.getMot(), motInfo.getLemme(), motInfo.getCatgram(), motInfo.getNote(), motInfo.getFreqMot(),
						motInfo.getFreqLemme(), motInfo.isLemme(), motInfo.getGenre(), motInfo.getNombre(), motInfo.getPersonne()));
			}
		}

		return info;

	}

	public List<String> getFormes(String lemme) {
		Set<String> info = new HashSet<String>();

		// Recherche du mot dans les formes triées
		int idx = Collections.binarySearch(lemmes, lemme.toLowerCase());

		// renvoyer toutes les occurences (candidats possibles)
		if (idx >= 0) {
			for (MotInfo motInfo : lemmesInfos.get(idx)) {
				info.add(motInfo.getMot());
			}
		}

		return new ArrayList<String>(info);

	}

	public boolean isLemme(String lemme) {
		return false;
	}

	public void init() {
		// Chargement du fichier des formes
		chargementFormes();

	}

	private void chargementFormes() {
		// Format : mot|lemme|catgram
		List<MotInfo> formesTemp = new ArrayList<MotInfo>(NB_FORMES);
		try {

			// Si chemin spécifique vers fichier des formes donnés, utiliser ce fichier
			if (formesFilePath != null && !formesFilePath.isEmpty()) {
				File formesFile = new File(formesFilePath);
				if (formesFile.exists()) {
					formesStream = new FileInputStream(formesFile);
					logger.debug("Chargement des formes depuis formesFilePath ({})", formesFilePath);
				} else {
					logger.error("Le fichier des formes {} n'existe pas.", formesFilePath);
					return;
				}
			} else {
				// Recherche de la ressource par défaut dans le classpath
				formesStream = this.getClass().getClassLoader().getResourceAsStream(FORMES_NOMFICHIER);
				if (formesStream != null) {
					logger.debug("Chargement des formes depuis le fichier {} trouvé dans le classpath.", FORMES_NOMFICHIER);
				} else {
					logger.error("Le fichier des formes {} n'existe pas dans le classpath.", FORMES_NOMFICHIER);
				}
			}

			// Chargement du fichier des formes dans une table temporaire
			BufferedReader in = new BufferedReader(new InputStreamReader(formesStream, "UTF-8"));
			String line = null;
			while ((line = in.readLine()) != null) {

				// FIXME utiliser le ligneSplitter !
				// ligneSplitter.splitLigne(line).get(0);
				String[] formeInfo = line.split("\\|");

				String mot = formeInfo[0];
				String lemme = formeInfo[1];
				String catgram = formeInfo[2];

				Double freqMot = Double.parseDouble(formeInfo[3]);
				Double freqLemme = Double.parseDouble(formeInfo[4]);

				boolean isLemme = Boolean.parseBoolean(formeInfo[5]);

				String note = formeInfo[6];

				String genre = formeInfo[8];
				String nombre = formeInfo[9];

				String personne = formeInfo.length > 10 ? formeInfo[10] : "";

				formesTemp.add(new MotInfo(mot, lemme, catgramsPivot.getCatgramFromId(catgram), note, freqMot, freqLemme, isLemme, genre,
						nombre, personne));
			}
			in.close();
			formesStream.close();
		} catch (IOException e) {
			logger.error("Erreur lors du chargement des formes depuis le fichier {} (classpath ou fichier).", FORMES_NOMFICHIER + " ou "
					+ formesFilePath, e);
			return;
		}

		int formesSize = formesTemp.size();

		// Tri des formes sur base du mot
		logger.info("Tri des {} formes...", formesSize);
		Collections.sort(formesTemp, motComp);
		logger.info("Tri des {} formes terminé.", formesSize);

		// Remplissage des 2 tables nécessaires au traitement des formes
		int pos = 0;
		String nouveauMot = formesTemp.get(pos).mot;
		while (pos < formesSize) {
			List<MotInfo> lemmesPossibles = new ArrayList<MotInfo>(1);
			// Tant que le mot est identique et qu'on est pas à la fin du tableau,
			// construction de la liste des formes/lemmes possibles
			while (pos < formesSize && nouveauMot.equals(formesTemp.get(pos).mot)) {
				lemmesPossibles.add(formesTemp.get(pos));
				pos++;
			}

			mots.add(nouveauMot);
			motsInfos.add(lemmesPossibles);

			if (pos < formesSize) {
				nouveauMot = formesTemp.get(pos).mot;
			}

		}

		// Tri des formes sur base du lemme
		logger.info("Tri des {} formes sur base des lemmes...", formesSize);
		Collections.sort(formesTemp, lemmeComp);
		logger.info("Tri des {} formes sur base des lemmes terminé.", formesSize);

		// Remplissage des 2 tables nécessaires au traitement des lemmes
		pos = 0;
		String nouveauLemme = formesTemp.get(pos).lemme;
		while (pos < formesSize) {
			List<MotInfo> formesPossibles = new ArrayList<MotInfo>(1);
			// Tant que le mot est identique et qu'on est pas à la fin du tableau,
			// construction de la liste des formes/lemmes possibles
			while (pos < formesSize && nouveauLemme.equals(formesTemp.get(pos).lemme)) {
				formesPossibles.add(formesTemp.get(pos));
				pos++;
			}

			lemmes.add(nouveauLemme);
			lemmesInfos.add(formesPossibles);

			if (pos < formesSize) {
				nouveauLemme = formesTemp.get(pos).lemme;
			}

		}

		sauvegardeListe(mots, "/tmp/formes-tri.txt");
		sauvegardeListeMotInfo(motsInfos, "/tmp/motsInfo.txt");

	}

	public void sauvegardeListe(List<String> motInfoList, String dumpFilename) {
		File dumpFile = new File(dumpFilename);
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dumpFile), "UTF-8"));
			for (String string : motInfoList) {
				writer.append(string);
				writer.newLine();
			}
			writer.close();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void sauvegardeListeMotInfo(List<List<MotInfo>> motInfoList, String dumpFilename) {
		File dumpFile = new File(dumpFilename);
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dumpFile), "UTF-8"));
			int cpt = 0;
			for (List<MotInfo> list : motInfoList) {
				writer.append(cpt++ + "");
				writer.newLine();		
				
				for (MotInfo motInfo : list) {
					writer.append(motInfo.toString());
					writer.newLine();
				}
			}
			writer.close();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	

	public String getFormesFilePath() {
		return formesFilePath;
	}

	public void setFormesFilePath(String formesFilePath) {
		this.formesFilePath = formesFilePath;
	}
	
	public boolean isInitialisé() {
		return mots.size() > 0 && lemmes.size() > 0;
	}

}
