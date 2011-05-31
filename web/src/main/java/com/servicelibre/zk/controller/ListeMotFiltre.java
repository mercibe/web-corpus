package com.servicelibre.zk.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.springframework.beans.factory.annotation.Autowired;

import com.servicelibre.corpus.manager.ListeManager;
import com.servicelibre.corpus.manager.MotManager;

public class ListeMotFiltre
{

    @Autowired
    ListeManager listeManager;

    @Autowired
    MotManager motManager;

    List<Filtre> filtres = new ArrayList<Filtre>();
   

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

    List<DefaultKeyValue> getFiltreNoms()
    {
     
        List<DefaultKeyValue> noms = new ArrayList<DefaultKeyValue>(filtres.size());
        for (Filtre filtre : filtres)
        {
            noms.add(new DefaultKeyValue(filtre.nom, filtre.description));
        }
        return noms;
    }

    List<DefaultKeyValue> getFiltreValeurs(String nom)
    {
        // recherche du filtre
        List<DefaultKeyValue> values = new ArrayList<DefaultKeyValue>();
            
        Filtre f = null;
        for (Filtre filtre : filtres)
        {
            if(filtre.nom.equalsIgnoreCase(nom)) {
                f= filtre;
                break;
            }
        }
        
        if(f != null) {
            values = f.keyValues;
        }
        
        return values;
    }

    public void init()
    {

        // ajout du filtre des listes
//        List<Liste> listes = listeManager.findByCorpusId(corpusId);

//        List<DefaultKeyValue> listesClésValeurs = new ArrayList<DefaultKeyValue>(listes.size());
//        for (Liste liste : listes)
//        {
//            listesClésValeurs.add(new DefaultKeyValue(liste.getId(), liste.getNom()));
//        }
//        filtres.put("Liste", listesClésValeurs);

        // Ajout de la liste des catgram
        List<DefaultKeyValue>catgramClésValeurs = new ArrayList<DefaultKeyValue>(2);
        catgramClésValeurs.add(new DefaultKeyValue("adj.", "adj."));
        catgramClésValeurs.add(new DefaultKeyValue("n.", "n."));
        catgramClésValeurs.add(new DefaultKeyValue("v.", "v."));
        catgramClésValeurs.add(new DefaultKeyValue("adv.", "adv."));
        filtres.add(new Filtre("Cat. gram.","Catégorie grammaticale", catgramClésValeurs));

        // Ajout de la liste des genres
        List<DefaultKeyValue> genreClésValeurs = new ArrayList<DefaultKeyValue>(2);
        genreClésValeurs.add(new DefaultKeyValue("m.", "m."));
        genreClésValeurs.add(new DefaultKeyValue("f.", "f."));
        filtres.add(new Filtre("Genre","Genre", genreClésValeurs));

        // Ajout de la liste des nombres
        List<DefaultKeyValue> nombreClésValeurs = new ArrayList<DefaultKeyValue>(3);
        nombreClésValeurs.add(new DefaultKeyValue("s.", "s."));
        nombreClésValeurs.add(new DefaultKeyValue("inv.", "inv."));
        nombreClésValeurs.add(new DefaultKeyValue("pl.", "pl."));
        filtres.add(new Filtre("Nombre","Nombre", nombreClésValeurs));

    }

    public long getCorpusId()
    {
        return corpusId;
    }

    public void setCorpusId(long corpusId)
    {
        this.corpusId = corpusId;
    }
    
    
    class Filtre {
        
        public String nom;
        public String description;
        public List<DefaultKeyValue> keyValues;
        
        public Filtre(String nom, String description, List<DefaultKeyValue> keyValues)
        {
            super();
            this.nom = nom;
            this.description = description;
            this.keyValues = keyValues;
        }
        
        
        
    }

}
