package service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.servicelibre.corpus.analysis.MotInfo;
import com.servicelibre.corpus.service.FormeService;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class FormeServiceTest
{
    @Autowired
    FormeService formeService;

    @Test
    public void formeServiceTest()
    {

        List<MotInfo> lemmesMotInfo = formeService.getLemmesMotInfo("pommés");
        assertNotNull("lemmes ne peut être null", lemmesMotInfo);
        System.out.println(lemmesMotInfo);
        assertEquals("Le candidat pommé doit retourner 2 lemmes, pommer et pomme", 2, lemmesMotInfo.size());

        List<String> lemmes = formeService.getLemmes("pommés");
        assertNotNull("lemmes ne peut être null", lemmes);
        System.out.println(lemmes);
        assertEquals("Le candidat pommé doit retourner 2 lemmes, pommer et pomme", 2, lemmes.size());
        
        
        List<MotInfo> formesMotInfo = formeService.getFormesMotInfo("pomme");
        assertNotNull("formes ne peut être null", formesMotInfo);
        System.out.println(formesMotInfo);
        assertEquals("Le candidat pomme doit retourner 2 formes, pomme et pommes", 2, lemmesMotInfo.size());
        
        List<String> formes = formeService.getFormes("pomme");
        assertNotNull("formes ne peut être null", formes);
        System.out.println(formes);
        assertEquals("Le candidat pomme doit retourner 2 formes, pomme et pommes", 2, formes.size());
        
        System.out.println(formeService.getFormes("manger"));
        
        
    }

}
