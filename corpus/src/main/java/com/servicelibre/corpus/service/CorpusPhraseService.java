package com.servicelibre.corpus.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CorpusPhraseService implements PhraseService {

	enum Position {
		DANS_PHRASE, DANS_CHEVRON, PEUT_ÊTRE_FIN_PHRASE, FIN_CITATION_COMPLÈTE, DANS_PARENTHÈSE, DANS_INCISE
	};

	/**
	 * En français, une phrase est délimitée, à l'écrit, par une majuscule
	 * (début) et un signe de ponctuation de fin de phrase (point, point
	 * d'exclamation, point d'interrogation, trois points de suspension et
	 * parfois le double point ou le point virgule).
	 * 
	 * Incises (http://66.46.185.79/bdl/gabarit_bdl.asp?id=3407) [Je n'arrive
	 * pas à croire, *dit-il*, que tu as oublié ce que je t'avais demandé.] [Il
	 * veut sûrement me faire comprendre, *pensa-t-elle*, que ce genre de chose
	 * ne l'intéresse pas.] [Encore une fois!(,) s'écria-t-elle, visiblement
	 * déçue.] [Qu'as-tu fait hier soir, finalement?(,) lui demanda-t-il.]
	 * [Qu'as-tu fait hier soir...(,) lui demanda-t-il.]
	 * 
	 * (http://fr.wikipedia.org/wiki/Guillemet#Usage_des_guillemets_en_fran.C3.
	 * A7ais) Citation
	 * 
	 * blabla: « Je n'en ai que faire. » (citation phrase entière, deux-points
	 * ouvrants les guillemets, majuscule + ponctuation) la caissière du cinéma
	 * m’a recommandé un « film sensationnel » ! (citation morceau, minuscule et
	 * pas de ponctuation)
	 * 
	 * Quid Dialogue ? partie gauche du : et droite du tiret cadratin => 1 ou 2
	 * phrases?
	 * 
	 * Guillemets ironiques [Il évoquait la « culture allemande ».]
	 * 
	 * Abréviations
	 * Utiliser FormeService pour le savoir en lui passant toutes les lettres qui précèdent ce point (jusqu'au premier séparateur de mot)
		B.A. etc. B. A. c.-a.-d. c.a.d.
		Abréviation prénom: B. Mercier => si point précédé immédiatement d'une majuscule, toujours ignorer?
	 * 
	 * Phrases particulières
	 * 
	 * Il a un peu (beaucoup?) menti.
	 * 
	 * 1 phrase formelle, 2 propositions indépendantes [C'est les vacances : je
	 * fais la grasse matinée.]
	 * 
	 * Syntaxe débordant du cadre (figure de style, 3 phrases formelles = une
	 * seule syntaxiquement) [Et j'ai connu un moment de désespoir absolu. Le
	 * premier de ma vie consciente. Un moment de haine pure, aussi, envers
	 * cette femme altière et ses manigances. ]
	 * 
	 * Quid poème? (majuscule + retour à la ligne) Je voudrais avoir un chien
	 * Qui me donnerait la patte Je voudrais avoir un chien Qui me lécherait la
	 * main Et quand je n’aurais pas d’amis Pour aller jouer au parc Quand je
	 * n’aurais pas d’amis Je jouerais avec mon chien
	 * 
	 */
	@Override
	public List<Phrase> getPhrasesComplètes(Contexte c) {
		
		// Reconstitution d'une string complète
		StringBuilder sb = new StringBuilder(c.texteAvant).append(c.mot).append(c.texteAprès);

		return getPhrasesComplètes(sb.toString());

	}

	/**
	 * Retourne le texte passé en paramètre sous forme de phrases
	 */
	@Override
	public List<Phrase> getPhrasesComplètes(String texte) {

		List<Phrase> phrases = new ArrayList<Phrase>();

		// remplacements pour simplification traitement (pas optimal)
		//texte = texte.replaceAll("\\.\\.\\.", "…");

		char[] charArray = texte.toCharArray();

		Phrase curPhrase = null;
		char curChar;
		char[] phraseBuffer = new char[charArray.length];
		int phraseBufferPos = 0;
		Position position = Position.DANS_PHRASE;

		for (int curPos = 0; curPos < charArray.length; curPos++) {

			curChar = charArray[curPos];

			if (curPhrase == null) {
				curPhrase = new Phrase();
			}

			switch (curChar) {
			case '!':
			case '?':
			case '…':
			case '.':

				if (curChar != '.') {
					// si le premier caractère qui suit est minuscule, phrase
					// pas terminée
					if (Character.isLowerCase(getPremièreLettre(charArray, curPos))) {
						phraseBuffer[phraseBufferPos++] = curChar;
						position = Position.DANS_INCISE;
						continue;
					}
				}
				else {
					// Point de suspension (ou plus!)?
					if( (curPos + 1) < charArray.length && charArray[curPos+1] == '.'){
						phraseBuffer[phraseBufferPos++] = curChar;
						continue;
					}
				}
				
				//TODO est-ce le point d'une abréviation?  

				if (position == Position.DANS_PHRASE || position == Position.DANS_INCISE) {
					// la phrase est terminée
					finPhrase(phrases, curPhrase, curChar, phraseBuffer, phraseBufferPos);

					curPhrase = null;
					phraseBufferPos = 0;
					position = Position.DANS_PHRASE;
				} else if (position == Position.DANS_CHEVRON) {
					phraseBuffer[phraseBufferPos++] = curChar;
					position = Position.FIN_CITATION_COMPLÈTE;
				} else { // on ignore les ponctuations finales entre
							// parenthèse
					phraseBuffer[phraseBufferPos++] = curChar;
				}

				break;
			case '(':
				position = Position.DANS_PARENTHÈSE;
				phraseBuffer[phraseBufferPos++] = curChar;
				break;
			case ')':
				position = Position.DANS_PHRASE;
				phraseBuffer[phraseBufferPos++] = curChar;
				break;
			case '«':
				position = Position.DANS_CHEVRON;
				phraseBuffer[phraseBufferPos++] = curChar;
				break;
			case '»':
				if (position == Position.FIN_CITATION_COMPLÈTE) {
					finPhrase(phrases, curPhrase, curChar, phraseBuffer, phraseBufferPos);
					curPhrase = null;
					phraseBufferPos = 0;
					position = Position.DANS_PHRASE;
				} else if (position == Position.DANS_CHEVRON) {
					// citation incomplète
					phraseBuffer[phraseBufferPos++] = curChar;
					position = Position.DANS_PHRASE;
				} else if(position == Position.DANS_INCISE) {
					phraseBuffer[phraseBufferPos++] = curChar;
					position = Position.DANS_PHRASE;
				}
				break;
			default:
				phraseBuffer[phraseBufferPos++] = curChar;
				break;
			}

		}
		// Si pas de point final... (ou autre)
		if (phraseBufferPos > 0) {
			curPhrase.phrase = new String(Arrays.copyOfRange(phraseBuffer, 0, phraseBufferPos)).trim();
			if (!curPhrase.phrase.isEmpty()) {
				curPhrase.complète = false; // manque ponctuation finale
				phrases.add(curPhrase);
			}
		}

		return phrases;
	}

	private void finPhrase(List<Phrase> phrases, Phrase curPhrase, char curChar, char[] buffer, int endBuffer) {

		buffer[endBuffer] = curChar;

		curPhrase.phrase = new String(Arrays.copyOfRange(buffer, 0, endBuffer + 1)).trim();
		
		// FIXME si position == DANS_INCISE => supprimer chevron éventuellement ouvrant ? (si pas de fermant)
		

		// Si première lettre n'est pas une majuscule, phrase
		// incomplète - manque majuscule
		char premièreLettre = getPremièreLettre(buffer, 0);
		if (Character.isUpperCase(premièreLettre)) {
			curPhrase.complète = true;
		} else {
			curPhrase.complète = false;
		}

		phrases.add(curPhrase);

	}

	private char getPremièreLettre(char[] buffer, int start) {
		for (int i = start; i < buffer.length; i++) {
			if (Character.isLetter(buffer[i])) {
				return buffer[i];
			}
		}
		return 0;
	}

	@Override
	public Phrase getPhraseComplète(Contexte c) {
		
		// Reconstitution d'une string complète
		List<Phrase> phrasesComplètes = getPhrasesComplètes(c);
		
		int max = c.texteAvant.trim().length();// + c.mot.length();
		
		//FIXME hack horrible
		Phrase phrasePrécédente = null;
		
		int posCpt = 0;
		for(Phrase phrase: phrasesComplètes) {
			posCpt += phrase.phrase.length();
			//System.out.println(posCpt + "/" + max + ": " + phrase.phrase);
			if(posCpt >= max-1) {
				if(phrase.phrase.toLowerCase().indexOf(c.mot.toLowerCase()) < 0){
					System.err.println("ERREUR!! Le mot [" +c.mot + "] ne se trouve pas dans \n[" + phrase.phrase + "]\n\n Le contexte complet est \n [" + c.toString() + "]\n");
					return phrasePrécédente;
				}
				return phrase;
			}
			phrasePrécédente = phrase;
		}
		return null;
	}

	@Override
	public Contexte getContextePhraseComplète(Contexte c) {
		
		Phrase phrase = getPhraseComplète(c);
		
		if(phrase != null) {
			// si mot n'est pas dans phrase: erreur!!!
			String phraseComplète = phrase.phrase;
			if(phraseComplète.indexOf(c.mot) < 0) {
				System.err.println("ERREUR!!!! " + c.mot + " ne se trouve pas dans " + phraseComplète);
				return null;
			}
			// reconstruction d'un contexte à partir de la phrase (offset si plusieurs fois même mot dans la phrase, sinon tjrs première occurence retournée)
			
			int positionPhraseDansContexte = c.getPhrase().phrase.indexOf(phraseComplète);
			if(positionPhraseDansContexte < 0) {
				System.err.println("c.texteAvant.length() - c.getPhrase().phrase.indexOf(phrase.phrase) => " + c.texteAvant.length() + "-" + Math.max(0,positionPhraseDansContexte));
				System.err.println("Cherche ["+phraseComplète+"] dans ["+c.getPhrase().phrase+"]");
				return c;
			}
			
			int offset = c.texteAvant.length() - Math.max(0,positionPhraseDansContexte);
			
			int débutMot = phraseComplète.indexOf(c.mot,Math.max(offset-1,0));
			int finMot = débutMot + c.mot.length();
			// FIXME problème trim / blanc insécable?
			return new Contexte(phraseComplète.substring(0, débutMot), phraseComplète.substring(débutMot, finMot ), phraseComplète.substring(finMot));
		}
		return null;
	}
}
