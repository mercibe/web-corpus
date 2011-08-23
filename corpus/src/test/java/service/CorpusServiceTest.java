package service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.servicelibre.corpus.entity.Corpus;
import com.servicelibre.corpus.manager.CorpusManager;
import com.servicelibre.corpus.service.Contexte;
import com.servicelibre.corpus.service.ContexteSet;
import com.servicelibre.corpus.service.CorpusService;
import com.servicelibre.corpus.service.FormeService;
import com.servicelibre.corpus.service.PhraseService;


@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class CorpusServiceTest
{

    @Autowired
    CorpusManager cm;
    
    @Autowired
    FormeService formeService;

    @Ignore
    @Test
    public void créeTest()
    {
        Corpus corpus = new Corpus("Corpus de test", "Un nouveau corpus de test");
        
        CorpusService cs = new CorpusService(cm, corpus);
        
        assertNotNull("Le corpus doit exister (création))", cs.getCorpus());
        
        assertTrue("Le corpus doit avoir un ID.", cs.getCorpus().getId() > 0);


    }
    
    @Ignore
    @Test
    public void ouvreTest()
    {
        Corpus corpus = new Corpus("Corpus de test", "");
        
        CorpusService cs = new CorpusService(cm, corpus);

        assertNotNull("Le corpus doit exister (ouverture))", cs.getCorpus());

        assertTrue("Le corpus doit avoir un ID généré.", cs.getCorpus().getId() > 0);

    }
    
    
    @Test
    public void contextesTest() {
       
        Corpus corpus = new Corpus("Corpus de test nouveau", "");
        
        corpus.setDossierData(System.getProperty("java.io.tmpdir") + File.separator + "index");
        
        CorpusService cs = new CorpusService(cm, corpus);
        
        String mot = "chien";
	ContexteSet contexteSet = cs.getContextesMot(mot);
        
        assertNotNull("La liste des contextes ne peut être null.", contexteSet.getContextes());
        assertTrue("La liste des contextes de ne peut être vide.", contexteSet.size() > 0);
        
        System.out.println("Trouvé " + contexteSet.size() + " occurrences du mot " + mot + " dans " + contexteSet.getDocumentCount() + " documents.");
        
//        System.err.println("# contextes: " + contextes.size());
//        int cpt = 1;
//        for(Contexte c : contextes) {
//            System.out.println("------"+ cpt++ +"------");
//            System.out.println(c);
//        }
        
    }
    
    @Test
    public void contextesLemmeTest() {
       
        Corpus corpus = new Corpus("Corpus de test nouveau", "");
        
        corpus.setDossierData(System.getProperty("java.io.tmpdir") + File.separator + "index");
        
        CorpusService cs = new CorpusService(cm, corpus);
        cs.setFormeService(formeService);
        
        String lemme = "manger";
	
        ContexteSet contexteSet = cs.getContextesLemme(lemme);
        
        assertNotNull("La liste des contextes ne peut être null.", contexteSet.getContextes());
        assertTrue("La liste des contextes de ne peut être vide.", contexteSet.size() > 0);
        
        System.out.println("Trouvé " + contexteSet.size() + " formes du lemme " + lemme + " dans " + contexteSet.getDocumentCount() + " documents.");
        
    }
    
    @Test
    public void contextesPhraseTest() {
       
        Corpus corpus = new Corpus("Corpus de test nouveau", "");
        
        corpus.setDossierData(System.getProperty("java.io.tmpdir") + File.separator + "index");
        
        CorpusService cs = new CorpusService(cm, corpus);
        
        int nbPhrases = 1;
		int nbMoyMotPhrase = 30;
		
		int tailleVoisinnage = nbPhrases  * nbMoyMotPhrase;
		
        cs.setTailleVoisinnage(tailleVoisinnage);
        
        ContexteSet contexteSet = cs.getContextesMot("chien");
        
        assertNotNull("La liste des contextes ne peut être null.", contexteSet.getContextes());
        assertTrue("La liste des contextes de ne peut être vide.", contexteSet.size() > 0);
        
        PhraseService phraseService = new PhraseService(); 
        
        System.err.println("# contextes: " + contexteSet.size());
        int cpt = 1;
        for(Contexte c : contexteSet.getContextes()) {
            System.out.println("------"+ cpt++ +"------");
            
           List<String> phrases = phraseService.getPhrasesComplètes(c);
            
            //System.out.println(c);
        }
        
    }

	private void getPhrases(Contexte c) {
		// TODO Auto-generated method stub
		
	}

}
