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

public class CatgramsLexique3 extends CatgramsManager
{

    // Bloc d'initialisation 
    {
        catgrams.put("ART_DEF", new Catgram("ART_DEF", "article défini", "ART:def", true));
        catgrams.put("ART_IND", new Catgram("ART_IND", "article indéfini", "ART:ind", true));
        catgrams.put("CON", new Catgram("CON", "conjonction", "CON", true));
        catgrams.put("LIA", new Catgram("LIA", "liaison euphonique", "LIA", true));
        catgrams.put("NOM", new Catgram("NOM", "nom commun", "NOM", true));
        catgrams.put("ONO", new Catgram("ONO", "onomatopée", "ONO", true));
        catgrams.put("PRE", new Catgram("PRE", "préposition", "PRE", true));
        catgrams.put("PRO_DEM", new Catgram("PRO_DEM", "pronom démonstratif", "PRO:dem", true));
        catgrams.put("PRO_IND", new Catgram("PRO_IND", "pronom indéfini", "PRO:ind", true));
        catgrams.put("PRO_INT", new Catgram("PRO_INT", "pronom interrogatif", "PRO:int", true));
        catgrams.put("PRO_PER", new Catgram("PRO_PER", "pronom personnel", "PRO:per", true));
        catgrams.put("PRO_POS", new Catgram("PRO_POS", "pronom possessif", "PRO:pos", true));
        catgrams.put("PRO_REL", new Catgram("PRO_REL", "pronom relatif", "PRO:rel", true));

        catgrams.put("ADJ", new Catgram("ADJ", "adjectif", "ADJ", false));
        catgrams.put("ADJ_DEM", new Catgram("ADJ_DEM", "adjectif démonstratif", "ADJ:dem", false));
        catgrams.put("ADJ_IND", new Catgram("ADJ_IND", "adjectif indéfini", "ADJ:ind", false));
        catgrams.put("ADJ_INT", new Catgram("ADJ_INT", "adjectif interrogatif", "ADJ:int", false));
        catgrams.put("ADJ_NUM", new Catgram("ADJ_NUM", "adjectif numérique", "ADJ:num", false));
        catgrams.put("ADJ_POS", new Catgram("ADJ_POS", "adjectif possessif", "ADJ:pos", false));
        catgrams.put("VER", new Catgram("VER", "verbe", "VER", false));
        catgrams.put("ADV", new Catgram("ADV", "adverbe", "ADV", false));
        catgrams.put("AUX", new Catgram("AUX", "auxilliaire", "AUX", false));
        catgrams.put("INCONNUE",
                new Catgram("INCONNUE", "catégorie grammaticale Lexique 3 inconnue", "inconnue", false));

        
        // Table de traduction (bijection) des catégories Lexique3 et Pivot
        catgramBijection.put("ART_DEF", "ARTICLE_DÉFINI" );
        catgramBijection.put("ART_IND", "ARTICLE_INDÉFINI" );
        catgramBijection.put("CON", "CONJONCTION" );
        catgramBijection.put("LIA", "DÉTERMINANT" );
        catgramBijection.put("NOM", "NOM_COMMUN" );
        catgramBijection.put("ONO", "INTERJECTION" );// aussi commonNoun
        catgramBijection.put("PRE", "PRÉPOSITION" ); 
        catgramBijection.put("PRO_DEM", "PRONOM_DÉMONSTRATIF" ); 
        catgramBijection.put("PRO_IND", "PRONOM_INDÉFINI" );
        catgramBijection.put("PRO_INT", "PRONOM_INTERROGATIF" ); 
        catgramBijection.put("PRO_PER", "PRONOM_PERSONNEL" ); 
        catgramBijection.put("PRO_POS", "PRONOM_POSSESSIF" );
        catgramBijection.put("PRO_REL", "PRONOM_RELATIF" ); 
        catgramBijection.put("ADJ", "ADJECTIF" ); 
        catgramBijection.put("ADJ_DEM", "DÉTERMINANT_DÉMONSTRATIF" );
        catgramBijection.put("ADJ_IND", "DÉTERMINANT_INDÉFINI" );
        catgramBijection.put("ADJ_INT", "DÉTERMINANT_INTERROGATIF" );
        catgramBijection.put("ADJ_NUM", "DÉTERMINANT_NUMÉRAL" ); // aussi ADJECTIF_NUMÉRAL_CARDINAL, etc.
        catgramBijection.put("ADJ_POS", "DÉTERMINANT_POSSESSIF" ); 
        catgramBijection.put("VER", "VERBE" ); 
        catgramBijection.put("ADV", "ADVERBE" ); 
        catgramBijection.put("AUX", "AUXILLIAIRE" );
        catgramBijection.put("INCONNUE", "INCONNUE");

        
    }

 
}