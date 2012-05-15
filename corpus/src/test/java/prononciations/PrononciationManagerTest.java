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

import com.servicelibre.corpus.liste.ListeImport;
import com.servicelibre.entities.corpus.Liste;
import com.servicelibre.entities.corpus.Mot;
import com.servicelibre.entities.corpus.MotPrononciation;
import com.servicelibre.entities.corpus.Prononciation;
import com.servicelibre.repositories.corpus.MotPrononciationRepository;
import com.servicelibre.repositories.corpus.MotRepository;
import com.servicelibre.repositories.corpus.PrononciationRepository;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class PrononciationManagerTest implements ApplicationContextAware
{
    @Autowired
    PrononciationRepository prononciationRepo;

    @Autowired
    MotRepository motRepo;
    
    @Autowired
    MotPrononciationRepository motPrononciationRepo;

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

        List<Mot> mots = motRepo.findByMot("manger");
        mangerMot = mots.get(0);
        System.out.println(mangerMot);

        Prononciation prononc = new Prononciation("mɑ̃ʒe");
        mangerMot = motRepo.save(mangerMot);
        prononc = prononciationRepo.save(prononc);
        MotPrononciation motPrononciation = motPrononciationRepo.save(new MotPrononciation(mangerMot, prononc));
        mangerMot.getMotPrononciations().add(motPrononciation);
		

        System.out.println("mangerMot ++++++++++++++  " + mangerMot);

        List<Mot> mots2 = motRepo.findByMot("blancheur");
        Mot blancheurMot = mots2.get(0);
        Prononciation prononc2 = new Prononciation("blɑ̃ʃœʀ");

        blancheurMot = motRepo.save(blancheurMot);
        prononc2 = prononciationRepo.save(prononc2);
        motPrononciationRepo.save(new MotPrononciation(blancheurMot, prononc2));

        System.out.println("blancheurMot ++++++++++++++  " + blancheurMot);

        Prononciation prononc3 = prononciationRepo.findOne((long) 1);

        assertNotNull("La prononciation d'id = " + 1 + " ne peut-être null.", prononc3);
        System.out.println(prononc3);

        List<MotPrononciation> motPrononcs = motPrononciationRepo.findByMot(mangerMot);
        assertNotNull(motPrononcs);
        assertEquals("Le mot « manger » n'a qu'une seule prononciation", 1, motPrononcs.size());
        assertEquals("mɑ̃ʒe", motPrononcs.get(0).getPrononciation().prononciation);

        assertEquals("Le mot « manger » n'a qu'une seule prononciation", 1, mangerMot.getMotPrononciations().size());
        assertEquals("mɑ̃ʒe", mangerMot.getMotPrononciations().iterator().next().getPrononciation().prononciation);

        // suppression d'une prononciation
        assertTrue(mangerMot.getMotPrononciations().remove(motPrononcs.get(0)));
        assertEquals("Le mot « manger » n'a plus de prononciation", 0, mangerMot.getMotPrononciations().size());

        motPrononcs = motPrononciationRepo.findByMot(mangerMot);
        assertEquals("Le mot « manger » n'a plus de prononciation", 0, motPrononcs.size());
    }

    //    
    //    @Test
    //    @Ignore
    //    public void motManagerGraphieTest() {
    //        
    //        List<Mot> mots = motRepository.findByGraphie("pomme", MotManager.Condition.ENTIER);
    //        assertEquals("pomme", mots.get(0).lemme);
    //        System.out.println(mots);
    //        
    //        //FIXME ajouter assertions
    //        mots = motRepository.findByGraphie("pom", MotManager.Condition.COMMENCE_PAR);
    //        System.out.println(mots);
    //        
    //        mots = motRepository.findByGraphie("ment", MotManager.Condition.FINIT_PAR);
    //        System.out.println(mots);
    //        
    //        mots = motRepository.findByGraphie("ari", MotManager.Condition.CONTIENT);        
    //        System.out.println(mots);
    //        
    //    }
    //    
    //    @Test
    //    public void motManagerFilterTest() {
    //
    //    	FiltreRecherche f = new FiltreRecherche();
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
    //		List<Mot> mots = motRepository.findByGraphie(graphie, MotManager.Condition.COMMENCE_PAR, f);
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
