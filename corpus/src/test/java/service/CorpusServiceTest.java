package service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.*;

import java.io.File;
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
import com.servicelibre.corpus.service.RegexpPhraseService;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class CorpusServiceTest {

	@Autowired
	CorpusManager cm;

	@Autowired
	FormeService formeService;

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

	@Ignore
	@Test
	public void contextesRegexpPhraseServiceTest() {

		Corpus corpus = new Corpus("Corpus de test nouveau", "");

		corpus.setDossierData(System.getProperty("java.io.tmpdir") + File.separator + "index");

		CorpusService cs = new CorpusService(cm, corpus);

		int nbPhrases = 1;
		int nbMoyMotPhrase = 30;

		int tailleVoisinnage = nbPhrases * nbMoyMotPhrase / 2;

		cs.setTailleVoisinnage(tailleVoisinnage);

		ContexteSet contexteSet = cs.getContextesMot("chien");

		assertNotNull("La liste des contextes ne peut être null.", contexteSet.getContextes());
		assertTrue("La liste des contextes de ne peut être vide.", contexteSet.size() > 0);

		RegexpPhraseService phraseService = new RegexpPhraseService();

		System.err.println("# contextes: " + contexteSet.size());
		int cpt = 1;
		for (Contexte c : contexteSet.getContextes()) {
			System.out.println("------" + cpt++ + "------");

			List<Phrase> phrases = phraseService.getPhrasesComplètes(c);

			for (Phrase p : phrases) {
				System.out.println(p.phrase);
			}
		}

	}

	@Test
	public void contextesCorpusPhraseServiceStaticTest() {

		String texte = "L’usage le plus courant consiste à placer un guillemet ouvrant au début du dialogue et un guillemet " +
				"fermant à la fin du dialogue. On ne sort pas des guillemets au moment des incises, sauf pour celle qui suit éventuellement " +
				"la dernière réplique. Les répliques, hormis la première, sont introduites par un tiret cadratin. Ceci est une phrase qui";

		CorpusPhraseService phraseService = new CorpusPhraseService();

		List<Phrase> phrases = phraseService.getPhrasesComplètes(texte);

		assertNotNull("phrases ne peut être null", phrases);
		assertEquals("Mauvais nombre de phrases identifiées.", 4, phrases.size());
		afficherPhrases(phrases, texte);
		assertFalse("La 4e phrase doit être incomplète",phrases.get(3).complète);
		
		
		texte = "Tais-toi, idiot! Pourquoi ne viens-tu pas me voir plus souvent ? On ne sort pas si facilement de prison..." +
		"Les répliques, hormis la première, sont introduites par un tiret cadratin…";
		phrases = phraseService.getPhrasesComplètes(texte);
		assertNotNull("phrases ne peut être null", phrases);
		assertEquals("Mauvais nombre de phrases identifiées.", 4, phrases.size());
		afficherPhrases(phrases, texte);
		
		
		texte = "C'est alors qu'il me dit: « Je n'en ai que faire. »  Quel toupet! La caissière du cinéma m’a recommandé un « film sensationnel ». " +
				"Il évoquait la « culture allemande ». Il a un peu (beaucoup?) menti.";
		phrases = phraseService.getPhrasesComplètes(texte);
		assertNotNull("phrases ne peut être null", phrases);
		afficherPhrases(phrases, texte);
		
		
		texte="Parfois, même, il se surprenait à songer : « C’est peut-être une fée! » Le soir, au lieu de ruminer de sombres pensées, Vieux Thomas " +
				"écoutait le chant des vagues et contemplait sa petite fée sautillant sous les étoiles.   un phrase avec oubli de majuscule. Étonnement, encore une autre..." +
				"Julia chuchote à l'oreille du chien Chien en lui mordillant nerveusement l'oreille : — Tu vois bien qu'ils attendaient l'obscurité. " +
				"Ils attendaient seulement qu'il fasse noir. Ils craignaient sûrement de se faire voir... L'un à la suite de l'autre, en file bien rangée, " +
				"de petits fantômes continuent à entrer par la fenêtre. — Que fais-tu ici? lui lance mon ami mort de rire.  Je l'ignore, me dit-il.";
		phrases = phraseService.getPhrasesComplètes(texte);
		assertNotNull("phrases ne peut être null", phrases);
		afficherPhrases(phrases, texte);
		
		
		texte=" Frédéric à Ti-Mine, quarante-quatre ans, divorcé, la voix éraillée par les Export \"A\", avait confié à son père, " +
				"au-dessus d'un verre de gin : «Vois-tu, mon André, une femme, aujourd'hui, il faut pas la séduire une fois, comme avant! " +
				"Il faut la séduire tous les jours!... J'veux dire, au moins une fois par semaine, godême!...» Les amoureux firent glisser " +
				"la porte et pénétrèrent dans la timonerie. ";
		phrases = phraseService.getPhrasesComplètes(texte);
		assertNotNull("phrases ne peut être null", phrases);
		afficherPhrases(phrases, texte);
		
		
		
		texte="nos portes ! » Robert, reconnaissant, leva ses pauvres yeux de chien battu vers la princesse. Elle lui sourit. Aussitôt, il en devint amoureux " +
				" baver de bonheur et à frétiller de la queue pour elle jusqu'à la fin des temps.* Qui mange du chien.De ce jour, Robert ne quitta " +
				"plus la princesse. Il la suivait partout et, bien vite, l'entourage du roi s'habitua à sa présence. Sauf le sénéchal Enguerrand " +
				"de La Trémouille, un teigneux, un patte-pelue*, doublé d'un porc qui lorgnait la main de la princesse... ainsi que bien d'autres parties";
		phrases = phraseService.getPhrasesComplètes(texte);
		assertNotNull("phrases ne peut être null", phrases);
		afficherPhrases(phrases, texte);
		
		texte="« Eh bien, merci ! dit le chien-saucisse. Marchons ensemble pour voir si nous pouvons atteindre le bout de l’un d’entre nous. » Ils marchèrent " +
				"pendant des heures et des heures… … et quand ils s’arrêtèrent enfin, l’éléphant se tourna vers le serpent " +
				"et dit : « Je me demande où est passé le chien-saucisse. On dirait qu’on voit son autre bout, mais lui, où est-il ? » « Je crois que je l’ai mangé" +
				" tout à l’heure, répondit le serpent. Un peu long à avaler, mais vous savez… j’aime bien les chiens-saucisses. » ";
		phrases = phraseService.getPhrasesComplètes(texte);
		assertNotNull("phrases ne peut être null", phrases);
		afficherPhrases(phrases, texte);
	}

	private void afficherPhrases(List<Phrase> phrases, String texte) {
		System.out.println("Trouvé " + phrases.size() + " phrases dans le texte [" + texte + "]");
		int phraseCpt = 1;
		for (Phrase p : phrases) {
				System.out.println(phraseCpt++ + ": " + p.phrase + "["+ (p.complète?"complète":"incomplète") +"]");
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

		int tailleVoisinnage = nbPhrases * nbMoyMotPhrase ;

		cs.setTailleVoisinnage(tailleVoisinnage);

		//ContexteSet contexteSet = cs.getContextesMot("séduire");
		ContexteSet contexteSet = cs.getContextesMot("chien");

		assertNotNull("La liste des contextes ne peut être null.", contexteSet.getContextes());
		assertTrue("La liste des contextes de ne peut être vide.", contexteSet.size() > 0);

		CorpusPhraseService phraseService = new CorpusPhraseService();

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
