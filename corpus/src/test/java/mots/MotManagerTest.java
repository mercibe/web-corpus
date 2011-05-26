package mots;

import static org.junit.Assert.*;

import java.util.Collections;
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

import com.servicelibre.corpus.liste.Liste;
import com.servicelibre.corpus.liste.ListeImport;
import com.servicelibre.corpus.liste.Mot;
import com.servicelibre.corpus.manager.MotManager;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class MotManagerTest implements ApplicationContextAware 
{
    @Autowired
    MotManager motManager;
    
    @Autowired
    ListeImport listImport;

    private ApplicationContext ctx;
    
    /**
     * Devrait être @Before, mais celui-ci fait un rollback...
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
    
    @Test
    public void motManagerSimpleTest() {
        
        Mot mot = motManager.findOne((long)1);
        
        assertNotNull(mot);
        System.out.println(mot);
        
        mot = motManager.findByMot("manger");
        assertNotNull(mot);
        System.out.println(mot);
    }
    
    @Test
    public void motManagerGraphieTest() {
        
        List<Mot> mots = motManager.findByGraphie("pomme", MotManager.Condition.MOT_ENTIER);
        assertEquals("pomme", mots.get(0).lemme);
        System.out.println(mots);
        
        //FIXME ajouter assertions
        mots = motManager.findByGraphie("pom", MotManager.Condition.MOT_COMMENCE_PAR);
        System.out.println(mots);
        
        mots = motManager.findByGraphie("ment", MotManager.Condition.MOT_FINIT_PAR);
        System.out.println(mots);
        
        mots = motManager.findByGraphie("ari", MotManager.Condition.MOT_CONTIENT);        
        System.out.println(mots);

        
        
    }
    
    

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.ctx = applicationContext;
        
    }

}
