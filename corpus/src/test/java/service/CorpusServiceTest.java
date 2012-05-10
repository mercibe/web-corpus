package service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.entity.Corpus;
import com.servicelibre.corpus.metadata.Metadata;
import com.servicelibre.corpus.repository.CorpusRepository;
import com.servicelibre.corpus.repository.DocMetadataRepository;
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
    CorpusRepository corpusRepo;

    @Autowired
    DocMetadataRepository docMetadataRepo;

    @Autowired
    FormeService formeService;

    private CorpusPhraseService phraseService;

    @Test
    public void créeTest() {
	Corpus corpus = new Corpus("Corpus de test", "Un nouveau corpus de test");

	CorpusService cs = new CorpusService(corpusRepo, corpus);

	assertNotNull("Le corpus doit exister (création))", cs.getCorpus());

	assertTrue("Le corpus doit avoir un ID.", cs.getCorpus().getId() > 0);

    }

    @Ignore
    @Test
    public void ouvreTest() {
	Corpus corpus = new Corpus("Corpus de test", "");

	CorpusService cs = new CorpusService(corpusRepo, corpus);

	assertNotNull("Le corpus doit exister (ouverture))", cs.getCorpus());

	assertTrue("Le corpus doit avoir un ID généré.", cs.getCorpus().getId() > 0);

    }

    @Test
    public void contextesTest() {

	Corpus corpus = new Corpus("Corpus de test nouveau", "");

	String dossierData = System.getProperty("java.io.tmpdir") + File.separator + "index";
	corpus.setDossierData(dossierData);

	CorpusService cs = new CorpusService(corpusRepo, corpus);

	String mot = "chien";
	ContexteSet contexteSet = cs.getContextesMot(mot);

	assertNotNull("La liste des contextes ne peut être null.  Le dossier des données existe-t-il ? " + dossierData, contexteSet.getContextes());
	assertTrue("La liste des contextes de ne peut être vide.  Le dossier des données existe-t-il ? " + dossierData, contexteSet.size() > 0);

	System.out.println("Trouvé " + contexteSet.size() + " occurrences du mot " + mot + " dans " + contexteSet.getDocumentCount() + " documents.");

	// System.err.println("# contextes: " + contextes.size());
	// int cpt = 1;
	// for(Contexte c : contextes) {
	// System.out.println("------"+ (cpt++) +"------");
	// System.out.println(c);
	// }

    }

    @Transactional
    @Test
    public void contextesLemmeTest() {

	Corpus corpus = new Corpus("Corpus de test nouveau", "");

	corpus.setDossierData(System.getProperty("java.io.tmpdir") + File.separator + "index");

	// DocMetadata docMetadata = new DocMetadata("Catégorie",
	// "La catégorie du document", "categorie", 40, corpus);

	// docMetadata.setCorpus(corpus);

	// corpus.ajouterDocMetadata(docMetadata);

	CorpusService cs = new CorpusService(corpusRepo, corpus);
	cs.setFormeService(formeService);

	String lemme = "manger";

	ContexteSet contexteSet = cs.getContextesLemme(lemme);

	assertNotNull("La liste des contextes ne peut être null.", contexteSet.getContextes());
	assertTrue("La liste des contextes de ne peut être vide.", contexteSet.size() > 0);

	System.out.println("Trouvé " + contexteSet.size() + " formes du lemme " + lemme + " dans " + contexteSet.getDocumentCount() + " documents.");

	// FIXME devrait fonctionner!
//	lemme = "grand-père";
//	contexteSet = cs.getContextesLemme(lemme);
//
//	assertNotNull("La liste des contextes ne peut être null.", contexteSet.getContextes());
//	assertTrue("La liste des contextes de ne peut être vide.", contexteSet.size() > 0);
//
//	System.out.println("Trouvé " + contexteSet.size() + " formes du lemme " + lemme + " dans " + contexteSet.getDocumentCount() + " documents.");
	
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

	// Plusieurs retour à la ligne
	phrasesÀTester
		.add(new String[] {
			"Et il s'endort souvent.",
			"Comme un enfant.",
			"La preuve...\n\n\n",
			"Alors, Jüll glisse son épée entre les bras de son grand-père et lui chuchote à l'oreille :\n- Ne t'inquiète pas, quand je vais être grand, je serai ton grand-papa.\n",
			"Ton grand-papa en or." });

	// Plusieurs !!! ou ???
	phrasesÀTester.add(new String[] { "Quelle histoire!!!", "Et tu veux que je viennent?????", "Saperlipopette!?!?..." });

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

    }

    /**
     * Vérifie que toutes les phrases identifiées par l'algoritme de découpe en
     * phrase sont bien identiquent aux phrases de départ.
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

	CorpusService cs = new CorpusService(corpusRepo, corpus);

	int nbPhrases = 1;
	int nbMoyMotPhrase = 50;

	int tailleVoisinnage = nbPhrases * nbMoyMotPhrase;

	cs.setTailleVoisinage(tailleVoisinnage);

	String[] mots = { "papa", "chien", "séduire", "maman", "mange" };
	CorpusPhraseService phraseService = new CorpusPhraseService();

	for (String mot : mots) {

	    ContexteSet contexteSet = cs.getContextesMot(mot);

	    assertNotNull("La liste des contextes ne peut être null.", contexteSet.getContextes());
	    assertTrue("La liste des contextes de ne peut être vide.", contexteSet.size() > 0);

	    System.err.println("# contextes: " + contexteSet.size());
	    int cpt = 1;
	    for (Contexte c : contexteSet.getContextes()) {
		System.out.println("------" + cpt + "------");

		Phrase phrase = phraseService.getPhraseComplète(c);
		assertNotNull("La phrase est NULL pour le contexte #" + (cpt++) + " =>" + c, phrase);
		System.out.println(phrase.phrase);
		Contexte contexte = phraseService.getContextePhraseComplète(c);
		System.out.println(contexte);

		assertFalse("Erreur qui n'est pas juste ;-)",
			contexte.texteAvant.contains("erreur") && contexte.texteAprès.contains("erreur") && contexte.mot.contains("erreur"));
	    }

	}

    }

    @Test
    public void phrasesIdentiquesDansContextesTest() {
	CorpusPhraseService phraseService = new CorpusPhraseService();
	Contexte contextePhraseComplète = phraseService.getContextePhraseComplète(new Contexte("Bizarre. Excepté papa et maman. Bizarre. Excepté ", "papa",
		" et maman. Quitte à le répéter une troisième fois.  Excepté papa et maman."));

	assertNotNull(contextePhraseComplète);

	contextePhraseComplète = phraseService.getContextePhraseComplète(new Contexte("Ça ne m'intéresse pas. Tais-toi et ", "mange",
		". Vexé, l'Affreux mange en silence. "));
	assertNotNull(contextePhraseComplète);
	assertEquals("Texte avant invalide", "Tais-toi et ", contextePhraseComplète.texteAvant);

	contextePhraseComplète = phraseService.getContextePhraseComplète(new Contexte("Ça ne m'intéresse pas. Tais-toi et mange. Vexé, l'Affreux ", "mange",
		" en silence. "));
	assertNotNull(contextePhraseComplète);
	assertEquals("Texte avant invalide", "Vexé, l'Affreux ", contextePhraseComplète.texteAvant);

	contextePhraseComplète = phraseService
		.getContextePhraseComplète(new Contexte(
			"le saumon d'herbes sauvages, puis elle fait une délicieuse galette.\nL'Affreux revient au coucher du soleil.\n—	Huumm... Du poisson ?\n—	Du saumon, répond la petite.\n—	Du saumon ? Du SAUMON ?G-É-N-I-A-L... La dernière fois que j'ai mangé du saumon, c'était...\n—	Ça ne m'intéresse pas. Tais-toi et ",
			"mange",
			".\nVexé, l'Affreux mange en silence. Il avale le saumon, engloutit la galette, croque les arêtes, lèche les assiettes... Après le repas, il rote avec fracas.\n—	Je ne te mangerai pas ce soir, finalement. Le feu s'est éteint. Je n'ai plus de sel. Et puis j'ai trop mangé. J'ai mal au"));
	assertNotNull(contextePhraseComplète);
	assertEquals("Texte avant invalide", "Tais-toi et ", contextePhraseComplète.texteAvant);
    }

    @Test
    @Ignore
    public void contextesCorpusErreur1Test() {

	Corpus corpus = new Corpus("Corpus de test nouveau", "");
	CorpusPhraseService phraseService = new CorpusPhraseService();

	corpus.setDossierData(System.getProperty("java.io.tmpdir") + File.separator + "index");

	CorpusService cs = new CorpusService(corpusRepo, corpus);

	ContexteSet contexteSet = cs.getContextesMot("E");

	cs.setTailleVoisinage(50);
	contexteSet = cs.getContextesMot("Raphaël");

	assertNotNull("La liste des contextes ne peut être null.", contexteSet.getContextes());
	assertTrue("La liste des contextes de ne peut être vide.", contexteSet.size() > 0);

	System.err.println("# contextes: " + contexteSet.size());
	int cpt = 1;
	for (Contexte c : contexteSet.getContextes()) {
	    System.out.println("------" + (cpt++) + "------");

	    Phrase phrase = phraseService.getPhraseComplète(c);
	    System.out.println(phrase.phrase);
	    Contexte contexte = phraseService.getContextePhraseComplète(c);
	    System.out.println(contexte);

	    System.out.println("Métadonnées du document dont est issu le contexte");
	    for (Metadata md : contexte.getDocMétadonnées()) {
		System.out.println(md.getNom() + ": " + md.getSimpleString());
	    }

	}

    }

    @Test
    public void contextesPhrasesAvecMarqueurPosTest() {
	CorpusPhraseService phraseService = new CorpusPhraseService();
	List<Phrase> phrasesComplètes = null;
	int[] marqueursPos = { 0, 40, 66, 114 };
	int cptMarqueur = 0;
	for (int marqueurPos : marqueursPos) {
	    phrasesComplètes = phraseService.getPhrasesComplètes(
		    "Papa a une Maman très gentille.  Et son Papa est chou également.  Papa a une Maman très gentille. Papa, c'est mon Papa!", marqueurPos);
	    int cptPhrase = 0;
	    for (Phrase phrase : phrasesComplètes) {
		System.out.println(cptMarqueur + ")" + phrase.phrase + (phrase.hasContexte ? " <===" : ""));
		if (phrase.hasContexte) {
		    assertTrue("Papa n'est pas trouvé à la bonne position", cptMarqueur == cptPhrase);
		}
		cptPhrase++;
	    }
	    cptMarqueur++;
	    assertNotNull(phrasesComplètes);
	}
    }

}
