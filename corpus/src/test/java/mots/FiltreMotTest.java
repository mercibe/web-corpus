package mots;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.servicelibre.corpus.manager.Filtre;
import com.servicelibre.corpus.manager.FiltreRecherche;

@ContextConfiguration("MotManagerTest-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class FiltreMotTest {

	
	@Test
	public void FiltreTest() {
		FiltreRecherche fm = new FiltreRecherche();
		
		List<DefaultKeyValue> kv = new ArrayList<DefaultKeyValue>();
		kv.add(new DefaultKeyValue("clé1", "valeur1"));
		kv.add(new DefaultKeyValue("clé2", "valeur2"));
		
		Filtre filtre1 = new Filtre("Filtre1", "La description du Filtre1", kv);
		
		System.err.println(filtre1);
		
		fm.addFiltre(filtre1);
		
		System.err.println(fm);
		
		System.err.println("Affichage des groupes du FiltreRecherche");
		for(DefaultKeyValue dkv :fm.getFiltreGroupes())
		{
			System.err.println("Groupe: " + dkv);
		}
		
		System.err.println("Affichage des valeurs du FiltreRecherche");
		for(Object[] o :fm.getFiltreValeurs())
		{
			System.err.print("Valeur: ");
			for(int i = 0 ; i< o.length; i++)
			{
				System.err.print(o[i] + " ");
			}
			System.err.println();
		}
		
		
	}
}
