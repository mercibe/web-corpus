package service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.servicelibre.corpus.entity.Corpus;
import com.servicelibre.corpus.manager.CorpusManager;
import com.servicelibre.corpus.service.Contexte;
import com.servicelibre.corpus.service.ContexteSet;
import com.servicelibre.corpus.service.CorpusPhraseService;
import com.servicelibre.corpus.service.CorpusService;
import com.servicelibre.corpus.service.FormeService;
import com.servicelibre.corpus.service.Phrase;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class CorpusServiceTest {

	@Autowired
	CorpusManager cm;

	@Autowired
	FormeService formeService;

	private CorpusPhraseService phraseService;

	@Ignore
	@Test
	public void créeTest() {
		Corpus corpus = new Corpus("Corpus de test", "Un nouveau corpus de test");

		CorpusService cs = new CorpusService(cm, corpus);

		assertNotNull("Le corpus doit exister (création))", cs.getCorpus());

		assertTrue("Le corpus doit avoir un ID.", cs.getCorpus().getId() > 0);

	}

	@Ignore
	@Test
	public void ouvreTest() {
		Corpus corpus = new Corpus("Corpus de test", "");

		CorpusService cs = new CorpusService(cm, corpus);

		assertNotNull("Le corpus doit exister (ouverture))", cs.getCorpus());

		assertTrue("Le corpus doit avoir un ID généré.", cs.getCorpus().getId() > 0);

	}

	@Test
	@Ignore
	public void contextesTest() {

		Corpus corpus = new Corpus("Corpus de test nouveau", "");

		corpus.setDossierData(System.getProperty("java.io.tmpdir") + File.separator + "index");

		CorpusService cs = new CorpusService(cm, corpus);

		String mot = "chien";
		ContexteSet contexteSet = cs.getContextesMot(mot);

		assertNotNull("La liste des contextes ne peut être null.", contexteSet.getContextes());
		assertTrue("La liste des contextes de ne peut être vide.", contexteSet.size() > 0);

		System.out.println("Trouvé " + contexteSet.size() + " occurrences du mot " + mot + " dans " + contexteSet.getDocumentCount() + " documents.");

		// System.err.println("# contextes: " + contextes.size());
		// int cpt = 1;
		// for(Contexte c : contextes) {
		// System.out.println("------"+ cpt++ +"------");
		// System.out.println(c);
		// }

	}

	@Test
	@Ignore
	public void contextesLemmeTest() {

		Corpus corpus = new Corpus("Corpus de test nouveau", "");

		corpus.setDossierData(System.getProperty("java.io.tmpdir") + File.separator + "index");

		CorpusService cs = new CorpusService(cm, corpus);
		cs.setFormeService(formeService);

		String lemme = "manger";

		ContexteSet contexteSet = cs.getContextesLemme(lemme);

		assertNotNull("La liste des contextes ne peut être null.", contexteSet.getContextes());
		assertTrue("La liste des contextes de ne peut être vide.", contexteSet.size() > 0);

		System.out.println("Trouvé " + contexteSet.size() + " formes du lemme " + lemme + " dans " + contexteSet.getDocumentCount() + " documents.");

	}

	
	@Test
	public void contextesCorpusPhraseServiceStaticTest() {

		phraseService = new CorpusPhraseService();

		List<String[]> phrasesÀTester = new ArrayList<String[]>();

		// Test règle de base: majuscule + point final
		phrasesÀTester
				.add(new String[] {
						"L’usage le plus courant consiste à placer un guillemet ouvrant au début du dialogue et un guillemet fermant à la fin du dialogue.",
						"On ne sort pas des guillemets au moment des incises, sauf pour celle qui suit éventuellement a dernière réplique.",
						"Les répliques, hormis la première, sont introduites par un tiret cadratin.",
						"Ceci est une phrase exclamative!",
						"Je me le demande, en fait...",
						"En fait de quoi, mon cher Monsieur?",
						"Juste comme cela, pour fin de test…",
						"Freud a voulu bâtir une « science », et il n'y est pas parvenu ; il a voulu « prouver » que l'inconscient avait ses lois, sa logique intrinsèque, ses protocoles expérimentaux." });

		// Test discours direct formel
		phrasesÀTester
				.add(new String[] { "L’ouvreuse m’a dit : « Donnez-moi votre ticket. » avec un grand sourire.",
						"L’ouvreur m’a dit : « Donnez-moi votre billet. »", " Je le lui ai donné.", "La fille m’a dit : « Donnez-moi votre cachet. ».",
						" Je le lui ai donné.", "Le gars m’a dit : « Donnez-moi votre permi de conduire » et me fixa des yeux.",
						"Jean a déclaré : « Je suis malade »." });

		// Test incises diverses
		phrasesÀTester.add(new String[] { "L’ouvreuse m’a dit : « Donnez-moi votre ticket. » avec un grand sourire.",
				"Que faisiez-vous au temps chaud ? dit-elle à cette emprunteuse.", "Encore une fois! s'écria-t-elle, visiblement déçue.",
				"Encore une fois!, s'écria-t-elle, visiblement déçue.", "Qu'as-tu fait hier soir, finalement? lui demanda-t-il.",
				"Qu'as-tu fait hier soir, finalement?, lui demanda-t-il.", "Qu'as-tu fait hier soir... lui demanda-t-il.",
				"Qu'as-tu fait hier soir..., lui demanda-t-il.", "Cette personne —  par ailleurs charmante — a toute mon estime.",
				"Cette personne – charmante par ailleurs – a toute mon estime." });

		// Test dialogues
		phrasesÀTester
				.add(new String[] {
						"« Bonjour, Monsieur. ",
						"— Bonjour, Madame. »",
						"« Dis-moi Prince Rubis, qui es-tu? ",
						"Si tu m'aimes vraiment, dis-moi la vérité. ",
						"D'où viens-tu? »",
						"« J’vais voir si c’est ainsi ! que je crie à Arthur, et me voici parti à m’engager, et au pas de course encore. ",
						"— T’es rien Ferdinand ! » qu’il me crie, lui Arthur en retour, vexé sans doute par l’effet de mon héroïsme sur tout le monde qui nous regardait.",
						"Quelqu'un est arrivé devant la tente, et j'ai demandé: « Qui est-ce? ", "— C'est moi, Ned, Margaret. ",
						"— Qu'est-ce que tu fais là, Mag? ", "— J'ai la frousse. ", "Tu entends ces coups de feu? ", "Je peux rester avec toi? » ",
						"Sans attendre réponse, elle a pris place dans mon lit de camp. ", "Toute habillée."

				});

		// Citation
		phrasesÀTester.add(new String[] {
				"L'énoncé « Les Parisiens se sont emparés de la Bastille le 14 juillet 1789. » est produit par la situation d'énonciation suivante.",
				"La caissière du cinéma m’a recommandé un « film sensationnel » !", });

		// Poésie
		phrasesÀTester.add(new String[] { "La mère à Maillard\nNourrit trois canards\nQui sont pas les siens\nUn tien pour le mien\n\n",
				"Le papa d’Éloi\nÉlevait des oies\nC’est le vieux Perras\nQui les mangera\n", });

		// Énumération (est-ce vraiment souhaité? Souvent phrase incomplète)
		// phrasesÀTester.add(new String[] {
		// "Voici ce que je préfère:\n",
		// "-	mes galops avec papa dans la forêt des Appalaches\n",
		// "-	la couleur des feuilles en automne\n",
		// "-	le bruit des vagues à marée haute\n",
		// "-	les après-midi à ramasser des palourdes dans les rochers du lac Champlain\n"
		//
		// });

		// Interjection (Ex.: Ah! la vache...)

		// Exécution des tests
		for (String[] phrasesTest : phrasesÀTester) {
			String texte = getTexteFromPhrases(phrasesTest);
			List<Phrase> phrases = phraseService.getPhrasesComplètes(texte);

			assertNotNull("phrases ne peut être null", phrases);
			afficherPhrases(phrases, texte);
			assertTrue("Les phrases n'ont pas été correctement identifiées", assertPhrases(phrasesTest, phrases));
		}

		// assertFalse("La 4e phrase doit être incomplète",phrases.get(3).complète);

		// texte =
		// "Tais-toi, idiot! Pourquoi ne viens-tu pas me voir plus souvent ? On ne sort pas si facilement de prison..."
		// +
		// "Les répliques, hormis la première, sont introduites par un tiret cadratin…";
		// phrases = phraseService.getPhrasesComplètes(texte);
		// assertNotNull("phrases ne peut être null", phrases);
		// assertEquals("Mauvais nombre de phrases identifiées.", 4, phrases.size());
		// afficherPhrases(phrases, texte);
		//
		//
		// texte =
		// "C'est alors qu'il me dit: « Je n'en ai que faire. »  Quel toupet! La caissière du cinéma m’a recommandé un « film sensationnel ». "
		// +
		// "Il évoquait la « culture allemande ». Il a un peu (beaucoup?) menti.";
		// phrases = phraseService.getPhrasesComplètes(texte);
		// assertNotNull("phrases ne peut être null", phrases);
		// afficherPhrases(phrases, texte);
		//
		//
		// texte="Parfois, même, il se surprenait à songer : « C’est peut-être une fée! » Le soir, au lieu de ruminer de sombres pensées, Vieux Thomas "
		// +
		// "écoutait le chant des vagues et contemplait sa petite fée sautillant sous les étoiles.   un phrase avec oubli de majuscule. Étonnement, encore une autre..."
		// +
		// "Julia chuchote à l'oreille du chien Chien en lui mordillant nerveusement l'oreille : — Tu vois bien qu'ils attendaient l'obscurité. "
		// +
		// "Ils attendaient seulement qu'il fasse noir. Ils craignaient sûrement de se faire voir... L'un à la suite de l'autre, en file bien rangée, "
		// +
		// "de petits fantômes continuent à entrer par la fenêtre. — Que fais-tu ici? lui lance mon ami mort de rire.  Je l'ignore, me dit-il.";
		// phrases = phraseService.getPhrasesComplètes(texte);
		// assertNotNull("phrases ne peut être null", phrases);
		// afficherPhrases(phrases, texte);
		//
		//
		// texte=" Frédéric à Ti-Mine, quarante-quatre ans, divorcé, la voix éraillée par les Export \"A\", avait confié à son père, "
		// +
		// "au-dessus d'un verre de gin : «Vois-tu, mon André, une femme, aujourd'hui, il faut pas la séduire une fois, comme avant! "
		// +
		// "Il faut la séduire tous les jours!... J'veux dire, au moins une fois par semaine, godême!...» Les amoureux firent glisser "
		// +
		// "la porte et pénétrèrent dans la timonerie. ";
		// phrases = phraseService.getPhrasesComplètes(texte);
		// assertNotNull("phrases ne peut être null", phrases);
		// afficherPhrases(phrases, texte);
		//
		//
		//
		// texte="nos portes ! » Robert, reconnaissant, leva ses pauvres yeux de chien battu vers la princesse. Elle lui sourit. Aussitôt, il en devint amoureux "
		// +
		// " baver de bonheur et à frétiller de la queue pour elle jusqu'à la fin des temps.* Qui mange du chien.De ce jour, Robert ne quitta "
		// +
		// "plus la princesse. Il la suivait partout et, bien vite, l'entourage du roi s'habitua à sa présence. Sauf le sénéchal Enguerrand "
		// +
		// "de La Trémouille, un teigneux, un patte-pelue*, doublé d'un porc qui lorgnait la main de la princesse... ainsi que bien d'autres parties";
		// phrases = phraseService.getPhrasesComplètes(texte);
		// assertNotNull("phrases ne peut être null", phrases);
		// afficherPhrases(phrases, texte);
		//
		// texte="« Eh bien, merci ! dit le chien-saucisse. Marchons ensemble pour voir si nous pouvons atteindre le bout de l’un d’entre nous. » Ils marchèrent "
		// +
		// "pendant des heures et des heures… … et quand ils s’arrêtèrent enfin, l’éléphant se tourna vers le serpent "
		// +
		// "et dit : « Je me demande où est passé le chien-saucisse. On dirait qu’on voit son autre bout, mais lui, où est-il ? » « Je crois que je l’ai mangé"
		// +
		// " tout à l’heure, répondit le serpent. Un peu long à avaler, mais vous savez… j’aime bien les chiens-saucisses. » ";
		// phrases = phraseService.getPhrasesComplètes(texte);
		// assertNotNull("phrases ne peut être null", phrases);
		// afficherPhrases(phrases, texte);
	}

	/**
	 * Vérifie que toutes les phrases identifiées par l'algoritme de découpe en phrase
	 * sont bien identiquent aux phrases de départ.
	 * 
	 * @param phrasesTest
	 * @param phrases
	 * @return
	 */
	private boolean assertPhrases(String[] phrasesTest, List<Phrase> phrases) {

		boolean succès = true;

		if (phrasesTest.length != phrases.size()) {
			afficheComparaison(phrasesTest, phrases);
			return false;
		}

		for (int i = 0; i < phrasesTest.length; i++) {
			if (!phrasesTest[i].trim().equals(phrases.get(i).phrase)) {
				succès = false;
				afficheComparaison(phrasesTest, phrases);
				break;
			}
		}

		return succès;
	}

	private void afficheComparaison(String[] phrasesTest, List<Phrase> phrases) {
		int nbPhrasesIdentifiées = phrases.size();
		if (phrasesTest.length != phrases.size()) {
			System.err.println("Mauvais nombre de phrases identifiées.  Attendu:<" + phrasesTest.length + "> mais identifié:<" + nbPhrasesIdentifiées + ">");
		}

		for (int i = 0; i < phrasesTest.length; i++) {
			if (i < nbPhrasesIdentifiées) {
				if (!phrasesTest[i].trim().equals(phrases.get(i).phrase)) {
					System.err.println("Phrase mal identifiée [" + i + "] :\nAttendu [" + phrasesTest[i] + "]\nTrouvé  [" + phrases.get(i).phrase + "]");
				} else {
					System.out.println("Phrase bien identifiée [" + i + "] : " + phrasesTest[i]);
				}
			} else {
				System.err.println("Phrase non identifiée [" + i + "] : " + phrasesTest[i]);
			}
		}

	}

	private String getTexteFromPhrases(String[] phrases) {
		StringBuilder sb = new StringBuilder();
		for (String phrase : phrases) {
			sb.append(phrase);
		}
		return sb.toString();
	}

	private void afficherPhrases(List<Phrase> phrases, String texte) {
		System.out.println("Trouvé " + phrases.size() + " phrases dans le texte [" + texte + "]");
		int phraseCpt = 1;
		for (Phrase p : phrases) {
			System.out.println(phraseCpt++ + ": " + p.phrase + "[" + p.nettoyée() + "]");
		}
	}

	@Test
	@Ignore
	public void contextesCorpusPhraseServiceTest() {

		Corpus corpus = new Corpus("Corpus de test nouveau", "");

		corpus.setDossierData(System.getProperty("java.io.tmpdir") + File.separator + "index");

		CorpusService cs = new CorpusService(cm, corpus);

		int nbPhrases = 1;
		int nbMoyMotPhrase = 50;

		int tailleVoisinnage = nbPhrases * nbMoyMotPhrase;

		cs.setTailleVoisinnage(tailleVoisinnage);

		// ContexteSet contexteSet = cs.getContextesMot("séduire");
		ContexteSet contexteSet = cs.getContextesMot("chien");

		assertNotNull("La liste des contextes ne peut être null.", contexteSet.getContextes());
		assertTrue("La liste des contextes de ne peut être vide.", contexteSet.size() > 0);

		CorpusPhraseService phraseService = new CorpusPhraseService();

		// TODO assertions!!!!
		System.err.println("# contextes: " + contexteSet.size());
		int cpt = 1;
		for (Contexte c : contexteSet.getContextes()) {
			System.out.println("------" + cpt++ + "------");

			Phrase phrase = phraseService.getPhraseComplète(c);
			System.out.println(phrase.phrase);
			Contexte contexte = phraseService.getContextePhraseComplète(c);
			System.out.println(contexte);

		}
		
	}
	
	@Test
	public void contextesCorpusErreur1Test() {
		
		Corpus corpus = new Corpus("Corpus de test nouveau", "");
		CorpusPhraseService phraseService = new CorpusPhraseService();

		corpus.setDossierData(System.getProperty("java.io.tmpdir") + File.separator + "index");

		CorpusService cs = new CorpusService(cm, corpus);
		
		ContexteSet contexteSet = cs.getContextesMot("E");
		
//		assertNotNull("La liste des contextes ne peut être null.", contexteSet.getContextes());
//		assertTrue("La liste des contextes de ne peut être vide.", contexteSet.size() > 0);
//
//
//		System.err.println("# contextes: " + contexteSet.size());
//		int cpt = 1;
//		for (Contexte c : contexteSet.getContextes()) {
//			System.out.println("------" + cpt++ + "------");
//
//			Phrase phrase = phraseService.getPhraseComplète(c);
//			System.out.println(phrase.phrase);
//			Contexte contexte = phraseService.getContextePhraseComplète(c);
//			System.out.println(contexte);
//
//		}
		
		cs.setTailleVoisinnage(50);
		contexteSet = cs.getContextesMot("Raphaël");
		
		assertNotNull("La liste des contextes ne peut être null.", contexteSet.getContextes());
		assertTrue("La liste des contextes de ne peut être vide.", contexteSet.size() > 0);


		System.err.println("# contextes: " + contexteSet.size());
		int cpt = 1;
		for (Contexte c : contexteSet.getContextes()) {
			System.out.println("------" + cpt++ + "------");

			Phrase phrase = phraseService.getPhraseComplète(c);
			System.out.println(phrase.phrase);
			Contexte contexte = phraseService.getContextePhraseComplète(c);
			System.out.println(contexte);

		}
		
	}

}
