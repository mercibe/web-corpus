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
import java.util.Comparator;
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

public class PrononciationService {

	private static final int NB_PRONONCIATIONS = 60000;

	private final static Logger logger = LoggerFactory.getLogger(PrononciationService.class);

	private static final String PRONONCIATIONS_NOMFICHIER = "prononciations.txt";

	String prononciationsFilePath;

	private InputStream prononciationsStream;

	private List<Set<String>> prononciationsInfos = new ArrayList<Set<String>>(NB_PRONONCIATIONS);
	private List<String> graphies = new ArrayList<String>(NB_PRONONCIATIONS);

	
	public PrononciationService() {
		super();
	}

	public PrononciationService(String prononciationsFilePath) {
		super();
		this.prononciationsFilePath = prononciationsFilePath;
	}

	public List<String> getGraphiePrononciations(String graphie) {
		List<String> motPrononciations = new ArrayList<String>();

		// Recherche du mot dans les prononciations triées
		int idx = Collections.binarySearch(graphies, graphie);

		System.out.println("Trouvé " + idx);
		
		// renvoyer toutes les prononciations associées
		if (idx >= 0) {
			for (String prononciation : prononciationsInfos.get(idx)) {

					motPrononciations.add(prononciation);
				}
			}
		return motPrononciations;

	}

	public void init() {
		// Chargement du fichier des formes
		chargementPrononciations();

	}

	private void chargementPrononciations() {
		// Format : graphie\tprononciation
		List<GraphiePrononciation> prononciationsTemp = new ArrayList<GraphiePrononciation>(NB_PRONONCIATIONS);
		try {

			// Si chemin spécifique vers fichier des prononciations donné, utiliser ce fichier
			if (prononciationsFilePath != null && !prononciationsFilePath.isEmpty()) {
				File prononciationsFile = new File(prononciationsFilePath);
				if (prononciationsFile.exists()) {
					prononciationsStream = new FileInputStream(prononciationsFile);
					logger.debug("Chargement des prononciations depuis prononciationsFilePath ({})", prononciationsFilePath);
				} else {
					logger.error("Le fichier des prononciations {} n'existe pas.", prononciationsFilePath);
					return;
				}
			} else {
				// Recherche de la ressource par défaut dans le classpath
				prononciationsStream = this.getClass().getClassLoader().getResourceAsStream(PRONONCIATIONS_NOMFICHIER);
				if (prononciationsStream != null) {
					logger.debug("Chargement des prononciations depuis le fichier {} trouvé dans le classpath.", PRONONCIATIONS_NOMFICHIER);
				} else {
					logger.error("Le fichier des prononciations {} n'existe pas dans le classpath.", PRONONCIATIONS_NOMFICHIER);
				}
			}

			// Chargement du fichier des prononciations dans une table temporaire
			BufferedReader in = new BufferedReader(new InputStreamReader(prononciationsStream, "UTF-8"));
			String line = null;
			while ((line = in.readLine()) != null) {

				
				String[] formeInfo = line.split("\t");

				String graphie = formeInfo[0];
				String prononciation = formeInfo[1];

				prononciationsTemp.add(new GraphiePrononciation(graphie, prononciation));
			}
			in.close();
			prononciationsStream.close();
		} catch (IOException e) {
			logger.error("Erreur lors du chargement des prononciations depuis le fichier {} (classpath ou fichier).", PRONONCIATIONS_NOMFICHIER + " ou "
					+ prononciationsFilePath, e);
			return;
		}

		int prononciationsSize = prononciationsTemp.size();

		// Tri des prononciations
		logger.info("Tri des {} prononciations (graphies)...", prononciationsSize);
		
		Collections.sort(prononciationsTemp, new GraphiePrononciationNaturalComparator< GraphiePrononciation>());
		
		logger.info("Tri des {} prononciations (graphies) terminé.", prononciationsSize);

		// Remplissage des 2 tables nécessaires au traitement des prononciations
		int pos = 0;
		String nouvelleGraphie = prononciationsTemp.get(pos).graphie;
		while (pos < prononciationsSize) {
			Set<String> prononciationsPossibles = new HashSet<String>(1);
			// Tant que la graphie est identique et qu'on est pas à la fin du tableau,
			// construction de la liste des prononciations possibles
			while (pos < prononciationsSize && nouvelleGraphie.equals(prononciationsTemp.get(pos).graphie)) {
				prononciationsPossibles.add(prononciationsTemp.get(pos).prononciation);
				pos++;
			}

			graphies.add(nouvelleGraphie);
			prononciationsInfos.add(prononciationsPossibles);

			if (pos < prononciationsSize) {
				nouvelleGraphie = prononciationsTemp.get(pos).graphie;
			}

		}

		sauvegardeListe(graphies, "/tmp/prononciations-tri.txt");
		sauvegardeListePrononciationsInfo(prononciationsInfos, "/tmp/prononciationsInfo.txt");

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
	
	public void sauvegardeListePrononciationsInfo(List<Set<String>> prononciationsInfoList, String dumpFilename) {
		File dumpFile = new File(dumpFilename);
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dumpFile), "UTF-8"));
			int cpt = 0;
			for (Set<String> list : prononciationsInfoList) {
				writer.append(cpt++ + "");
				writer.newLine();		
				
				for (String prononciation : list) {
					writer.append(prononciation);
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
	

	public String getPrononciationsFilePath() {
		return prononciationsFilePath;
	}

	public void setPrononciationsFilePath(String prononciationsFilePath) {
		this.prononciationsFilePath = prononciationsFilePath;
	}
	
	class GraphiePrononciation {
		public String graphie;
		public String prononciation;
		public GraphiePrononciation(String graphie, String prononciation) {
			super();
			this.graphie = graphie;
			this.prononciation = prononciation;
		}
		
	}

	class GraphiePrononciationNaturalComparator<T extends GraphiePrononciation> implements Comparator<GraphiePrononciation>
	{

	    @Override
	    public int compare(GraphiePrononciation o1, GraphiePrononciation o2)
	    {
	        return o1.graphie.compareTo(o2.graphie);
	    }

	}
	
}
