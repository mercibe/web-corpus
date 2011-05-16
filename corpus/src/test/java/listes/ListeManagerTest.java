package listes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.liste.Corpus;
import com.servicelibre.corpus.liste.Liste;
import com.servicelibre.corpus.liste.Mot;
import com.servicelibre.corpus.manager.CorpusManager;
import com.servicelibre.corpus.manager.ListeManager;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ListeManagerTest
{
    @Autowired
    ListeManager lm;

    @Autowired
    CorpusManager cm;

    @Test
    @Transactional
    public void créationListes()
    {

        Corpus corpus = new Corpus("Corpus de test", "description du corpus de test");

        Liste listeTest1 = new Liste("Liste de test", "Liste de test TDD", corpus);

        System.err.println("corpus_id: " + corpus.getId());

        cm.save(corpus);

        System.err.println("corpus_id: " + corpus.getId());

        assertEquals("Liste de test", listeTest1.getNom());
        assertEquals("Liste de test TDD", listeTest1.getDescription());

        // Création de la liste de lemmes
        List<Mot> mots = new ArrayList<Mot>();
        mots.add(new Mot("manger", "manger", true, "VERBE", "", listeTest1));
        mots.add(new Mot("pomme", "pomme", true, "NOM_COMMUN", "", listeTest1));

        // Ajout de la liste de lemmes à la définition de la liste
        listeTest1.setMots(mots);

        lm.save(listeTest1);
        
        System.err.println(listeTest1.getId());
        
        List<Mot> mots2 = listeTest1.getMots();
        assertNotNull(mots2);
        assertEquals(mots.size(), mots2.size());
        assertEquals(mots.size(), listeTest1.size());

        List<Mot> mots3 = listeTest1.getMots();
        assertNotNull(mots3);
        for (int i = 0; i < listeTest1.size(); i++)
        {
            assertEquals(mots.get(i), mots3.get(i));
            System.out.println(mots.get(i));
        }

    }

}
