package com.servicelibre.corpus.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpPhraseService implements PhraseService {

	/**
	 * cas non traités: 
	 * => « Encore et toujours ces maudits braconniers ! » pensa Xavier en libérant le pic-vert. 
	 * => Satapoisse! s'exclama Effroyable Mémère. 
	 * => Je réplique : « Ils sont très sensibles. Touche, voir. »
	 * => - Bah ! Tu empestes ! s'écria-t-elle.
	 * => —	Émile ? Mon Dieu ! Dans quel état es- tu ! Tu empestes l'alcool... Tu as encore bu... Mon Dieu ! Mon Dieu ! J'ai mis mes mains sur ses épaules.
	 */
	Pattern phraseComplète = Pattern.compile("[A-ZÇÂÀÉÈËÊÎÏÔÖÙÜ]+[a-zA-Zçâàéèëêîïôöùü\\-,'\\(\\)«»\": ]* [\\.?!—]+");

	/**
	 * Retourne nbPhrases phrases complètes autour du contexte donné. Une phrase
	 * complète commence obligatoirement par une majuscule et se termine par un
	 * point, un point d'interrogation ou un point d'exclamation
	 * 
	 * @param c
	 * @return
	 */
	public List<Phrase> getPhrasesComplètes(Contexte c) {


		// Reconstitution d'une string complète
		StringBuilder sb = new StringBuilder(c.texteAvant).append(c.mot).append(c.texteAprès);

		return getPhrasesComplètes(sb.toString());

	}

	@Override
	public List<Phrase> getPhrasesComplètes(String texte) {
		
		List<Phrase> phrases = new ArrayList<Phrase>();
		
		Matcher matcher = phraseComplète.matcher(texte);

		while (matcher.find()) {
			Phrase p = new Phrase();
			p.phrase = matcher.group();
			phrases.add(p);
		}

		// Expression régulière défaillante...
		if (phrases.size() == 0) {
			Phrase p = new Phrase();
			p.phrase = texte;
			phrases.add(p);
		}
		
		return phrases;
	}

	@Override
	public Phrase getPhraseComplète(Contexte c) {
		// TODO Auto-generated method stub
		return null;
	}

}
