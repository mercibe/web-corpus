package mots;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;
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

import com.servicelibre.corpus.liste.ListeImport;
import com.servicelibre.corpus.manager.Filtre;
import com.servicelibre.corpus.manager.FiltreRecherche;
import com.servicelibre.corpus.manager.FiltreRecherche.CléFiltre;
import com.servicelibre.entities.corpus.Liste;
import com.servicelibre.entities.corpus.Mot;
import com.servicelibre.repositories.corpus.MotRepository;
import com.servicelibre.repositories.corpus.MotRepositoryCustom;
import com.servicelibre.repositories.corpus.MotRepositoryCustom.MotRésultat;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class MotManagerTest implements ApplicationContextAware {
	@Autowired
	MotRepository motRepository;

	@Autowired
	ListeImport listImport;

	private ApplicationContext ctx;

	/**
	 * Devrait être @Before, mais celui-ci fait un rollback...
	 * FIXME utiliser nouveau mécanime d'inportation et importer UNE fois (@Before) pour tous les tests.
	 */
	@Test
	@Transactional
	@Rollback(value = false)
	public void createListeDBNoRollabck() {
		listImport.setApplicationContext(ctx);

		for (Liste liste : listImport.getListes()) {
			listImport.execute(liste);
		}

	}

	// @Test
	// public void motListePrimaireTest() {
	// List<Mot> mots = motRepository.findByGraphie("acheminement", MotRepositoryCustom.Condition.ENTIER);
	//
	// Liste listePrimaire = motRepository.findListePrimaire(mots.get(0));
	//
	// assertTrue(listePrimaire.getId() == 1);
	// }

	@Test
	@Transactional
	@Rollback(value = false)
	public void motManagerSimpleTest() {

		Mot mot = motRepository.findOne((long) 1);

		assertNotNull(mot);
		System.out.println(mot);

		List<Mot> mots = motRepository.findByMot("manger");
		assertNotNull(mots);
		assertTrue(mots.size() > 0);
		System.out.println(mots);

	}

	@Test
	@Transactional
	public void motManagerGraphieTest() {
		MotRésultat mr = motRepository.findByGraphie("acheminement", MotRepositoryCustom.Condition.ENTIER, true);
		assertEquals("acheminement", mr.mots.get(0).lemme);
		System.out.println(mr.mots);

		// FIXME ajouter assertions
		mr = motRepository.findByGraphie("ache", MotRepositoryCustom.Condition.COMMENCE_PAR, true);
		assertTrue(mr.mots.size() > 0);
		System.out.println(mr.mots);

		mr = motRepository.findByGraphie("ment", MotRepositoryCustom.Condition.FINIT_PAR, true);
		assertTrue(mr.mots.size() > 0);
		System.out.println(mr.mots);

		mr = motRepository.findByGraphie("min", MotRepositoryCustom.Condition.CONTIENT, true);
		assertTrue(mr.mots.size() > 0);
		System.out.println(mr.mots);

	}

	@Test
	@Transactional
	@Rollback(value = false)
	public void motManagerPrononciationTest() {

		MotRésultat mr = motRepository.findByPrononciation("vɛʀ", MotRepositoryCustom.Condition.ENTIER, null, true);
		// assertEquals("pomme", mots.get(0).lemme);
		System.out.println(mr.mots);

		// FIXME ajouter assertions
		// mots = motRepository.findByGraphie("pom",
		// MotManager.Condition.COMMENCE_PAR);
		// System.out.println(mots);
		//
		// mots = motRepository.findByGraphie("ment",
		// MotManager.Condition.FINIT_PAR);
		// System.out.println(mots);
		//
		// mots = motRepository.findByGraphie("ari",
		// MotManager.Condition.CONTIENT);
		// System.out.println(mots);

	}

	@Test
	@Transactional
	@Rollback(value = false)
	public void motManagerFilterTest() {

		FiltreRecherche f = new FiltreRecherche();

		// Syntaxe verbeuse
		List<DefaultKeyValue> keyValues = new ArrayList<DefaultKeyValue>(1);
		keyValues.add(new DefaultKeyValue(1L, "Détail: corpus_id=1"));
		Filtre filtre = new Filtre(CléFiltre.liste.name(), "Liste de mots", keyValues, "", "");
		f.addFiltre(filtre);

		// Syntaxe allégée
		f.addFiltre(new Filtre(CléFiltre.catgram.name(), "Catégorie grammaticale", new String[] { "n.", "adv." }, "", ""));
		f.addFiltre(new Filtre(CléFiltre.genre.name(), "Genre", new String[] { "m." }, "", ""));
		f.addFiltre(new Filtre(CléFiltre.ro.name(), "Rectification ortographique", new Boolean[] { Boolean.TRUE }, "", ""));

		String graphie = "a";

		MotRésultat mr = motRepository.findByGraphie(graphie, MotRepositoryCustom.Condition.COMMENCE_PAR, f, true);
		System.out.println("Trouvé " + mr.mots.size() + " mots qui " + MotRepositoryCustom.Condition.COMMENCE_PAR + " « " + graphie
				+ " » et valident le filtre " + f);
		assertEquals(1, mr.mots.size());

		f.removeFiltre(CléFiltre.ro.name(), Boolean.TRUE);
		mr = motRepository.findByGraphie(graphie, MotRepositoryCustom.Condition.COMMENCE_PAR, f, true);
		System.out.println("Trouvé " + mr.mots.size() + " mots qui " + MotRepositoryCustom.Condition.COMMENCE_PAR + " « " + graphie
				+ " » et valident le filtre " + f);
		assertEquals(21, mr.mots.size());

		f.removeFiltre(CléFiltre.genre.name(), "m.");
		mr = motRepository.findByGraphie(graphie, MotRepositoryCustom.Condition.COMMENCE_PAR, f, true);
		System.out.println("Trouvé " + mr.mots.size() + " mots qui " + MotRepositoryCustom.Condition.COMMENCE_PAR + " « " + graphie
				+ " » et valident le filtre " + f);
		assertEquals(36, mr.mots.size());

		f.removeFiltre(CléFiltre.catgram.name(), "adv.");
		mr = motRepository.findByGraphie(graphie, MotRepositoryCustom.Condition.COMMENCE_PAR, f, true);
		System.out.println("Trouvé " + mr.mots.size() + " mots qui " + MotRepositoryCustom.Condition.COMMENCE_PAR + " « " + graphie
				+ " » et valident le filtre " + f);
		assertEquals(35, mr.mots.size());

		f.removeFiltre(CléFiltre.catgram.name(), "n.");
		mr = motRepository.findByGraphie(graphie, MotRepositoryCustom.Condition.COMMENCE_PAR, f, true);
		System.out.println("Trouvé " + mr.mots.size() + " mots qui " + MotRepositoryCustom.Condition.COMMENCE_PAR + " « " + graphie
				+ " » et valident le filtre " + f);
		assertEquals(46, mr.mots.size());

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;

	}

}
