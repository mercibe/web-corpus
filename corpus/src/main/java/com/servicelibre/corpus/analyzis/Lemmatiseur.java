package com.servicelibre.corpus.analyzis;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.apache.lucene.store.Directory;

import com.servicelibre.corpus.metadata.Metadata;

// TODO besoin d'un format unique pour catgram et convertisseur de/vers ce format pour franqus, lexique3, etc.
// => esperanto des catgrams ;-)

/**
 * 
 * 
 * @author benoitm
 *
 */
public interface Lemmatiseur
{

    public List<MotInfo> getFranqusMotInfo(String mot);

    public List<MotInfo> getLexiqueMotInfo(List<MotInfo> candidatFranqusList);

    public enum CatgramFranqus
    {
        // Mots grammaticaux
        CONJONCTION("conjonction", "conjonction"),
        DÉTERM_ET_ADJ_SOUDÉS("déterminant et adjectif soudés", "déterm._et_adj._soudés"), //ledit, ladite
        DÉTERMINANT("déterminant", "determinant"),
        DÉTERMINANT_DÉMONSTRATIF("déterminant démonstratif", "déterminant_démonstratif"),
        DÉTERMINANT_EXCLAMATIF("déterminant exclamatif", "déterminant_exclamatif"),
        DÉTERMINANT_INDÉFINI("déterminant indéfini", "déterminant_indéfini"),
        DÉTERMINANT_INTERROGATIF("déterminant interrogatif", "déterminant_interrogatif"),
        DÉTERMINANT_NUMÉRAL("déterminant numéral", "déterminant_numéral"),
        DÉTERMINANT_POSSESSIF("déterminant possessif", "déterminant_possessif"),
        DÉTERMINANT_RELATIF("déterminant relatif", "déterminant_relatif"),
        ARTICLE("article", "article"),
        ARTICLE_DÉFINI("article défini", "article_défini"),
        ARTICLE_INDÉFINI("article indéfini", "article_indéfini"),
        ARTICLE_PARTITIF("article partitif", "article_partitif"),
        FUNCTIONWORD("functionword", "functionword"), // huitante, octante, septante, nonante
        INTERJECTION("interjection", "interjection"),
        PRÉP_ET_DÉTERM_COMBINÉS("préposition et déterminant combinés", "prép._et_déterm._combinés"), //au, auquel, dudit, etc. (16)
        PRÉP_ET_PRON_COMBINÉS("préposition et pronom combinés", "prép._et_pron._combinés"), //auquel, desquels, duquel, etc. (6)
        PRÉPOSITION("préposition", "preposition"),
        PRONOM("pronom", "pronom"),
        PRONOM_DÉMONSTRATIF("pronom démonstratif", "pronom_démonstratif"),
        PRONOM_INDÉFINI("pronom indéfini", "pronom_indéfini"),
        PRONOM_INTERROGATIF("pronom interrogatif", "pronom_interrogatif"),
        PRONOM_NUMÉRAL("pronom numéral", "pronom_numéral"),
        PRONOM_PERSONNEL("pronom personnel", "pronom_personnel"),
        PRONOM_POSSESSIF("pronom possessif", "pronom_possessif"),
        PRONOM_RELATIF("pronom relatif", "pronom_relatif"),
        ABRÉVIATION_LEXICALE("abréviation lexicale", "abréviation_lexicale"),
        
        // autres mots (mot plein)
        ADJECTIF("adjectif", "adjective"), //13174+
        ADJECTIF_POSSESSIF("adjectif possessif", "adjectif_possessif"), //6 : leur, mien, tien, sien, nôtre, vôtre
        ADJECTIF_NUMÉRAL_ORDINAL("adjectif numéral ordinal", "adjectif_numéral_ordinal"), //31
        ADJECTIF_NUMÉRAL_CARDINAL("adjectif numéral cardinal", "adjectif_numéral_cardinal"), //31
        ADJECTIF_FRACTIONNAIRE("adjectif fractionnaire", "adjectif_fractionnaire"),
        ADVERBE("adverbe", "adverb"),
        NOM_COMMUN("nom commun", "commonnoun"), //38439+             
        LOCUTION("locution", "locution"),
        LOCUTION_ADJ("locution adjectivale", "locution_adj."),
        LOCUTION_ADV("locution adverbiale", "locution_adv."),
        LOCUTION_CONJ("locution conjonctive", "locution_conj."),
        LOCUTION_INTERJ("locution interjective", "locution_interj."),
        LOCUTION_LATINE("locution latine", "locution_latine"),
        LOCUTION_PRÉP("locution prépositionnelle", "locution_prép."),
        LOCUTION_VERB("locution verbiale", "locution_verb."),
        PHRASÉOLOGISME("phraséologisme", "phraséologisme"),
        UNITÉ_COMPLEXE("unité complexe", "unité_complexe"),
        MOT_LATIN("mot latin", "mot_latin"), // confer, exit, pax romana (3)
        PARTICIPE("participe passé", "participe"),
        NOM_PROPRE("nom propre", "propernoun"), // 462+
        SIGLE("sigle", "sigle"),
        VERBE("verbe", "verb"),
        AUXILLIAIRE("verbe auxilliaire", "auxilliaire"),
        VERBE_DÉFECTIF("verbe défectif", "verbe_défectif"),
        INCONNUE("catégorie grammaticale inconnue", "inconnue");

        static EnumSet<CatgramFranqus> motsGrammaticaux = EnumSet.range(CatgramFranqus.CONJONCTION,CatgramFranqus.ABRÉVIATION_LEXICALE);
        
        String label;
        String value;
        

        CatgramFranqus(String value, String label)
        {
            this.value = value;
            this.label = label;
        }

        
        public boolean isMotGrammatical(){
            
            return motsGrammaticaux.contains(this);
        }
        
        /**
         * Retourne la catgram Franqus correspondant à la catgram Lexique 3
         * donnée
         * 
         * @param catgram_lexique
         * @return
         */
        public static CatgramFranqus valueOf(CatgramLexique catgram_lexique)
        {

            switch (catgram_lexique)
            {

                case ADJ:
                    return CatgramFranqus.ADJECTIF;
                case ADJ_DEM:
                    return CatgramFranqus.DÉTERMINANT_DÉMONSTRATIF;
                case ADJ_IND:
                    return CatgramFranqus.DÉTERMINANT_INDÉFINI;
                case ADJ_INT:
                    return CatgramFranqus.DÉTERMINANT_INTERROGATIF;
                case ADJ_NUM:
                    return CatgramFranqus.DÉTERMINANT_NUMÉRAL; // aussi ADJECTIF_NUMÉRAL_CARDINAL, etc.
                case ADJ_POS:
                    return CatgramFranqus.DÉTERMINANT_POSSESSIF;
                case ADV:
                    return CatgramFranqus.ADVERBE;
                case ART_DEF:
                    return CatgramFranqus.ARTICLE_DÉFINI;
                case ART_IND:
                    return CatgramFranqus.ARTICLE_INDÉFINI;
                case VER:
                    return CatgramFranqus.VERBE;
                case AUX:
                    return CatgramFranqus.AUXILLIAIRE;
                case CON:
                    return CatgramFranqus.CONJONCTION;
                case LIA:
                    return CatgramFranqus.DÉTERMINANT;
                case NOM:
                    return CatgramFranqus.NOM_COMMUN;
                case ONO:
                    return CatgramFranqus.INTERJECTION; // aussi commonNoun
                case PRE:
                    return CatgramFranqus.PRÉPOSITION;
                case PRO_DEM:
                    return CatgramFranqus.PRONOM_DÉMONSTRATIF;
                case PRO_IND:
                    return CatgramFranqus.PRONOM_INDÉFINI;
                case PRO_INT:
                    return CatgramFranqus.PRONOM_INTERROGATIF;
                case PRO_PER:
                    return CatgramFranqus.PRONOM_PERSONNEL;
                case PRO_POS:
                    return CatgramFranqus.PRONOM_POSSESSIF;
                case PRO_REL:
                    return CatgramFranqus.PRONOM_RELATIF;

            }

            return CatgramFranqus.INCONNUE;
        }

        /**
         * Retourne la catgram Franqus sur base du label donné 
         * 
         * @param label
         * @return
         */
        public static CatgramFranqus valueOfLabel(String label)
        {

            for (CatgramFranqus catgram : CatgramFranqus.values())
            {
                if (catgram.label.equalsIgnoreCase(label))
                {
                    return catgram;
                }
            }

            return CatgramFranqus.INCONNUE;
        }

    };

    public enum CatgramLexique
    {

        ADJ("adjectif", "ADJ"),
        ADJ_DEM("adjectif démonstratif", "ADJ:dem"),
        ADJ_IND("adjectif indéfini", "ADJ:ind"),
        ADJ_INT("adjectif interrogatif", "ADJ:int"),
        ADJ_NUM("adjectif numérique", "ADJ:num"),
        ADJ_POS("adjectif possessif", "ADJ:pos"),
        ADV("adverbe", "ADV"),
        ART_DEF("article défini", "ART:def"),
        ART_IND("article indéfini", "ART:ind"),
        AUX("auxilliaire", "AUX"),
        CON("conjonction", "CON"),
        LIA("liaison euphonique", "LIA"),
        NOM("nom commun", "NOM"),
        ONO("onomatopée", "ONO"),
        PRE("préposition", "PRE"),
        PRO_DEM("pronom démonstratif", "PRO:dem"),
        PRO_IND("pronom indéfini", "PRO:ind"),
        PRO_INT("pronom interrogatif", "PRO:int"),
        PRO_PER("pronom personnel", "PRO:per"),
        PRO_POS("pronom possessif", "PRO:pos"),
        PRO_REL("pronom relatif", "PRO:rel"),
        VER("verbe", "VER"),
        INCONNUE("catégorie grammaticale Lexique 3 inconnue", "inconnue");

        public String label;
        public String value;

        CatgramLexique(String value, String label)
        {
            this.value = value;
            this.label = label;
        }

        /**
         * Retourne la catgram Lexique sur base du label donné (case sensitive
         * Lexique3 catgram)
         * 
         * @param label
         * @return
         */
        public static CatgramLexique valueOfLabel(String label)
        {

            for (CatgramLexique catgram : CatgramLexique.values())
            {
                if (catgram.label.equals(label))
                {
                    return catgram;
                }
            }

            return CatgramLexique.INCONNUE;
        }

    }

    public CorpusInfo analyze(Directory fsDirectory, Map<String, Map<String, Metadata>> metadatas, String docIdFieldName, String txtlexFieldname);

    public void init();

    public List<String> getFranqusMotStartWith(String mot);

    public String normalize(String mot);


}
