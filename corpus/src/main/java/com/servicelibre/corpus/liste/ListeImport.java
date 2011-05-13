package com.servicelibre.corpus.liste;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.manager.ListeManager;


/**
 * Outil d'importation de listes
 * @author benoitm
 *
 */
public class ListeImport
{
    private static Logger log = LoggerFactory.getLogger(ListeImport.class);
    static ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("application-context.xml","system-context.xml");
    
	@Transactional
    public int execute() {
        
        log.info("Exécution de l'importation de la liste");
        
        ListeManager lm = (ListeManager)ctx.getBean("listeManager");
        
        Liste liste = lm.findOne((long) 1);
        System.err.println(liste);
        
        List<Mot> mots = liste.getMots();
		System.err.println(mots);
        
        mots.add(new Mot("boiraient", "boire", false, "v.", "une note", liste));
//        mots.add(new Mot("boirons", "boire", false, "v.", "une note", liste));
//        mots.add(new Mot("buvais", "boire", false, "v.", "une note", liste));
        
        
        return 0;
    }
    
    
    public static void main(String[] args)
    {
        
        ListeImport li = (ListeImport) ctx.getBean("listeImport");
        
        int exitCode = li.execute();
        
        System.exit(exitCode);
        
    }

}
