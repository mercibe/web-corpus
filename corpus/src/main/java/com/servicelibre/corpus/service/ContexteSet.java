package com.servicelibre.corpus.service;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

import com.servicelibre.corpus.analysis.FrenchAnalyzer;
import com.servicelibre.corpus.manager.FiltreRecherche;


public class ContexteSet {

	// Analyseur propriétaire projet de recherche Franqus
	//private static FranqusLexicalAnalyzer lexicalAnalyzer = new FranqusLexicalAnalyzer(Version.LUCENE_33);
	private static FrenchAnalyzer lexicalAnalyzer = new FrenchAnalyzer(Version.LUCENE_33);

	public enum Position {
		AVANT, APRÈS, AVANT_APRÈS
	};

	List<Contexte> contextes = new ArrayList<Contexte>();

	int documentCount;

	int totalContextesCount;

	String motCherché = "";

	boolean formesDuLemme;

	FiltreRecherche filtre;

	public int tailleVoisinage;

	private int maxCooccurrent = 100;

	public List<Contexte> getContextes() {
		return contextes;
	}

	public int getContextesSize() {
		return contextes.size();
	}

	public void setContextes(List<Contexte> contextes) {
		this.contextes = contextes;
	}

	public int size() {
		return contextes.size();
	}

	public int getDocumentCount() {
		return documentCount;
	}

	public void setDocumentCount(int documentCount) {
		this.documentCount = documentCount;
	}

	public String getMotCherché() {
		return motCherché;
	}

	public void setMotCherché(String motCherché) {
		this.motCherché = motCherché;
	}

	public FiltreRecherche getFiltre() {
		return filtre;
	}

	public void setFiltre(FiltreRecherche filtre) {
		this.filtre = filtre;
	}

	public boolean isFormesDuLemme() {
		return formesDuLemme;
	}

	public void setFormesDuLemme(boolean formesDuLemme) {
		this.formesDuLemme = formesDuLemme;
	}

	public int getTotalContextesCount() {
		return totalContextesCount;
	}

	public void setTotalContextesCount(int totalContextesCount) {
		this.totalContextesCount = totalContextesCount;
	}

	/**
	 * 
	 * 
	 * Y a-t-il moyen d'optimier?
	 * 
	 * @return
	 */
	public Map<Position, List<InfoCooccurrent>> getInfoCooccurrents() {

		Map<String, InfoCooccurrent> infoCooccurents = new HashMap<String, InfoCooccurrent>();

		// Parcourir les contextes...
		for (Contexte contexte : contextes) {
			// Tokenizer contexte
			putInfoCooccurents(infoCooccurents, contexte.mot, getTokens(contexte.texteAvant), Position.AVANT);
			putInfoCooccurents(infoCooccurents, contexte.mot, getTokens(contexte.texteAprès), Position.APRÈS);

		}
		// Création de la liste de base
		List<InfoCooccurrent> ics = new ArrayList<InfoCooccurrent>();
		for (String key : infoCooccurents.keySet()) {
			ics.add(infoCooccurents.get(key));

		}

		Map<Position, List<InfoCooccurrent>> info = new HashMap<ContexteSet.Position, List<InfoCooccurrent>>(3);
		// Création des 3 listes triées et limitées (maxCooccurrent)
		info.put(Position.AVANT, getIcsPartiel(ics, Position.AVANT));

		info.put(Position.AVANT_APRÈS, getIcsPartiel(ics, Position.AVANT_APRÈS));

		info.put(Position.APRÈS, getIcsPartiel(ics, Position.APRÈS));

		return info;
	}

	private List<InfoCooccurrent> getIcsPartiel(List<InfoCooccurrent> ics, Position position) {

		Comparator<InfoCooccurrent> comparateur = getComparateur(position);

		List<InfoCooccurrent> nIcs = new ArrayList<InfoCooccurrent>(maxCooccurrent);

		Collections.sort(ics, comparateur);

		int cpt = 1;

		for (InfoCooccurrent info : ics) {

			// N'ajouter que les fréquences > 0
			if ((position == Position.AVANT && info.freqAvant > 0)
					|| (position == Position.AVANT_APRÈS && info.freq > 0)
					|| (position == Position.APRÈS && info.freqAprès > 0)) {
				nIcs.add(info);
			}

			if (cpt == maxCooccurrent) {
				break;
			}
			cpt++;
		}

		return nIcs;
	}

	private Comparator<InfoCooccurrent> getComparateur(Position position) {
		switch (position) {
		case AVANT:
			return new Comparator<InfoCooccurrent>() {

				@Override
				public int compare(InfoCooccurrent o1, InfoCooccurrent o2) {
					if (o2 == null) {
						return -1;
					}

					if (o2.freqAvant > o1.freqAvant) {
						return 1;
					} else if (o2.freqAvant == o1.freqAvant) {
						return 0;
					} else {
						return -1;
					}
				}
			};

		case AVANT_APRÈS:
			return new Comparator<InfoCooccurrent>() {

				@Override
				public int compare(InfoCooccurrent o1, InfoCooccurrent o2) {
					if (o2 == null) {
						return -1;
					}

					if (o2.freq > o1.freq) {
						return 1;
					} else if (o2.freq == o1.freq) {
						return 0;
					} else {
						return -1;
					}
				}
			};
		case APRÈS:
			return new Comparator<InfoCooccurrent>() {

				@Override
				public int compare(InfoCooccurrent o1, InfoCooccurrent o2) {
					if (o2 == null) {
						return -1;
					}

					if (o2.freqAprès > o1.freqAprès) {
						return 1;
					} else if (o2.freqAprès == o1.freqAprès) {
						return 0;
					} else {
						return -1;
					}
				}
			};

		}

		return null;
	}

	private void putInfoCooccurents(Map<String, InfoCooccurrent> infoCooccurents, String terme, List<String> tokens,
			Position position) {

		for (String token : tokens) {
			// vérifier si terme déjà repéré
			InfoCooccurrent infoCooccurrent = infoCooccurents.get(token);

			if (infoCooccurrent == null) {
				infoCooccurrent = new InfoCooccurrent();
				infoCooccurrent.terme = terme;
				infoCooccurrent.cooccurrent = token;
				infoCooccurrent.freqAvant = 0;
				infoCooccurrent.freqAprès = 0;
				infoCooccurrent.freq = 0;
				infoCooccurrent.distance = this.tailleVoisinage;
			}

			// incrémenter compteur d'occurrences
			if (position == Position.AVANT) {
				infoCooccurrent.freqAvant++;
			} else {
				infoCooccurrent.freqAprès++;
			}
			infoCooccurrent.freq++;

			infoCooccurents.put(token, infoCooccurrent);

		}

	}

	private List<String> getTokens(String phrase) {

		List<String> tokens = new ArrayList<String>(15);

		Reader stringReader = new StringReader(phrase);
		TokenStream stream = lexicalAnalyzer.tokenStream("txtlex", stringReader);
		CharTermAttribute charTermAttr = stream.getAttribute(CharTermAttribute.class);

		try {
			while (stream.incrementToken()) {
				tokens.add(new String(charTermAttr.buffer(), 0, charTermAttr.length()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tokens;
	}

	public int getMaxCooccurrent() {
		return maxCooccurrent;
	}

	public void setMaxCooccurrent(int maxCooccurrent) {
		this.maxCooccurrent = maxCooccurrent;
	}

	public int getTailleVoisinage() {
		return tailleVoisinage;
	}

	public void setTailleVoisinage(int tailleVoisinage) {
		this.tailleVoisinage = tailleVoisinage;
	}

}
