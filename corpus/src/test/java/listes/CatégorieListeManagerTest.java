package listes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.liste.ListeImport;
import com.servicelibre.entities.corpus.CatégorieListe;
import com.servicelibre.entities.corpus.Corpus;
import com.servicelibre.entities.corpus.Liste;
import com.servicelibre.repositories.corpus.CatégorieListeRepository;
import com.servicelibre.repositories.corpus.CorpusRepository;
import com.servicelibre.repositories.corpus.ListeRepository;

@ContextConfiguration("ListeManagerTest-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class CatégorieListeManagerTest implements ApplicationContextAware {
	@Autowired
	ListeRepository listeRepo;

	@Autowired
	CorpusRepository corpusRepo;

	@Autowired
	CatégorieListeRepository catégorieListeRepo;

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
	public void créationCatégorieListe() {
		Corpus corpus = new Corpus("Corpus de test", "description du corpus de test");
		corpus = corpusRepo.save(corpus);

		CatégorieListe catégorie = new CatégorieListe("Thèmes", "Listes thématiques", corpus);

		catégorie = catégorieListeRepo.save(catégorie);

		assertNotNull(catégorie.getId());

		System.out.println("Catégorie id = " + catégorie.getId());

		// Création de listes et association avec cette catégorie
		Liste lThématique1 = new Liste("à la maison", "Liste des mots du vocabulaire utilisé à la maison", catégorie);
		lThématique1.setOrdre(1);
		Liste lThématique2 = new Liste("à l'école", "Liste des mots du vocabulaire utilisé à l'école", catégorie);
		lThématique2.setOrdre(2);

		lThématique1 = listeRepo.save(lThématique1);
		lThématique2 = listeRepo.save(lThématique2);

		catégorie.getListes().add(lThématique1);
		catégorie.getListes().add(lThématique2);

		// catégorie.ajouterListe(lThématique1);
		// catégorie.ajouterListe(lThématique2);

		assertNotNull(catégorie.getListes());
		assertEquals(2, catégorie.getListes().size());

		// Récupération de la catégorie fraîchement créée dans la DB
		CatégorieListe catDB = catégorieListeRepo.findByNom("Thèmes");
		assertNotNull(catDB.getListes());
		assertEquals(2, catDB.getListes().size());

		// Test syntaxe du tri
		catégorieListeRepo.findByCorpus(corpus, new Sort("ordre", "nom"));

		// Supprimer une liste de la catégorie
		catDB.getListes().remove(lThématique1);
		// catDB.enleverListe(lThématique1);
		assertEquals(1, catDB.getListes().size());

		System.out.println(catDB);

		// S'assurer que la suppression est effective dans la DB
		catDB = catégorieListeRepo.findByNom("Thèmes");
		assertEquals(1, catDB.getListes().size());
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;

	}

}
