package com.servicelibre.corpus.liste;

import java.util.ArrayList;
import java.util.List;

import com.servicelibre.corpus.entity.Mot;

/**
 * 
 * mot\tlemme\tcatgram\tgenre\tnombre\tcatgram_precision\tro
 * 
 * ro=> true ou false
 * 
 * @author benoitm
 * 
 */
public class LigneSimpleSplitter implements LigneSplitter {
	private static final String SÉPARATEUR = "\\t";

	@Override
	public List<Mot> splitLigne(String ligne) {
		List<Mot> mots = new ArrayList<Mot>(1);

		String[] cols = ligne.split(SÉPARATEUR);

		nettoie(cols);

		mots.add(new Mot(cols[0], cols[1], cols[0].equals(cols[1]), cols[2], cols[3], cols[4], cols[5], Boolean.parseBoolean(cols[6]), ""));

		return mots;
	}

	private void nettoie(String[] cols) {
		for (int i = 0; i < cols.length; i++) {
			cols[i] = cols[i].trim();
		}
	}
}
