package listes;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.servicelibre.corpus.liste.Lemme;
import com.servicelibre.corpus.liste.Liste;
import com.servicelibre.corpus.manager.InMemoryListeManager;
import com.servicelibre.corpus.manager.ListeManager;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ListeManagerTest {

	@Test
	public void créationListes(){
		
		ListeManager lm = new InMemoryListeManager();
		
		Liste listeTest1 = new Liste("test1", "Liste de test", "Liste de test TDD");
		
		assertEquals("test1", listeTest1.getId());
		assertEquals("Liste de test", listeTest1.getNom());
		assertEquals("Liste de test TDD", listeTest1.getDescription());
		

		// Création de la liste de lemmes
		List<Lemme> lemmes = new ArrayList<Lemme>();
		lemmes.add(new Lemme("manger", "VERBE"));
		lemmes.add(new Lemme("pomme", "NOM_COMMUN"));
		
		// Ajout de la liste de lemmes à la définition de la liste
		listeTest1.setLemmes(lemmes);
		
		List<Lemme> lemmes2 = listeTest1.getLemmes();
		assertNotNull(lemmes2);
		assertEquals(lemmes.size(), lemmes2.size());
		assertEquals(lemmes.size(), listeTest1.size());
		
		
		lm.addListe(listeTest1);
		List<Lemme> lemmes3 = lm.getListeLemmes(listeTest1.getId());
		assertNotNull(lemmes3);
		for (int i = 0; i < listeTest1.size(); i++) {
			assertEquals(lemmes.get(i), lemmes3.get(i));
			System.out.println(lemmes.get(i));
		}
		
		lm.setMaxLemmes(200);
		assertEquals(200,lm.getMaxLemmes());
		
		
		
		
	}
	
}
