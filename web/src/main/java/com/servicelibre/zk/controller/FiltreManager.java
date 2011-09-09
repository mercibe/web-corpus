package com.servicelibre.zk.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;

import com.servicelibre.corpus.manager.Filtre;
import com.servicelibre.corpus.manager.FiltreMot;
import com.servicelibre.corpus.manager.ListeManager;
import com.servicelibre.corpus.service.CorpusService;

public abstract class FiltreManager
{

	public static final String VIDE = "     ";
	
	// Définition valeur vide
	protected DefaultKeyValue keyValueVide = new DefaultKeyValue("-1", VIDE);
	
    protected List<Filtre> filtres = new ArrayList<Filtre>(3);

    protected CorpusService corpusService;

    protected ListeManager listeManager;

    private FiltreMot filtreActifModel;
    

    public FiltreManager()
    {
        super();
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

    public List<DefaultKeyValue> getFiltreValeurs(String nom)
    {
        List<DefaultKeyValue> values = new ArrayList<DefaultKeyValue>();

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

    private List<DefaultKeyValue> getValeursActives(String nom, List<DefaultKeyValue> keyValues)
    {

        List<DefaultKeyValue> valeursActives = new ArrayList<DefaultKeyValue>(keyValues.size());

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

}
