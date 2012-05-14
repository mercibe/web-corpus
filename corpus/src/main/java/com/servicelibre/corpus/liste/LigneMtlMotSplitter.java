package com.servicelibre.corpus.liste;

import java.util.ArrayList;
import java.util.List;

import com.servicelibre.corpus.entity.Liste;
import com.servicelibre.corpus.entity.Mot;

/**
 * mot\tlemme\tcatgram\tcatgram_precision\tgenre\tnombre\tro\tnote
 * 
 * Tous les mots de la liste sont considérés comme des informations sur des mots
 * (lemmes et mots)
 * 
 * Exemple d'entrées -----------------
 * 
 * malade \t \t adj. vite \t \t adv. comme \t \t conj.. deux \t \t dét. auto \t
 * \t n.f. bars \t \t n.m.
 * 
 * Virgule dans première colonne: soit verbe pronominal, soit graphie
 * alternative, soit forme féminine d'un nom (=> double catgram) baseball,
 * base-ball \t \tn.m. basketball, basket-ball \t \t n.m. ami, amie \t \t n.m.,
 * n.f.
 * 
 * il \t elle, ils, elles \t pron.
 * 
 * 
 * 
 * Contenu 2e colonne si adj.: forme féminine (un seul mot), forme féminine
 * rectifiée (si / *) ou forme masulione alternative+ forme féminine ( si « , »
 * 
 * faux \t fausse \t adj. frais \t fraîche /*fraiche \t adj. beau \t bel, belle
 * \t adj.
 * 
 * Contenu 2e colonne si nom: forme plurielle
 * 
 * animal \t animaux \t n.m.
 * 
 * Contenu 2e colonne si autre catgram (adv., dét., pron., etc.): autres formes
 * (féminine, plurielle, etc.), séparée par des virgules ne pas \t ne...pas,
 * n'…pas \t adv. du \t de l', de la, des \t dét.
 * 
 * RO:
 * 
 * frais \t fraîche / *fraiche \t adj.
 * 
 * 
 * @author benoitm
 * 
 */
public class LigneMtlMotSplitter implements LigneSplitter {
	
	private static final String SÉPARATEUR = "\\t";
	
	Liste liste  = new Liste();

	@Override
	public List<Mot> splitLigne(String ligne) {

		List<Mot> mots = new ArrayList<Mot>(1);
		

		String[] cols = ligne.split(SÉPARATEUR);

		if (cols.length != 3 && cols.length != 5) {
			System.err.println("Mauvais nomnre de colonnes dans le fichier source: il faut 3 ou 5 colonnes - trouvé "
					+ cols.length + "): " + ligne);
			return mots;
		}
		
		// Information sur la liste dans la ligne (fichier source multiliste)
		if(cols.length == 5) {
			liste = new Liste(cols[3].trim(), cols[4].trim(), null);
		}

		// Récupération des catgrams
		String[] catgrams = cols[2].split(",");

		// Détection des RO
		String[] col0GraphiesRo = cols[0].split("/ \\*");
		String[] col0Graphies;

		String lemme_classe = "";

		if (col0GraphiesRo.length > 1) {

			// Une ou 2 graphies RO dans la 2e colonne (après le /*)
			String[] graphiesTradionnelles = col0GraphiesRo[0].trim()
					.split(",");
			String[] graphiesRectifiées = col0GraphiesRo[1].trim().split(",");

			extraireMots(mots, cols, catgrams, graphiesTradionnelles,
					graphiesRectifiées);
			lemme_classe = extraireMots(mots, cols, catgrams,
					graphiesRectifiées, graphiesTradionnelles, true, null,
					null, null);

		} else {
			
			// pour traiter les cas de graphies multiples « non ro » (paie / paye, etc.)
			cols[0] = cols[0].replace("/", ",");
			
			col0Graphies = cols[0].trim().split(",");
			lemme_classe = extraireMots(mots, cols, catgrams, col0Graphies,
					null);
		}

		// TODO traiter 2e colonne
		String col1 = cols[1].trim();
		if (col1.length() > 0) {

			String[] col1GraphiesRo = cols[1].split("/ \\*");
			String[] col1Graphies;
			String lemmeForcé = lemme_classe.split(",")[0];
			String genreForcé = null;
			String nombreForcé = null;

			if (col1GraphiesRo.length > 1) {

				// Une ou 2 graphies RO dans la 2e colonne (après le /*)
				String[] graphiesTradionnelles = col1GraphiesRo[0].trim()
						.split(",");
				String[] graphiesRectifiées = col1GraphiesRo[1].trim().split(
						",");

				if (lemme_classe.endsWith(",adj.") && col1.indexOf(",") < 0) {
					genreForcé = "f.";
				}
				
				extraireMots(mots, cols, catgrams, graphiesTradionnelles,
						graphiesRectifiées, false, lemmeForcé, genreForcé, null);
				extraireMots(mots, cols, catgrams, graphiesRectifiées,
						graphiesTradionnelles, true, lemmeForcé, genreForcé, null);

			} else {

				col1Graphies = cols[1].trim().split(",");

				if (lemme_classe.endsWith(",adj.")) {
					if(col1.indexOf(",") < 0){
						genreForcé = "f.";
					}
					else {
						genreForcé = "mf";
					}
				} else if (lemme_classe.endsWith(",n.")) {
					nombreForcé = "pl.";
				}

				extraireMots(mots, cols, catgrams, col1Graphies, null, false,
						lemmeForcé, genreForcé, nombreForcé);
			}
		}

		return mots;
	}

	private String extraireMots(List<Mot> mots, String[] cols,
			String[] catgrams, String[] col0Graphies, String[] autresGraphies) {
		return extraireMots(mots, cols, catgrams, col0Graphies, autresGraphies,
				false, null, null, null);
	}

	private String extraireMots(List<Mot> mots, String[] cols,
			String[] catgrams, String[] col0Graphies, String[] autresGraphies,
			boolean ro, String lemmeForcé, String genreForcé, String nombreForcé) {

		String classe = "";

		// lemme = toujours le premier élément sauf si une seule catgram et
		// plusieurs élément colonne 1
		// animateur, animatrice \t \t n.m., n.f.
		// baseball, base-ball \t \t n.m.
		String lemme = col0Graphies[0].trim();

		// Boucler sur les graphies si pas verbe pronominal
		if (!(cols[0].endsWith(", s'") || cols[0].endsWith(", se"))) {

			for (int i = 0; i < col0Graphies.length; i++) {
				String mot = col0Graphies[i].trim();

				if (lemmeForcé != null) {
					lemme = lemmeForcé;
				} else if (i > 0 && catgrams.length == 1) {
					lemme = mot;
				}

				Mot modeleMot = new Mot();
				modeleMot.setNombre("");
				modeleMot.setCatgramPrésicion("");
				modeleMot.setNote("");

				modeleMot.setMot(mot);
				modeleMot.setLemme(lemme);

				String catgram = catgrams[Math.min(i, catgrams.length - 1)]
						.trim();
				classe = getClasse(catgram);
				String genre = getGenre(catgram, classe.length());
				if (genreForcé != null) {
					if(genreForcé.equals("mf")){
						if(i==0) {
							genre = "";
						}
						else {
							genre = "f.";
						}
					}
					else
					{
						genre = genreForcé;
					}
				}
				String nombre = getNombre(catgram);
				if (nombreForcé != null) {
					nombre = nombreForcé;
				}

				String catgramPrécision = catgram.replace(classe, "")
						.replace(genre, "").replace(nombre, "").trim();

				modeleMot.setCatgram(classe);
				modeleMot.setGenre(genre);
				modeleMot.setNombre(nombre);
				modeleMot.setCatgramPrésicion(catgramPrécision);
				modeleMot.setRo(ro);

				if (autresGraphies != null && autresGraphies.length > i) {
					modeleMot.setAutreGraphie(autresGraphies[i]);
				}

				modeleMot.isLemme = mot.equals(lemme);
				
				mots.add(modeleMot);

			}
		} else {
			// ajout du verbe pronominal
			String mot = cols[0];

			Mot modeleMot = new Mot();

			modeleMot.setNombre("");
			modeleMot.setCatgramPrésicion("");
			modeleMot.setNote("");
			modeleMot.setMot(mot);
			modeleMot.setLemme(mot);
			classe = "v.";
			modeleMot.setCatgram(classe);
			modeleMot.setCatgramPrésicion("pron.");
			modeleMot.setGenre("");
			modeleMot.setNombre("");
			modeleMot.setRo(ro);

			modeleMot.isLemme = true;
			
			mots.add(modeleMot);

		}

		return lemme + "," + classe;
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

	/**
	 * Extrait le genre d'une catgram complète. Genre = la lettre qui suit le
	 * premier point. catgram: n.m., n.f., v., etc.)
	 * 
	 * @param catgram
	 * @param classeLength
	 * @return
	 */
	private String getGenre(String catgram, int classeLength) {
		String genre = "";

		int pos = catgram.indexOf(".", classeLength);

		if (pos >= 0) {
			String candidatGenre = catgram.substring(classeLength, pos + 1);
			if (candidatGenre.equalsIgnoreCase("m.")
					|| candidatGenre.equalsIgnoreCase("f.")
					|| candidatGenre.equalsIgnoreCase("inv.")) {
				genre = candidatGenre;
			}
		}
		return genre;
	}

	/**
	 * Tout ce qui vient avant le premier point
	 * 
	 * @param catgram
	 * @return
	 */
	private String getClasse(String catgram) {
		return catgram.substring(0, catgram.indexOf(".") + 1);
	}
}
