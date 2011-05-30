package com.servicelibre.zk.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.springframework.beans.factory.annotation.Autowired;

import com.servicelibre.corpus.liste.Liste;
import com.servicelibre.corpus.manager.ListeManager;
import com.servicelibre.corpus.manager.MotManager;

public class ListeMotFiltre
{

    @Autowired
    ListeManager listeManager;

    @Autowired
    MotManager motManager;

    Map<String, List<DefaultKeyValue>> filtres = new HashMap<String, List<DefaultKeyValue>>();

    private long corpusId;

    public ListeMotFiltre()
    {
        super();
    }

    public ListeMotFiltre(long corpusId)
    {
        super();
        this.corpusId = corpusId;
    }

    Set<String> getFiltreNoms()
    {
        return filtres.keySet();
    }

    List<DefaultKeyValue> getFiltreValeurs(String nom)
    {
        return filtres.get(nom);
    }

    public void init()
    {

        // ajout du filtre des listes
        List<Liste> listes = listeManager.findByCorpusId(corpusId);

        List<DefaultKeyValue> listesClésValeurs = new ArrayList<DefaultKeyValue>(listes.size());
        for (Liste liste : listes)
        {
            listesClésValeurs.add(new DefaultKeyValue(liste.getId(), liste.getNom()));
        }
        filtres.put("Liste", listesClésValeurs);

        // Ajout de la liste des catgram
        List<DefaultKeyValue>catgramClésValeurs = new ArrayList<DefaultKeyValue>(2);
        catgramClésValeurs.add(new DefaultKeyValue("adj.", "adj."));
        catgramClésValeurs.add(new DefaultKeyValue("n.", "n."));
        catgramClésValeurs.add(new DefaultKeyValue("v.", "v."));
        catgramClésValeurs.add(new DefaultKeyValue("adv.", "adv."));
        filtres.put("Cat. gram.", catgramClésValeurs);

        // Ajout de la liste des genres
        List<DefaultKeyValue> genreClésValeurs = new ArrayList<DefaultKeyValue>(2);
        genreClésValeurs.add(new DefaultKeyValue("m.", "m."));
        genreClésValeurs.add(new DefaultKeyValue("f.", "f."));
        filtres.put("Genre", genreClésValeurs);

        // Ajout de la liste des nombres
        List<DefaultKeyValue> nombreClésValeurs = new ArrayList<DefaultKeyValue>(3);
        nombreClésValeurs.add(new DefaultKeyValue("s.", ""));
        nombreClésValeurs.add(new DefaultKeyValue("inv.", "inv."));
        nombreClésValeurs.add(new DefaultKeyValue("pl.", "pl."));
        filtres.put("Nombre", nombreClésValeurs);

    }

    public long getCorpusId()
    {
        return corpusId;
    }

    public void setCorpusId(long corpusId)
    {
        this.corpusId = corpusId;
    }

}
