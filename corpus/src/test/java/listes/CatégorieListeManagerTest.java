package listes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.entity.CatégorieListe;
import com.servicelibre.corpus.entity.Corpus;
import com.servicelibre.corpus.entity.Liste;
import com.servicelibre.corpus.entity.Mot;
import com.servicelibre.corpus.liste.ListeImport;
import com.servicelibre.corpus.manager.CatégorieListeManager;
import com.servicelibre.corpus.manager.CorpusManager;
import com.servicelibre.corpus.manager.ListeManager;


@ContextConfiguration("ListeManagerTest-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class CatégorieListeManagerTest implements ApplicationContextAware
{
    @Autowired
    ListeManager lm;

    @Autowired
    CorpusManager cm;
    
    @Autowired
    CatégorieListeManager clm;

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

    @Test
    @Transactional
    public void créationCatégorieListe()
    {
	Corpus corpus = new Corpus("Corpus de test", "description du corpus de test");
	cm.save(corpus);
	
	CatégorieListe catégorie = new CatégorieListe("Thèmes", "Listes thématiques", corpus);
	
	clm.save(catégorie);
	
	assertNotNull(catégorie.getId());
	
	System.out.println("Catégorie id = " + catégorie.getId());
	
	// Création de listes et association avec cette catégorie
	Liste lThématique1 = new Liste("à la maison", "Liste des mots du vocabulaire utilisé à la maison", corpus);
	Liste lThématique2 = new Liste("à l'école", "Liste des mots du vocabulaire utilisé à l'école", corpus);

	lm.save(lThématique1);
	lm.save(lThématique2);
	
	catégorie.ajouterListe(lThématique1);
	catégorie.ajouterListe(lThématique2);
	
	assertNotNull(catégorie.getListes());
	assertEquals(2,catégorie.getListes().size());
	
	// Récupération de la catégorie fraîchement créée dans la DB
	CatégorieListe catDB = clm.findByNom("Thèmes");
	assertNotNull(catDB.getListes());
	assertEquals(2,catDB.getListes().size());
	
	// Supprimer une liste de la catégorie
	catDB.enleverListe(lThématique1);
	assertEquals(1,catDB.getListes().size());
	
	System.out.println(catDB);
	
	// S'assurer que la suppression est effective dans la DB
	catDB = clm.findByNom("Thèmes");
	assertEquals(1,catDB.getListes().size());
    }

      
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.ctx = applicationContext;

    }

}
