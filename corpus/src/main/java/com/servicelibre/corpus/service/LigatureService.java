package com.servicelibre.corpus.service;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LigatureService {

    static Pattern p = Pattern.compile("\\w*(ae|oe)\\w*"); 
    
    static final String[] exceptions = new String[] {

	    // exceptions oe

	    "acétylcoenzyme", "agroenvironnement", "alstroemère ou alstromère", "alstroeméria", "alstroemeria ou alstromeria", "apoenzyme", "autoexcitateur",
	    "autoextractible", "boette ou bouette", "capoeira", "coefficient", "coelome", "coentreprise", "coenzyme", "coercible", "coercitif", "coercition",
	    "coexistence", "coexister", "coextensif", "électroencéphalogramme", "électroencéphalographie", "foehn", "gastroentérite", "gastroentérologie",
	    "gastroentérologue", "groenlandais", "incoercible", "méningoencéphalite ou méningo-encéphalite", "microentreprise", "minoen", "moelle",
	    "moelleusement", "moelleux", "moellon", "neuroendocrinologie", "oe", "paléoenvironnement", "pseudoemprunt", "réticuloendothélial",
	    "thromboembolique",

	    // exceptions ae

	    "dracaena", "dracéna ou dracaena", "HAE", "maelström", "maelstrom ou malstrom", "maerl", "maestoso", "maestria", "maestro", "paella", "reggae",
	    "sundae", "taekwondo", "uraeus"

    };
    
    static {
	Arrays.sort(exceptions);
    }

    /**
     * Retourne un mot avec les lettres ae ou oe ligaturées en tenant compte des
     * exceptions. Si ce mot contient des lettres ligaturées (bonne ou
     * mauvaise), aucune correction n'est faite.
     * 
     * @param mot
     * @return
     */
    public String getMotAvecLigature(String mot) {
	int index = Arrays.binarySearch(exceptions, mot.trim().toLowerCase());
	if (index > -1) {
	    return mot;
	} else {
	    return mot.replaceAll("oe", "œ").replaceAll("ae", "æ");
	}
    }

    public String getPhraseAvecLigature(String phrase) {

	// Récupérer tous les mots de la phrase avec oe ou ae
	Matcher matcher = p.matcher(phrase);
	
	while(matcher.find()) {
	    
	    String motCandidatLigature = matcher.group(0);
	    
	    // Effectuer remplacement de chacun de ces mots
	    phrase = phrase.replaceAll(motCandidatLigature, getMotAvecLigature(motCandidatLigature));
	}

	return phrase;
    }

}
