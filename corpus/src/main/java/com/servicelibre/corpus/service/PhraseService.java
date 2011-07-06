package com.servicelibre.corpus.service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhraseService {

	/**
	 * 
	 * cas non traités:
	 * => « Encore et toujours ces maudits braconniers ! » pensa Xavier en libérant le pic-vert. 
	 * => Satapoisse! s'exclama Effroyable Mémère.
	 * => Tu empestes ! s'écria-t-il.
	 */
	Pattern phraseComplète = Pattern.compile("[A-ZÇÂÀÉÈËÊÎÏÔÖÙÜ]+[a-zA-Zçâàéèëêîïôöùü\\-,'\\(\\)«»\": ]*[\\.?!—]+"); 

	/**
	 * Retourne toutes les phrases complètes du contexte donné
	 * Une phrase complète commence obligatoirement par une majuscule et se termine par un point, un point d'interrogation ou un point d'exclamation
	 * @param c
	 * @return
	 */
	public List<String> getPhrasesComplètes(Contexte c) {
		
		// Reconstitution d'une string complète
		StringBuilder sb = new StringBuilder(c.texteAvant).append(c.mot).append(c.texteAprès);
		
		Matcher matcher = phraseComplète.matcher(sb.toString());
		
		while(matcher.find()) {
			System.out.println(matcher.group());
		}
		
		
		// TODO Auto-generated method stub
		return null;
	}

}
