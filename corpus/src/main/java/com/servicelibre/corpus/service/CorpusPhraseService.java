package com.servicelibre.corpus.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CorpusPhraseService implements PhraseService {

	enum Position {
		DANS_PHRASE, DANS_GUILLEMET, PEUT_ÊTRE_FIN_PHRASE, FIN_CITATION_COMPLÈTE, DANS_PARENTHÈSE, DANS_INCISE
	};

	/**
	 * <p>
	 * Ce service a pour objectif principal de découper en phrases du texte brut. L'idée est de dégager des phrases
	 * complètes d'un point de vue syntaxique et, dans une moindre mesure, sémantique.
	 * </p>
	 * 
	 * <p>
	 * L'algorithme de découpe en phrase se base donc essentiellement sur les règles de ponctuation et autres signes
	 * typographiques d'usage standard en français.
	 * </p>
	 * 
	 * <p>
	 * <strong> En français, une phrase est délimitée, à l'écrit, par une majuscule (début) et un signe de ponctuation
	 * de fin de phrase (point, point d'exclamation, point d'interrogation, trois points de suspension et parfois le
	 * double point ou le point virgule).</strong>
	 * </p>
	 * 
	 * <h2>Exceptions à la règle... (pour la découpe en phrase!)</h2>
	 * 
	 * <h3>Discours direct formel</h3>
	 * 
	 * <h4>Guillemets</h4>
	 * <ul>
	 * <li>L’ouvreuse m’a dit : « Donnez-moi votre ticket. » avec un grand sourire.</li>
	 * <li>L’ouvreuse m’a dit : « Donnez-moi votre ticket. » Je le lui ai donné.</li>
	 * <li>L’ouvreuse m’a dit : « Donnez-moi votre ticket. ». Je le lui ai donné.</li> (inesthétique - rare)
	 * <li>L’ouvreuse m’a dit : « Donnez-moi votre ticket » et me fixa des yeux.</li>
	 * <li>Jean a déclaré : « Je suis malade ».</li>
	 * </ul>
	 * 
	 * <h4>Incises</h4>
	 * <ul>
	 * <li>Que faisiez-vous au temps chaud ? dit-elle à cette emprunteuse.</li>
	 * <li>Encore une fois! s'écria-t-elle, visiblement déçue.</li>
	 * <li>Encore une fois!, s'écria-t-elle, visiblement déçue.</li> (inesthétique - rare)
	 * <li>Qu'as-tu fait hier soir, finalement? lui demanda-t-il.</li>
	 * <li>Qu'as-tu fait hier soir, finalement?, lui demanda-t-il.</li> (inesthétique - rare)
	 * <li>Qu'as-tu fait hier soir... lui demanda-t-il.</li>
	 * <li>Qu'as-tu fait hier soir..., lui demanda-t-il.</li>inesthétique - rare)
	 * <li>Cette personne —  par ailleurs charmante — a toute mon estime.</li> (tiret long / cadratin)
	 * <li>Cette personne – charmante par ailleurs – a toute mon estime.</li> (tiret moyen / demi-cadratin)
	 * </ul>
	 * 
	 * <h4>Dialogue</h4>
	 * 
	 * <ul>
	 * <li>« J’vais voir si c’est ainsi ! que je crie à Arthur, et me voici parti à m’engager, et au pas de course
	 * encore. — T’es rien c… Ferdinand ! » qu’il me crie, lui Arthur en retour, vexé sans doute par l’effet de mon
	 * héroïsme sur tout le monde qui nous regardait.</li>
	 * <li>« Bonjour, Monsieur. — Bonjour, Madame. »</li>
	 * <li>« Dis-moi Prince Rubis, qui es-tu? Si tu m'aimes vraiment, dis-moi la vérité. D'où viens-tu? »</li>
	 * <li>Quelqu'un est arrivé devant la tente, et j'ai demandé: « Qui est-ce? — C'est moi, Ned, Margaret. — Qu'est-ce
	 * que tu fais là, Mag? — J'ai la frousse. Tu entends ces coups de feu? Je peux rester avec toi? » Sans attendre
	 * réponse, elle a pris place dans mon lit de camp. Toute habillée.</li>
	 * 
	 * </ul>
	 * 
	 * <strong>Note:</strong>Guillemets remplacés de plus en plus souvent par des tirets (habituellement cadratin: —)
	 * 
	 * <h3>Discours direct libre</h3>
	 * <p>
	 * Rien à faire de particulier - impossible/très difficile à identifier
	 * </p>
	 * <strong>Exemple:</strong> S'adressant à son élève, le professeur se mit
	 * alors en colère. Je ne supporte plus ta paresse ! Je finirai par ne plus
	 * m'occuper de toi si tu trouves sans arrêt des excuses pour ne pas faire
	 * tes devoirs !
	 * 
	 * <h3>Dialogue</h3>
	 * 
	 * « Aujourd'hui, je ne n'irai pas travailler », a-t-il ajouté en se
	 * remettant au lit.
	 * 
	 * <h3>Citation</h3> <h4>Complète</h4>
	 * <ul>
	 * <li>L'énoncé « Les Parisiens se sont emparés de la Bastille le 14 juillet 1789. » est produit par la situation
	 * d'énonciation suivante.</li>
	 * </ul>
	 * <h4>Partielle</h4>
	 * <ul>
	 * <li>La caissière du cinéma m’a recommandé un « film sensationnel » !</li>
	 * </ul>
	 * 
	 * <h3>Poésie</h3>
	 * 
	 * <p>
	 * Il faut se fier à la ponctuation normale. Si elle est absente comme dans l'exemple qui suit, l'algorithme
	 * retourne le vers au complet en se basant sur les retours à la ligne pour sa délimitation.
	 * </p>
	 * 
	 * <pre>
	 * Je voudrais avoir un chien
	 * Qui me donnerait la patte 
	 * Je voudrais avoir un chien 
	 * Qui me lécherait la main 
	 * Et quand je n’aurais pas d’amis 
	 * Pour aller jouer au parc 
	 * Quand je n’aurais pas d’amis 
	 * Je jouerais avec mon chien
	 * </pre>
	 * 
	 * <h3>Abréviations</h3>
	 * 
	 * <ul>
	 * <li>J'ai fait ma B.A. aujourd'hui.</li>
	 * <li>Demain je dois acheter des pommes, des poires, etc.</li>
	 * <li>J'en ai parlé à B. Mercier qui me l'a confirmé.</li>
	 * <li>Cela n'en fini plus etc. etc.</li>
	 * <li>Dans une énumération, etc. est toujours précédé d’une virgule.</li>
	 * <li>Mes amis, c.-à-d. vous, écoutez-moi!</li>
	 * </ul>
	 * 
	 * <h3>Autres cas particuliers</h3> <h4>Guillemets ironiques</h4>
	 * <ul>
	 * <li>Il évoquait la « culture allemande ».</li>
	 * <li>Freud a voulu bâtir une « science », et il n'y est pas parvenu ; il a voulu « prouver » que l'inconscient
	 * avait ses lois, sa logique intrinsèque, ses protocoles expérimentaux — mais, hélas, il a un peu (beaucoup ?)
	 * menti pour se parer des emblèmes de la scientificité.</li>
	 * <li>La recherche des ingrédients locaux propres à assaisonner d'un brin de « réalisme » (c'est là un de ces mots
	 * qui n'ont de sens qu'entre guillemets) la recette de l'imagination personnelle s'avéra une tâche beaucoup plus
	 * pénible, à cinquante ans, qu'elle ne l'avait été pendant ma jeunesse européenne, quand l'automatisme de ma
	 * réceptivité et de ma mémoire était à son apogée.</li>
	 * <li>Une phrase peut se terminer par 1 faux point de suspension...</li>
	 * <li>Le seul véritable point de suspension est…</li>
	 * <li>Pour indiquer un passage coupé dans une citation, on emploie les points de suspension entre crochets : « […]
	 * ».</li>
	 * <li>L’âme, c’est-à-dire le principe intelligent et immortel.</li>
	 * 
	 * </ul>
	 * 
	 * <h3>Quelques comportement généraux de l'algorithme</h3>
	 * <ul>
	 * <li>un point précédé immédiatement d'une majuscule n'est pas une fin de phrase (ex. B. Mercier)</li>
	 * <li>Un point de fin de phrase (et autres ponctuations de fin de phrase) est suivi obligatoirement d'une majuscule
	 * ou retour à la ligne</li>
	 * <li>un retour à la ligne non précédé de « : » est considéré comme une fin de phrase</li>
	 * </ul>
	 * 
	 * 
	 * <h3>Références</h3>
	 * <ul>
	 * <li>http://fr.wikipedia.org/wiki/Tiret</li>
	 * <li>http://fr.wikipedia.org/wiki/Guillemet#Usage_des_guillemets_en_fran.C3. A7ais</li>
	 * <li>http://66.46.185.79/bdl/gabarit_bdl.asp?id=3407</li>
	 * </ul>
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
	@Deprecated
	public List<Phrase> getPhrasesComplètesOld(String texte) {

		List<Phrase> phrases = new ArrayList<Phrase>();

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
				} else {
					// Point de suspension (ou plus!)?
					if ((curPos + 1) < charArray.length && charArray[curPos + 1] == '.') {
						phraseBuffer[phraseBufferPos++] = curChar;
						continue;
					}
				}

				// TODO est-ce le point d'une abréviation?

				if (position == Position.DANS_PHRASE || position == Position.DANS_INCISE) {
					// la phrase est terminée
					finPhrase(phrases, curPhrase, curChar, phraseBuffer, phraseBufferPos);

					curPhrase = null;
					phraseBufferPos = 0;
					position = Position.DANS_PHRASE;
				} else if (position == Position.DANS_GUILLEMET) {
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
				position = Position.DANS_GUILLEMET;
				phraseBuffer[phraseBufferPos++] = curChar;
				break;
			case '»':
				if (position == Position.FIN_CITATION_COMPLÈTE) {
					// Si la première lettre qui suit est une majuscule ou un point sur le chemin=> fin de phrase, sinon
					// la phrase continue
					// FIXME
					int finPotentielle = getFinCitationComplèteQuiTerminePhrase(charArray, curPos + 1);
					if (finPotentielle > curPos + 1) {
						finPhrase(phrases, curPhrase, curChar, phraseBuffer, phraseBufferPos);
						curPhrase = null;
						phraseBufferPos = 0;
					} else {
						phraseBuffer[phraseBufferPos++] = curChar;
					}
					position = Position.DANS_PHRASE;

				} else if (position == Position.DANS_GUILLEMET) {
					// citation incomplète
					phraseBuffer[phraseBufferPos++] = curChar;
					position = Position.DANS_PHRASE;
				} else if (position == Position.DANS_INCISE) {
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

	/**
	 * Retourne le texte passé en paramètre sous forme de phrases
	 */
	@Override
	public List<Phrase> getPhrasesComplètes(String texte) {

		List<Phrase> phrases = new ArrayList<Phrase>();

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
			case '»':
			case '\n':

				if (curChar == '.') {
					// Point de suspension (ou plus!)?
					if ((curPos + 1) < charArray.length && charArray[curPos + 1] == '.') {
						phraseBuffer[phraseBufferPos++] = curChar;
						continue;
					}
				}
				
				if(curChar == '»') {
					position = Position.DANS_PHRASE;
				}
				
				if (isFinPhrase(charArray, curPos)) {
					// la phrase est terminée
					finPhrase(phrases, curPhrase, curChar, phraseBuffer, phraseBufferPos);

					curPhrase = null;
					phraseBufferPos = 0;
					position = Position.DANS_PHRASE;
				} else {
					phraseBuffer[phraseBufferPos++] = curChar;

				}
				break;
			case '«':
				position = Position.DANS_GUILLEMET;
				phraseBuffer[phraseBufferPos++] = curChar;
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

	/**
	 * La phrase est terminée si cette ponctuation finale est suivie immédiatement d'une majuscule (sans autre caractère
	 * terminaux sur le chemin) ou d'un retour à la ligne (\n)
	 * 
	 * @param charArray
	 * @param curPos
	 * @return
	 */
	private boolean isFinPhrase(char[] buffer, int posCandidatCarFinal) {

		int pos = posCandidatCarFinal + 1;
		char candidatCar = buffer[posCandidatCarFinal];
		while (pos < buffer.length) {
			char c = buffer[pos];
			if (Character.isLetter(c)) {
				if (Character.isUpperCase(c)) {
					return true;
				} else {
					return false;
				}
				
			}
			else if(c == '»') {
				return false;
			}
			else if (c == '\n') {
				// FIXME pour poésie
				return true;
			}
			else if(c == '.' || c == '!') {
				return false;
			}
			pos++;
		}

		return true;
	}

	/**
	 * Prochaine lettre majuscule ou un point sur le chemin
	 * 
	 * @param buffer
	 * @param start
	 * @return
	 */
	private int getFinCitationComplèteQuiTerminePhrase(char[] buffer, int start) {
		int pos = start;
		while (pos < buffer.length && !Character.isLetter(buffer[pos]) && buffer[pos] != '.') {
			pos++;
		}
		if (pos < buffer.length) {
			if (Character.isUpperCase(buffer[pos])) {
				return pos - 1;
			} else if (buffer[pos] == '.') {
				return pos;
			}
		}

		return start;
	}

	private void finPhrase(List<Phrase> phrases, Phrase curPhrase, char curChar, char[] buffer, int endBuffer) {

		buffer[endBuffer] = curChar;

		curPhrase.phrase = new String(Arrays.copyOfRange(buffer, 0, endBuffer + 1)).trim();

		// FIXME si position == DANS_INCISE => supprimer chevron éventuellement
		// ouvrant ? (si pas de fermant)

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

		int max = c.texteAvant.length();

		// FIXME hack horrible
		Phrase phrasePrécédente = null;

		int posCpt = 0;
		for (Phrase phrase : phrasesComplètes) {
			posCpt += phrase.phrase.length();
			if (posCpt >= max) {
				if (phrase.phrase.toLowerCase().indexOf(c.mot.toLowerCase()) < 0) {
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

		if (phrase != null) {
			// si mot n'est pas dans phrase: erreur!!!
			String phraseComplète = phrase.phrase;
			if (phraseComplète.indexOf(c.mot) < 0) {
				System.err.println("ERREUR!!!! " + c.mot + " ne se trouve pas dans " + phraseComplète);
				return null;
			}
			// reconstruction d'un contexte à partir de la phrase (offset si
			// plusieurs fois même mot dans la phrase, sinon tjrs première
			// occurence retournée)

			int positionPhraseDansContexte = c.getPhrase().phrase.indexOf(phraseComplète);
			if (positionPhraseDansContexte < 0) {
				System.err.println("c.texteAvant.length() - c.getPhrase().phrase.indexOf(phrase.phrase) => " + c.texteAvant.length() + "-"
						+ Math.max(0, positionPhraseDansContexte));
				System.err.println("Cherche [" + phraseComplète + "] dans [" + c.getPhrase().phrase + "]");
				return c;
			}

			int offset = c.texteAvant.length() - Math.max(0, positionPhraseDansContexte);

			int débutMot = phraseComplète.indexOf(c.mot, Math.max(offset - 1, 0));
			int finMot = débutMot + c.mot.length();
			// FIXME problème trim / blanc insécable?
			return new Contexte(phraseComplète.substring(0, débutMot), phraseComplète.substring(débutMot, finMot), phraseComplète.substring(finMot));
		}
		return null;
	}
	
}