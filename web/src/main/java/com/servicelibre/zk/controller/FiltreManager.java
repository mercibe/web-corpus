package com.servicelibre.zk.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;

import com.servicelibre.corpus.manager.Filtre;
import com.servicelibre.corpus.manager.FiltreMot;
import com.servicelibre.corpus.manager.ListeManager;
import com.servicelibre.corpus.service.CorpusService;

public abstract class FiltreManager
{

    protected List<Filtre> filtres = new ArrayList<Filtre>(3);

    protected CorpusService corpusService;

    protected ListeManager listeManager;

    private FiltreMot filtreActifModel;

    private Comparator<DefaultKeyValue> keyValueComparator;

    public FiltreManager()
    {
        super();

        keyValueComparator = new Comparator<DefaultKeyValue>()
        {

            @Override
            public int compare(DefaultKeyValue arg0, DefaultKeyValue arg1)
            {
                return arg0.getKey().toString().compareTo(arg1.getKey().toString());
            }
        };
    }

    public List<DefaultKeyValue> getFiltreNoms()
    {

        List<DefaultKeyValue> noms = new ArrayList<DefaultKeyValue>(filtres.size());
        for (Filtre filtre : filtres)
        {
            noms.add(new DefaultKeyValue(filtre.nom, filtre.description));
        }

        return noms;
    }

    public Set<DefaultKeyValue> getFiltreValeurs(String nom)
    {
        Set<DefaultKeyValue> values = new HashSet<DefaultKeyValue>();

        // recherche du filtre
        Filtre f = null;
        for (Filtre filtre : filtres)
        {
            if (filtre.nom.equalsIgnoreCase(nom))
            {
                f = filtre;
                break;
            }
        }

        if (f != null)
        {

            values = getValeursActives(nom, f.keyValues);

        }

        return values;
    }

    private Set<DefaultKeyValue> getValeursActives(String nom, Set<DefaultKeyValue> keyValues)
    {

        Set<DefaultKeyValue> valeursActives = new TreeSet<DefaultKeyValue>(keyValueComparator);

        if (filtreActifModel != null)
        {

            for (DefaultKeyValue keyValue : keyValues)
            {
                String valeur = keyValue.getKey().toString();
                if (!filtreActifModel.isActif(nom, valeur))
                {
                    valeursActives.add(keyValue);
                }
            }
        }
        else
        {
            return keyValues;
        }

        return valeursActives;
    }

    public CorpusService getCorpusService()
    {
        return corpusService;
    }

    public void setCorpusService(CorpusService corpusService)
    {
        this.corpusService = corpusService;
    }

    public ListeManager getListeManager()
    {
        return listeManager;
    }

    public void setListeManager(ListeManager listeManager)
    {
        this.listeManager = listeManager;
    }

    abstract public void init();

    public void setFiltreActif(FiltreMot filtreActifModel)
    {
        this.filtreActifModel = filtreActifModel;

    }
    
    public Comparator<DefaultKeyValue> getKeyValueComparator()
    {
        return keyValueComparator;
    }

}
