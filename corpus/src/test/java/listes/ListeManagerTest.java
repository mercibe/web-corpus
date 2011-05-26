package listes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.liste.Corpus;
import com.servicelibre.corpus.liste.Liste;
import com.servicelibre.corpus.liste.ListeImport;
import com.servicelibre.corpus.liste.Mot;
import com.servicelibre.corpus.manager.CorpusManager;
import com.servicelibre.corpus.manager.ListeManager;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ListeManagerTest implements ApplicationContextAware {
	@Autowired
	ListeManager lm;

	@Autowired
	CorpusManager cm;

	@Autowired
	ListeImport listeImport;

	@Autowired
	Liste listeTest1;

	private ApplicationContext ctx;

	/**
	 * Devrait être @Before, mais celui-ci fait un rollback...
	 */
	@Test
	@Transactional
	@Rollback(value = false)
	public void createListeDBNoRollabck() {
		listeImport.setApplicationContext(ctx);

		for (Liste liste : listeImport.getListes()) {
			listeImport.execute(liste);
		}
	}

	@Test
	@Transactional
	public void créationListes() {
		Corpus corpus = new Corpus("Corpus de test", "description du corpus de test");

		Liste lTest1 = new Liste("Liste de test", "Liste de test TDD", corpus);

		System.err.println("corpus_id: " + corpus.getId());

		cm.save(corpus);

		System.err.println("corpus_id: " + corpus.getId());

		assertEquals("Liste de test", lTest1.getNom());
		assertEquals("Liste de test TDD", lTest1.getDescription());

		// Création de la liste de lemmes
		List<Mot> mots = new ArrayList<Mot>();
		mots.add(new Mot("manger", "manger", true, "VERBE", "", lTest1));
		mots.add(new Mot("pomme", "pomme", true, "NOM_COMMUN", "", lTest1));

		// Ajout de la liste de lemmes à la définition de la liste
		lTest1.setMots(mots);

		lm.save(lTest1);

		List<Mot> mots2 = lTest1.getMots();
		assertNotNull(mots2);
		assertEquals(mots.size(), mots2.size());
		assertEquals(mots.size(), lTest1.size());

		List<Mot> mots3 = lTest1.getMots();
		assertNotNull(mots3);
		for (int i = 0; i < lTest1.size(); i++) {
			assertEquals(mots.get(i), mots3.get(i));
			System.out.println(mots.get(i));
		}

	}

	@Test
	@Transactional
	public void testContenuDB() {
		Liste liste_test_1 = lm.findByNom(listeTest1.getNom());
		assertEquals(2299, liste_test_1.getMots().size());
		
		liste_test_1 = lm.findOne(listeTest1.getId());
		assertEquals(2299, liste_test_1.getMots().size());
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;

	}

}
