package com.servicelibre.corpus.liste;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Entity
public class Liste
{

    @Id
    @GeneratedValue
    int id;

    @Column
    String nom;

    @Column
    String description;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "liste_id")
    List<Mot> mots = new ArrayList<Mot>();

    public Liste(int id, String nom, String description)
    {
        this.id = id;
        this.nom = nom;
        this.description = description;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getNom()
    {
        return nom;
    }

    public void setNom(String nom)
    {
        this.nom = nom;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public List<Mot> getMots()
    {
        return mots;
    }

    public void setMots(List<Mot> mots)
    {
        this.mots = mots;
    }

    public int size()
    {
        return mots.size();
    }

}
