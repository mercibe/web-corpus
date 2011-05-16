package com.servicelibre.corpus.liste;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.manager.CorpusManager;
import com.servicelibre.corpus.manager.ListeManager;

/**
 * Outil d'importation de listes
 * 
 * @author benoitm
 * 
 */
public class ListeImport
{
    private static Logger logger = LoggerFactory.getLogger(ListeImport.class);

    private static ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("listeImport-config.xml",
            "system-context.xml");

    private Corpus corpus;
    private Liste liste;
    private File fichierMots;
    private LigneSplitter splitter;

    @Transactional
    public int execute()
    {

        logger.info("Exécution de l'importation de la liste");

        getOrCreateCorpus();

        if (corpus == null)
        {
            return -1;
        }

        getOrCreateListe();

        if (liste == null)
        {
            return -1;
        }

        // Chargement du fichier en liste
        if (fichierMots != null && fichierMots.exists())
        {

            try
            {
                List<String> lignes = FileUtils.readLines(fichierMots);

                List<Mot> mots = liste.getMots();

                for (String ligne : lignes)
                {
                    Mot mot = splitter.splitLigne(ligne, liste);
                    mots.add(mot);
                }
                
            }
            catch (IOException e)
            {
                logger.error("Erreur lors de la lecture de la liste des mots {}", fichierMots, e);
            }

        }
        else
        {
            logger.error("Fichier null ou inexistant: {}", fichierMots);
        }

        //        List<Mot> mots = liste.getMots();
        //		System.err.println(mots);
        //        
        //        mots.add(new Mot("boirions", "boire", false, "v.", "une autre note", liste));
        //        mots.add(new Mot("boirons", "boire", false, "v.", "une note", liste));
        //        mots.add(new Mot("buvais", "boire", false, "v.", "une note", liste));

        return 0;
    }

    private void getOrCreateListe()
    {
        ListeManager lm = (ListeManager) ctx.getBean("listeManager");

        if (liste == null)
        {
            logger.error("Pour importer une liste, il faut préciser cette liste!");
            return;
        }

        // Est-ce que la liste existe déjà?
        Liste dbListe = lm.findByNom(liste.getNom());

        if (dbListe == null)
        {
            liste.setCorpus(corpus);

            logger.info("Création de la liste {} dans la base de données.", liste);
            lm.save(liste);
        }
        else
        {
            liste = dbListe;
            logger.info("La liste {} a été trouvé dans la base de données.", liste);
        }

    }

    private void getOrCreateCorpus()
    {
        CorpusManager cm = (CorpusManager) ctx.getBean("corpusManager");

        if (corpus == null)
        {
            logger.error("Pour importer une liste, il faut préciser son corpus!");
            return;
        }

        // Est-ce que le corpus existe-déjà?
        Corpus dbCorpus = cm.findByNom(corpus.getNom());

        if (dbCorpus == null)
        {
            logger.info("Création du corpus {} dans la base de données.", corpus);
            cm.save(corpus);
        }
        else
        {
            corpus = dbCorpus;
            logger.info("Le corpus {} a été trouvé dans la base de données.", corpus);
        }

    }

    public Corpus getCorpus()
    {
        return corpus;
    }

    public void setCorpus(Corpus corpus)
    {
        this.corpus = corpus;
    }

    public static void main(String[] args)
    {

        ListeImport li = (ListeImport) ctx.getBean("listeImport");

        int exitCode = li.execute();

        System.exit(exitCode);

    }

    public Liste getListe()
    {
        return liste;
    }

    public void setListe(Liste liste)
    {
        this.liste = liste;
    }

    public File getFichierMots()
    {
        return fichierMots;
    }

    public void setFichierMots(File fichierMots)
    {
        this.fichierMots = fichierMots;
    }

    public LigneSplitter getSplitter()
    {
        return splitter;
    }

    public void setSplitter(LigneSplitter splitter)
    {
        this.splitter = splitter;
    }

}
