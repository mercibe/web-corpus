package listes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
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

import com.servicelibre.corpus.entity.CatégorieListe;
import com.servicelibre.corpus.entity.Corpus;
import com.servicelibre.corpus.entity.Liste;
import com.servicelibre.corpus.entity.ListeMot;
import com.servicelibre.corpus.entity.Mot;
import com.servicelibre.corpus.liste.ListeImport;
import com.servicelibre.corpus.repository.CorpusRepository;
import com.servicelibre.corpus.repository.ListeMotRepository;
import com.servicelibre.corpus.repository.ListeRepository;


@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ListeManagerTest implements ApplicationContextAware
{
	
	
    @Autowired
    ListeRepository listeRepo;
    
    @Autowired
    ListeMotRepository listeMotRepo;

    @Autowired
    CorpusRepository corpusRepo;
    
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
    public void createListeDBNoRollabck()
    {
        listeImport.setApplicationContext(ctx);

        for (Liste liste : listeImport.getListes())
        {
            listeImport.execute(liste);
        }
    }

    @Ignore
    @Transactional
    public void créationListes()
    {
        Corpus corpus = new Corpus("Corpus de test", "description du corpus de test");

        System.err.println("corpus_id: " + corpus.getId());
        
        corpus = corpusRepo.save(corpus);
        
        CatégorieListe catégorieListe = new CatégorieListe("Listes", "Listes de partitionnement des mots", corpus);
        
        Liste lTest1 = new Liste("Liste de test", "Liste de test TDD", catégorieListe);
        

        System.err.println("corpus_id: " + corpus.getId());

        assertEquals("Liste de test", lTest1.getNom());
        assertEquals("Liste de test TDD", lTest1.getDescription());

        // Création de la liste de lemmes
        List<Mot> mots = new ArrayList<Mot>();
        mots.add(new Mot("manger", "manger", true, "VERBE", "", null, null, false, null));
        mots.add(new Mot("pomme", "pomme", true, "NOM_COMMUN", "", null, null, false, null));

        // Ajout de la liste de lemmes à la définition de la liste
        
        for (Mot mot : mots) {
        	listeMotRepo.save(new ListeMot(mot, lTest1));
		}
        
        lTest1 = listeRepo.save(lTest1);
        
        List<ListeMot> listeMots = lTest1.getListeMots();
        assertNotNull(listeMots);
        assertEquals(mots.size(), listeMots.size());
        assertEquals(mots.size(), listeMots.size());

//        List<Mot> mots3 = lTest1.getMots();
//        assertNotNull(mots3);
//        for (int i = 0; i < lTest1.size(); i++)
//        {
//            assertEquals(mots.get(i), mots3.get(i));
//            System.out.println(mots.get(i));
//        }
        
        System.out.println("Catégorie == " + lTest1.getCatégorie());

    }

    @Test
    @Transactional
    public void testContenuDB()
    {
        Liste liste_test_1 = listeRepo.findByNom(listeTest1.getNom());
        assertEquals(50, liste_test_1.getListeMots().size());

        liste_test_1 = listeRepo.findOne(listeTest1.getId());
        assertEquals(50, liste_test_1.getListeMots().size());
    }

    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.ctx = applicationContext;

    }

}
