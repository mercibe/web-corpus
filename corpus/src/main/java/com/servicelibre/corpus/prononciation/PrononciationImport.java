package com.servicelibre.corpus.prononciation;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.entity.Corpus;
import com.servicelibre.corpus.entity.Liste;
import com.servicelibre.corpus.entity.Mot;
import com.servicelibre.corpus.entity.Prononciation;
import com.servicelibre.corpus.manager.CorpusManager;
import com.servicelibre.corpus.manager.ListeManager;
import com.servicelibre.corpus.manager.MotManager;
import com.servicelibre.corpus.manager.PrononciationManager;


/**
 * Outil d'importation des prononcistions
 * 
 * format des lignes du fichier:
 * 
 * mot\tprononciation\tnomFichier
 * 
 * @author benoitm
 * 
 */
public class PrononciationImport
{
	
    private static Logger logger = LoggerFactory.getLogger(PrononciationImport.class);
    
    private static final String SÉPARATEUR = "\\t";

    private ApplicationContext ctx;

    @Transactional
    public int execute(File fichierSource)
    {

        logger.info("Exécution de l'importation des prononciations");

        // Chargement du fichier en liste
        if (fichierSource != null && fichierSource.exists())
        {

            MotManager mm = (MotManager) ctx.getBean("motManager");
            PrononciationManager pm = (PrononciationManager) ctx.getBean("prononciationManager");

            try
            {
                List<String> lignes = FileUtils.readLines(fichierSource);

                int prononciationCpt = 0;
                int liaisonCpt = 0;
                for (String ligne : lignes)
                {
                    String[] cols = ligne.split(SÉPARATEUR);

                    Prononciation prononc = new Prononciation(cols[1]);
                    
                    logger.debug("importation de la prononciation {}", prononc.prononciation);
                    
                    pm.save(prononc);
                    
                    
                    // Rechercher le/les éventuels mot/forme associés et lier
                    String motForme = cols[0];
                    List<Mot> mots = mm.findByMot(motForme);
                    for (Mot mot : mots)
                    {
                        logger.debug("liaison de la prononciation {} à la forme {}", prononc.prononciation, mot);
                        mot.ajoutePrononciation(prononc);
                        liaisonCpt++;
                    }
                        
                    prononciationCpt++;
                }

                logger.info("{} prononciations ont été ajoutées à la table des prononciations. {} liaisons avec des mots.", prononciationCpt, liaisonCpt);

            }
            catch (IOException e)
            {
                logger.error("Erreur lors de la lecture de la liste des mots {}", fichierSource, e);
            }

        }
        else
        {
            logger.error("Fichier null ou inexistant: {}", fichierSource);
        }

        return 0;
    }

    private Liste getOrCreateListe(Liste currentListe)
    {

        ListeManager lm = (ListeManager) ctx.getBean("listeManager");

        if (currentListe == null)
        {
            logger.error("Pour importer une liste, il faut préciser cette liste!");
            return null;
        }

        // Est-ce que la liste existe déjà?
        Liste dbListe = lm.findByNom(currentListe.getNom());

        if (dbListe == null)
        {
            logger.info("Création de la liste {} dans la base de données.", currentListe);
            lm.save(currentListe);
            return currentListe;
        }
        else
        {

            // récupération des champs transient éventuels
            dbListe.setFichierSource(currentListe.getFichierSource());
            dbListe.setLigneSplitter(currentListe.getLigneSplitter());
            logger.info("La liste {} a été trouvé dans la base de données.", currentListe);
            return dbListe;
        }

    }

    private void getOrCreateCorpus(Liste currentListe)
    {
        CorpusManager cm = (CorpusManager) ctx.getBean("corpusManager");

        if (currentListe.getCorpus() == null)
        {
            logger.error("Pour importer la phonétique, il faut préciser son corpus (pour l'instant!)");
            return;
        }

        // Est-ce que le corpus existe-déjà?
        Corpus dbCorpus = cm.findByNom(currentListe.getCorpus().getNom());

        if (dbCorpus == null)
        {
            cm.save(currentListe.getCorpus());
            logger.info("Création du corpus {} dans la base de données.", currentListe.getCorpus());
        }
        else
        {
            currentListe.setCorpus(dbCorpus);
            logger.info("Le corpus {} a été trouvé dans la base de données.", currentListe.getCorpus());
        }

    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.ctx = applicationContext;

    }

}
