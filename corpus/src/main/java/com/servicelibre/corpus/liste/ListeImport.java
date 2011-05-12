package com.servicelibre.corpus.liste;

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
@Transactional
public class ListeImport
{
    private static Logger log = LoggerFactory.getLogger(ListeImport.class);
    
    public int execute() {
        
        log.info("Exécution de l'importation de la liste");
        
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("application-context.xml","system-context.xml");
        
        
        ListeManager lm = (ListeManager)ctx.getBean("listeManager");
        
        Liste liste = lm.getListe(2);
        System.err.println(liste);
        
        System.err.println(liste.getMots());
        
        
        return 0;
    }
    
    
    public static void main(String[] args)
    {
        ListeImport li = new ListeImport();
        
        int exitCode = li.execute();
        
        System.exit(exitCode);
        
    }

}
