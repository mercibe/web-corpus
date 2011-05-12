package listes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.servicelibre.corpus.liste.Liste;
import com.servicelibre.corpus.liste.Mot;
import com.servicelibre.corpus.manager.InMemoryListeManager;
import com.servicelibre.corpus.manager.ListeManager;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ListeManagerTest
{
 
    @Test
    public void créationListes()
    {

        ListeManager lm = new InMemoryListeManager();

        Liste listeTest1 = new Liste(1, "Liste de test", "Liste de test TDD");

        assertEquals(1, listeTest1.getId());
        assertEquals("Liste de test", listeTest1.getNom());
        assertEquals("Liste de test TDD", listeTest1.getDescription());

        // Création de la liste de lemmes
        List<Mot> mots = new ArrayList<Mot>();
        mots.add(new Mot("manger", "manger", true, "VERBE", ""));
        mots.add(new Mot("pomme", "pomme", true, "NOM_COMMUN", ""));

        // Ajout de la liste de lemmes à la définition de la liste
        listeTest1.setMots(mots);

        List<Mot> mots2 = listeTest1.getMots();
        assertNotNull(mots2);
        assertEquals(mots.size(), mots2.size());
        assertEquals(mots.size(), listeTest1.size());

        lm.save(listeTest1);
        List<Mot> mots3 = lm.getMots(listeTest1.getId());
        assertNotNull(mots3);
        for (int i = 0; i < listeTest1.size(); i++)
        {
            assertEquals(mots.get(i), mots3.get(i));
            System.out.println(mots.get(i));
        }

        lm.setMaxMots(200);
        assertEquals(200, lm.getMaxMots());

    }

}
