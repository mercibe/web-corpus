/**
 * Code library for textual corpus management
 *
 * Copyright (C) 2011 Benoit Mercier <benoit.mercier@servicelibre.com> — Tous droits réservés.
 *
 * Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le
 * modifier suivant les termes de la “GNU General Public License” telle que
 * publiée par la Free Software Foundation : soit la version 3 de cette
 * licence, soit (à votre gré) toute version ultérieure.
 *
 * Ce programme est distribué dans l’espoir qu’il vous sera utile, mais SANS
 * AUCUNE GARANTIE : sans même la garantie implicite de COMMERCIALISABILITÉ
 * ni d’ADÉQUATION À UN OBJECTIF PARTICULIER. Consultez la Licence Générale
 * Publique GNU pour plus de détails.
 *
 * Vous devriez avoir reçu une copie de la Licence Générale Publique GNU avec
 * ce programme ; si ce n’est pas le cas, consultez :
 * <http://www.gnu.org/licenses/>.
 */

package com.servicelibre.corpus.analyzis;

public class CatgramsPivot extends CatgramsManager {

	// Bloc d'initialisation
	{
	    // Mots grammaticaux
		catgrams.put("CONJONCTION", new Catgram("CONJONCTION", "conjonction", "conjonction", true));
		catgrams.put("DÉTERM_ET_ADJ_SOUDÉS", new Catgram("DÉTERM_ET_ADJ_SOUDÉS", "déterminant et adjectif soudés", "déterm._et_adj._soudés", true)); // ledit, ladite
		catgrams.put("DÉTERMINANT", new Catgram("DÉTERMINANT", "déterminant", "determinant", true));
		catgrams.put("DÉTERMINANT_DÉMONSTRATIF", new Catgram("DÉTERMINANT_DÉMONSTRATIF", "déterminant démonstratif", "déterminant_démonstratif", true));
		catgrams.put("DÉTERMINANT_EXCLAMATIF", new Catgram("DÉTERMINANT_EXCLAMATIF", "déterminant exclamatif", "déterminant_exclamatif", true));
		catgrams.put("DÉTERMINANT_INDÉFINI", new Catgram("DÉTERMINANT_INDÉFINI", "déterminant indéfini", "déterminant_indéfini", true));
		catgrams.put("DÉTERMINANT_INTERROGATIF", new Catgram("DÉTERMINANT_INTERROGATIF", "déterminant interrogatif", "déterminant_interrogatif", true));
		catgrams.put("DÉTERMINANT_NUMÉRAL", new Catgram("DÉTERMINANT_NUMÉRAL", "déterminant numéral", "déterminant_numéral", true));
		catgrams.put("DÉTERMINANT_POSSESSIF", new Catgram("DÉTERMINANT_POSSESSIF", "déterminant possessif", "déterminant_possessif", true));
		catgrams.put("DÉTERMINANT_RELATIF", new Catgram("DÉTERMINANT_RELATIF", "déterminant relatif", "déterminant_relatif", true));
		catgrams.put("ARTICLE", new Catgram("ARTICLE", "article", "article", true));
		catgrams.put("ARTICLE_DÉFINI", new Catgram("ARTICLE_DÉFINI", "article défini", "article_défini", true));
		catgrams.put("ARTICLE_INDÉFINI", new Catgram("ARTICLE_INDÉFINI", "article indéfini", "article_indéfini", true));
		catgrams.put("ARTICLE_PARTITIF", new Catgram("ARTICLE_PARTITIF", "article partitif", "article_partitif", true));
		catgrams.put("FUNCTIONWORD", new Catgram("FUNCTIONWORD", "functionword", "functionword", true)); // huitante, octante, septante, nonante
		catgrams.put("INTERJECTION", new Catgram("INTERJECTION", "interjection", "interjection", true));
		catgrams.put("PRÉP_ET_DÉTERM_COMBINÉS",
				new Catgram("PRÉP_ET_DÉTERM_COMBINÉS", "préposition et déterminant combinés", "prép._et_déterm._combinés", true)); // au, auquel, dudit, etc.(16)
		catgrams.put("PRÉP_ET_PRON_COMBINÉS", new Catgram("PRÉP_ET_PRON_COMBINÉS", "préposition et pronom combinés", "prép._et_pron._combinés", true)); // auquel, desquels, duquel, etc. (6)
		catgrams.put("PRÉPOSITION", new Catgram("PRÉPOSITION", "préposition", "preposition", true));
		catgrams.put("PRONOM", new Catgram("PRONOM", "pronom", "pronom", true));
		catgrams.put("PRONOM_DÉMONSTRATIF", new Catgram("PRONOM_DÉMONSTRATIF", "pronom démonstratif", "pronom_démonstratif", true));
		catgrams.put("PRONOM_INDÉFINI", new Catgram("PRONOM_INDÉFINI", "pronom indéfini", "pronom_indéfini", true));
		catgrams.put("PRONOM_INTERROGATIF", new Catgram("PRONOM_INTERROGATIF", "pronom interrogatif", "pronom_interrogatif", true));
		catgrams.put("PRONOM_NUMÉRAL", new Catgram("PRONOM_NUMÉRAL", "pronom numéral", "pronom_numéral", true));
		catgrams.put("PRONOM_PERSONNEL", new Catgram("PRONOM_PERSONNEL", "pronom personnel", "pronom_personnel", true));
		catgrams.put("PRONOM_POSSESSIF", new Catgram("PRONOM_POSSESSIF", "pronom possessif", "pronom_possessif", true));
		catgrams.put("PRONOM_RELATIF", new Catgram("PRONOM_RELATIF", "pronom relatif", "pronom_relatif", true));
		catgrams.put("ABRÉVIATION_LEXICALE", new Catgram("ABRÉVIATION_LEXICALE", "abréviation lexicale", "abréviation_lexicale", true));

		// autres mots (mot plein)
		catgrams.put("ADJECTIF", new Catgram("ADJECTIF", "adjectif", "adjective", false)); // 13174+
		catgrams.put("ADJECTIF_POSSESSIF", new Catgram("ADJECTIF_POSSESSIF", "adjectif possessif", "adjectif_possessif", false)); // 6: leur, mien, tien, sien, nôtre, vôtre
		catgrams.put("ADJECTIF_NUMÉRAL_ORDINAL", new Catgram("ADJECTIF_NUMÉRAL_ORDINAL", "adjectif numéral ordinal", "adjectif_numéral_ordinal", false)); // 31
		catgrams.put("ADJECTIF_NUMÉRAL_CARDINAL", new Catgram("ADJECTIF_NUMÉRAL_CARDINAL", "adjectif numéral cardinal", "adjectif_numéral_cardinal", false)); // 31
		catgrams.put("ADJECTIF_FRACTIONNAIRE", new Catgram("ADJECTIF_FRACTIONNAIRE", "adjectif fractionnaire", "adjectif_fractionnaire", false));
		catgrams.put("ADVERBE", new Catgram("ADVERBE", "adverbe", "adverb", false));
		catgrams.put("NOM_COMMUN", new Catgram("NOM_COMMUN", "nom commun", "commonnoun", false)); // 38439+
		catgrams.put("LOCUTION", new Catgram("LOCUTION", "locution", "locution", false));
		catgrams.put("LOCUTION_ADJ", new Catgram("LOCUTION_ADJ", "locution adjectivale", "locution_adj.", false));
		catgrams.put("LOCUTION_ADV", new Catgram("LOCUTION_ADV", "locution adverbiale", "locution_adv.", false));
		catgrams.put("LOCUTION_CONJ", new Catgram("LOCUTION_CONJ", "locution conjonctive", "locution_conj.", false));
		catgrams.put("LOCUTION_INTERJ", new Catgram("LOCUTION_INTERJ", "locution interjective", "locution_interj.", false));
		catgrams.put("LOCUTION_LATINE", new Catgram("LOCUTION_LATINE", "locution latine", "locution_latine", false));
		catgrams.put("LOCUTION_PRÉP", new Catgram("LOCUTION_PRÉP", "locution prépositionnelle", "locution_prép.", false));
		catgrams.put("LOCUTION_VERB", new Catgram("LOCUTION_VERB", "locution verbiale", "locution_verb.", false));
		catgrams.put("PHRASÉOLOGISME", new Catgram("PHRASÉOLOGISME", "phraséologisme", "phraséologisme", false));
		catgrams.put("UNITÉ_COMPLEXE", new Catgram("UNITÉ_COMPLEXE", "unité complexe", "unité_complexe", false));
		catgrams.put("MOT_LATIN", new Catgram("MOT_LATIN", "mot latin", "mot_latin", false)); // confer, exit, pax romana (3)
		catgrams.put("PARTICIPE", new Catgram("PARTICIPE", "participe passé", "participe", false));
		catgrams.put("NOM_PROPRE", new Catgram("NOM_PROPRE", "nom propre", "propernoun", false)); // 462+
		catgrams.put("SIGLE", new Catgram("SIGLE", "sigle", "sigle", false));
		catgrams.put("VERBE", new Catgram("VERBE", "verbe", "verb", false));
		catgrams.put("AUXILLIAIRE", new Catgram("AUXILLIAIRE", "verbe auxilliaire", "auxilliaire", false));
		catgrams.put("VERBE_DÉFECTIF", new Catgram("VERBE_DÉFECTIF", "verbe défectif", "verbe_défectif", false));
		catgrams.put("INCONNUE", new Catgram("INCONNUE", "catégorie grammaticale inconnue", "inconnue", false));
		
		
		for(String key : catgrams.keySet()) {
		    catgramBijection.put(key, key);
		}
		
	}

}
