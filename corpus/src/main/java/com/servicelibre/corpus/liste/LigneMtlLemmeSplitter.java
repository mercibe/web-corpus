package com.servicelibre.corpus.liste;

import java.util.ArrayList;
import java.util.List;

import com.servicelibre.corpus.entity.Liste;
import com.servicelibre.corpus.entity.Mot;


/**
 * mot\tlemme\tcatgram\tcatgram_precision\tgenre\tnombre\tro\tnote
 * 
 * Tous les mots de la liste sont considérés comme des lemmes (seuls les lemmes
 * sont retenus)
 * 
 * Exemple d'entrées 
 * -----------------
 * 
 * malade \t \t adj. 
 * vite \t \t adv. 
 * comme \t \t conj.. 
 * deux \t \t dét. 
 * auto \t \t n.f. 
 * bars \t \t n.m.
 * 
 * ami, amie \t \t n.m., n.f. 
 * il \t elle, ils, elles \t pron.
 * 
 * beau \t bel, belle \t adj. 
 * animal \t animaux \t n.m. 
 * ne pas \t ne...pas, n'…pas \t adv. 
 * du \t de l', de la, des \t dét.
 * 
 * RO:
 * 
 * frais \t fraîche / *fraiche \t adj.
 * 
 * 
 * 
 * @author benoitm
 * 
 */
public class LigneMtlLemmeSplitter implements LigneSplitter {
	private static final String SÉPARATEUR = "\\t";

	@Override
	public List<Mot> splitLigne(String ligne, Liste liste) {

		List<Mot> mots = new ArrayList<Mot>(1);

		String[] cols = ligne.split(SÉPARATEUR);
		
		if(cols.length< 3) {
			System.err.println("Colonnes insuffisantes (minimum 3 - trouvé "+cols.length+"): " + ligne);
			return mots;
		}

		nettoie(cols);

		String graphie = cols[0];
		String lemme = cols[0];
		boolean isLemme = true;
		String classe = cols[2];
		String catgram = "";
		List<String> genres;
		String nombre = "";
		String catgramPrécision = "";
		boolean isRo = false;
		String note = "";

		// Traiter catgram_genre_nombre
		catgram = getCatgram(classe);
		genres = getGenre(classe, catgram.length());
		nombre = getNombre(classe);

		catgramPrécision = classe.replace(catgram, "").replace(nombre, "").trim();
		for (String genre : genres) {
			catgramPrécision = catgramPrécision.replace(genre, "").trim();
		}
		catgramPrécision = catgramPrécision.replace("et", "").replace("ou", "");

		// traitement double graphie (RO ou non) : «goût / *gout» ou «paie /
		// paye»
		int indexOfSlash = lemme.indexOf("/");
		if (indexOfSlash > 0) {
			String graphie2 = graphie.substring(indexOfSlash + 1).trim();
			if (graphie2.indexOf("*") >= 0) {
				isRo = true;
				graphie2 = graphie2.replace("*", "").trim();
			}

			String lemme2 = graphie2;

			graphie = graphie.substring(0, indexOfSlash).trim();
			lemme = graphie;

			if (genres.isEmpty()) {
				mots.add(new Mot(liste, graphie2, lemme2, isLemme, catgram, "", nombre, catgramPrécision, isRo, note));
				mots.add(new Mot(liste, graphie, lemme, isLemme, catgram, "", nombre, catgramPrécision, false, note));

			} else {
				for (String genre : genres) {
					mots.add(new Mot(liste, graphie2, lemme2, isLemme, catgram, genre, nombre, catgramPrécision, isRo, note));
					mots.add(new Mot(liste, graphie, lemme, isLemme, catgram, genre, nombre, catgramPrécision, false, note));
				}
			}

		} else {
			if (genres.isEmpty()) {
				mots.add(new Mot(liste, graphie, lemme, isLemme, catgram, "", nombre, catgramPrécision, isRo, note));
			} else {
				for (String genre : genres) {
					mots.add(new Mot(liste, graphie, lemme, isLemme, catgram, genre, nombre, catgramPrécision, isRo, note));
				}
			}
		}

		return mots;
	}

	private String getNombre(String classe) {
		String nombre = "";

		if (classe.indexOf("pl.") >= 0) {
			nombre = "pl.";
		} else if (classe.indexOf("inv.") >= 0) {
			nombre = "inv.";
		}

		return nombre;
	}

	private List<String> getGenre(String classe, int length) {
		List<String> genres = new ArrayList<String>(1);

		int pos = classe.indexOf(".", length);

		if (pos >= 0) {
			String candidatGenre = classe.substring(length, pos + 1);
			if (candidatGenre.equalsIgnoreCase("m.") || candidatGenre.equalsIgnoreCase("f.") || candidatGenre.equalsIgnoreCase("inv.")) {
				genres.add(candidatGenre);
			}

			if (classe.contains("et f.") || classe.contains("ou f.")) {
				candidatGenre = classe.substring(pos + 2, pos + 2 + 5);
				genres.add(candidatGenre.substring(3));
			}
		}

		return genres;
	}

	/**
	 * Tout ce qui vient avant le premier point
	 * 
	 * @param classe
	 * @return
	 */
	private String getCatgram(String classe) {
		return classe.substring(0, classe.indexOf(".") + 1);
	}

	/**
	 * Supprime les blancs inutiles Ne conserve que le lemme (1er élément de la
	 * 1ère colonne, séparé par virgule, sauf si pronominal s' ou se) Ne
	 * conserve que la 1ère catgram (séparée par virgule)
	 * 
	 * @param cols
	 */
	private void nettoie(String[] cols) {
		for (int i = 0; i < cols.length; i++) {
			cols[i] = cols[i].trim();
		}

		if (!(cols[0].endsWith(", s'") || cols[0].endsWith(", se"))) {
			cols[0] = cols[0].split(",")[0].trim();
		}

		cols[1] = "";

		if (!(cols[0].endsWith(", s'") || cols[0].endsWith(", se"))) {
			cols[2] = cols[2].split(",")[0].trim();
		}
	}

    @Override
    public List<Mot> splitLigne(String ligne)
    {
        return splitLigne(ligne, new Liste());
    }
}
