package com.servicelibre.corpus.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.servicelibre.corpus.analyzis.CatgramsPivot;
import com.servicelibre.corpus.analyzis.LemmeNaturalComparator;
import com.servicelibre.corpus.analyzis.MotInfo;
import com.servicelibre.corpus.analyzis.MotNaturalComparator;
import com.servicelibre.corpus.liste.LigneSplitter;

public class FormeService
{

    private static final int NB_FORMES = 365000;

    private final static Logger logger = LoggerFactory.getLogger(FormeService.class);

    private static final String FORMES_NOMFICHIER = "formes.txt";

    String formesFilePath;
    LigneSplitter ligneSplitter;

    private InputStream formesStream;

    private CatgramsPivot catgramsPivot = new CatgramsPivot();

    private List<List<MotInfo>> motsInfos = new ArrayList<List<MotInfo>>(NB_FORMES);
    private List<String> mots = new ArrayList<String>(NB_FORMES);

    private List<List<MotInfo>> lemmesInfos = new ArrayList<List<MotInfo>>(NB_FORMES);
    private List<String> lemmes = new ArrayList<String>(NB_FORMES);

    private MotNaturalComparator<MotInfo> motComp = new MotNaturalComparator<MotInfo>();
    private LemmeNaturalComparator<MotInfo> lemmeComp = new LemmeNaturalComparator<MotInfo>();

    public FormeService()
    {
        super();
    }

    public FormeService(String formesFilePath)
    {
        super();
        this.formesFilePath = formesFilePath;
    }

    public List<MotInfo> getLemmesMotInfo(String mot)
    {
        List<MotInfo> info = new ArrayList<MotInfo>();

        // Recherche du mot dans les formes triées
        int idx = Collections.binarySearch(mots, mot);

        // renvoyer toutes les occurences (candidats possibles)
        if (idx >= 0)
        {
            for (MotInfo motInfo : motsInfos.get(idx))
            {
                info.add(new MotInfo(motInfo.getMot(), motInfo.getLemme(), motInfo.getCatgram(), motInfo.getNote(),
                        motInfo.getFreqMot(), motInfo.getFreqLemme(), motInfo.isLemme()));
            }
        }

        return info;

    }

    public List<String> getLemmes(String mot)
    {
        Set<String> info = new HashSet<String>();

        // Recherche du mot dans les formes triées
        int idx = Collections.binarySearch(mots, mot);

        // renvoyer toutes les occurences (candidats possibles)
        if (idx >= 0)
        {
            for (MotInfo motInfo : motsInfos.get(idx))
            {
                info.add(motInfo.getLemme());
            }
        }

        return new ArrayList<String>(info);

    }

    public List<MotInfo> getFormesMotInfo(String lemme)
    {
        List<MotInfo> info = new ArrayList<MotInfo>();

        // Recherche du mot dans les formes triées
        int idx = Collections.binarySearch(lemmes, lemme);

        // renvoyer toutes les occurences (candidats possibles)
        if (idx >= 0)
        {
            for (MotInfo motInfo : lemmesInfos.get(idx))
            {
                info.add(new MotInfo(motInfo.getMot(), motInfo.getLemme(), motInfo.getCatgram(), motInfo.getNote(),
                        motInfo.getFreqMot(), motInfo.getFreqLemme(), motInfo.isLemme()));
            }
        }

        return info;

    }

    public List<String> getFormes(String lemme)
    {
        Set<String> info = new HashSet<String>();

        // Recherche du mot dans les formes triées
        int idx = Collections.binarySearch(lemmes, lemme);

        // renvoyer toutes les occurences (candidats possibles)
        if (idx >= 0)
        {
            for (MotInfo motInfo : lemmesInfos.get(idx))
            {
                info.add(motInfo.getMot());
            }
        }

        return new ArrayList<String>(info);

    }

    public boolean isLemme(String lemme)
    {
        return false;
    }

    public void init()
    {
        // Chargement du fichier des formes
        chargementFormes();

    }

    private void chargementFormes()
    {
        // Format : mot|lemme|catgram
        List<MotInfo> formesTemp = new ArrayList<MotInfo>(NB_FORMES);
        try
        {

            // Si chemin spécifique vers fichier des formes donnés, utiliser ce fichier
            if (formesFilePath != null && !formesFilePath.isEmpty())
            {
                File formesFile = new File(formesFilePath);
                if (formesFile.exists())
                {
                    formesStream = new FileInputStream(formesFile);
                }
                else
                {
                    logger.error("Le fichier des formes {} n'existe pas", formesFilePath);
                    return;
                }
            }
            else
            {
                // Recherche de la ressource par défaut dans le classpath
                formesStream = this.getClass().getClassLoader().getResourceAsStream(FORMES_NOMFICHIER);
            }

            // Chargement du fichier des formes dans une table temporaire
            BufferedReader in = new BufferedReader(new InputStreamReader(formesStream, "UTF-8"));
            String line = null;
            while ((line = in.readLine()) != null)
            {

                // FIXME utiliser le ligneSplitter !
                //ligneSplitter.splitLigne(line).get(0);
                String[] formeInfo = line.split("\\|");
                formesTemp.add(new MotInfo(formeInfo[0], formeInfo[1], catgramsPivot.getCatgramFromId(formeInfo[2]),
                        formeInfo[3]));
            }
            in.close();
            formesStream.close();
        }
        catch (IOException e)
        {
            logger.error("Erreur lors du chargement des formes depuis le fichier {} (classpath ou fichier).",
                    FORMES_NOMFICHIER + " ou " + formesFilePath, e);
            return;
        }

        int formesSize = formesTemp.size();

        // Tri des formes sur base du mot
        logger.info("Tri des {} formes...", formesSize);
        Collections.sort(formesTemp, motComp);
        logger.info("Tri des {} formes terminé.", formesSize);

        // Remplissage des 2 tables nécessaires au traitement des formes 
        int pos = 0;
        String nouveauMot = formesTemp.get(pos).mot;
        while (pos < formesSize)
        {
            List<MotInfo> lemmesPossibles = new ArrayList<MotInfo>(1);
            // Tant que le mot est identique et qu'on est pas à la fin du tableau,
            // construction de la liste des formes/lemmes possibles
            while (pos < formesSize && nouveauMot.equals(formesTemp.get(pos).mot))
            {
                lemmesPossibles.add(formesTemp.get(pos));
                pos++;
            }

            mots.add(nouveauMot);
            motsInfos.add(lemmesPossibles);

            if (pos < formesSize)
            {
                nouveauMot = formesTemp.get(pos).mot;
            }

        }

        // Tri des formes sur base du lemme
        logger.info("Tri des {} formes sur base des lemmes...", formesSize);
        Collections.sort(formesTemp, lemmeComp);
        logger.info("Tri des {} formes sur base des lemmes terminé.", formesSize);

        // Remplissage des 2 tables nécessaires au traitement des lemems 
        pos = 0;
        String nouveauLemme = formesTemp.get(pos).lemme;
        while (pos < formesSize)
        {
            List<MotInfo> formesPossibles = new ArrayList<MotInfo>(1);
            // Tant que le mot est identique et qu'on est pas à la fin du tableau,
            // construction de la liste des formes/lemmes possibles
            while (pos < formesSize && nouveauLemme.equals(formesTemp.get(pos).lemme))
            {
                formesPossibles.add(formesTemp.get(pos));
                pos++;
            }

            lemmes.add(nouveauLemme);
            lemmesInfos.add(formesPossibles);

            if (pos < formesSize)
            {
                nouveauLemme = formesTemp.get(pos).lemme;
            }

        }

        sauvegardeListe(mots, "/tmp/formes-tri.txt");

    }

    public void sauvegardeListe(List<String> motInfoList, String dumpFilename)
    {
        File dumpFile = new File(dumpFilename);
        try
        {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dumpFile), "UTF-8"));
            for (String string : motInfoList)
            {
                writer.append(string);
                writer.newLine();
            }
            writer.close();

        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public String getFormesFilePath()
    {
        return formesFilePath;
    }

    public void setFormesFilePath(String formesFilePath)
    {
        this.formesFilePath = formesFilePath;
    }

}
