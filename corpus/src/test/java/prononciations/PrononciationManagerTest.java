package prononciations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.entity.Liste;
import com.servicelibre.corpus.entity.Mot;
import com.servicelibre.corpus.entity.Prononciation;
import com.servicelibre.corpus.liste.ListeImport;
import com.servicelibre.corpus.manager.MotManager;
import com.servicelibre.corpus.manager.PrononciationManager;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class PrononciationManagerTest implements ApplicationContextAware
{
    @Autowired
    PrononciationManager prononcManager;

    @Autowired
    MotManager motManager;

    @Autowired
    ListeImport listImport;

    //    @Autowired
    //    PrononciationImport prononciationImport;

    private ApplicationContext ctx;

    private Mot mangerMot;

    /**
     * Devrait être @Before, mais celui-ci fait un rollback...
     */
    @Test
    @Transactional
    @Rollback(false)
    public void createPrononciationDBNoRollabck()
    {
        listImport.setApplicationContext(ctx);

        for (Liste liste : listImport.getListes())
        {
            listImport.execute(liste);
        }
    }

    @Test
    @Transactional
    public void prononciationManagerSimpleTest()
    {

        List<Mot> mots = motManager.findByMot("manger");
        mangerMot = mots.get(0);
        System.out.println(mangerMot);

        Prononciation prononc = new Prononciation("mɑ̃ʒe");
        mangerMot.addPrononciation(prononc);
        motManager.save(mangerMot);
        System.out.println("mangerMot ++++++++++++++  " + mangerMot);

        List<Mot> mots2 = motManager.findByMot("blancheur");
        Mot blancheurMot = mots2.get(0);
        Prononciation prononc2 = new Prononciation("blɑ̃ʃœʀ");
        blancheurMot.addPrononciation(prononc2);
        motManager.save(blancheurMot);
        System.out.println("blancheurMot ++++++++++++++  " + blancheurMot);

        Prononciation prononc3 = prononcManager.findOne((long) 1);

        assertNotNull("La prononciation d'id = " + 1 + " ne peut-être null.", prononc3);
        System.out.println(prononc3);

        List<Prononciation> prononcs = prononcManager.findByMot(mangerMot);
        assertNotNull(prononcs);
        assertEquals("Le mot « manger » n'a qu'une seule prononciation", 1, prononcs.size());
        assertEquals("mɑ̃ʒe", prononcs.get(0).prononciation);

        assertEquals("Le mot « manger » n'a qu'une seule prononciation", 1, mangerMot.getPrononciations().size());
        assertEquals("mɑ̃ʒe", mangerMot.getPrononciations().iterator().next().prononciation);

        // suppression d'une prononciation
        assertTrue(mangerMot.getPrononciations().remove(prononcs.get(0)));
        assertEquals("Le mot « manger » n'a plus de prononciation", 0, mangerMot.getPrononciations().size());

        prononcs = prononcManager.findByMot(mangerMot);
        assertEquals("Le mot « manger » n'a plus de prononciation", 0, prononcs.size());
    }

    //    
    //    @Test
    //    @Ignore
    //    public void motManagerGraphieTest() {
    //        
    //        List<Mot> mots = motManager.findByGraphie("pomme", MotManager.Condition.ENTIER);
    //        assertEquals("pomme", mots.get(0).lemme);
    //        System.out.println(mots);
    //        
    //        //FIXME ajouter assertions
    //        mots = motManager.findByGraphie("pom", MotManager.Condition.COMMENCE_PAR);
    //        System.out.println(mots);
    //        
    //        mots = motManager.findByGraphie("ment", MotManager.Condition.FINIT_PAR);
    //        System.out.println(mots);
    //        
    //        mots = motManager.findByGraphie("ari", MotManager.Condition.CONTIENT);        
    //        System.out.println(mots);
    //        
    //    }
    //    
    //    @Test
    //    public void motManagerFilterTest() {
    //
    //    	FiltreMot f = new FiltreMot();
    //
    //    	// Syntaxe verbeuse
    //    	List<DefaultKeyValue> keyValues = new ArrayList<DefaultKeyValue>(1);
    //    	keyValues.add(new DefaultKeyValue(1L, "Détail: corpus_id=1"));
    //    	Filtre filtre = new Filtre(CléFiltre.liste.name(), "Liste de mots", keyValues);
    //    	f.addFiltre(filtre);
    //    	
    //  
    //    	// Syntaxe allégée
    //    	f.addFiltre(new Filtre(CléFiltre.catgram.name(), "Catégorie grammaticale", new String[]{"n.", "adv."}));
    //  
    //    	f.addFiltre(new Filtre(CléFiltre.genre.name(), "Genre", new String[]{"f."}));
    //    	
    //    	String graphie = "a";
    //    	
    //		List<Mot> mots = motManager.findByGraphie(graphie, MotManager.Condition.COMMENCE_PAR, f);
    //    	System.out.println("Trouvé " + mots.size() + " mots qui " + MotManager.Condition.COMMENCE_PAR + " « " + graphie + " » et valident le filtre " + f);
    //    	assertEquals(152, mots.size());
    //    	
    //    }
    //    

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.ctx = applicationContext;

    }

}
