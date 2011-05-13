package com.servicelibre.corpus.liste;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Liste
{

    @Id
    @GeneratedValue
    long id;

    @Column
    String nom;

    @Column
    String description;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "liste_id")
    List<Mot> mots;

    @ManyToOne(optional = false)
    Corpus corpus;

    public Liste()
    {
        super();
    }

    public Liste(String nom, String description, Corpus corpus)
    {
        this.nom = nom;
        this.description = description;
        this.corpus = corpus;
    }

    public long getId()
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

    public Corpus getCorpus()
    {
        return corpus;
    }

    public void setCorpus(Corpus corpus)
    {
        this.corpus = corpus;
    }

    @Override
    public String toString()
    {
        return "Liste [id=" + id + ", nom=" + nom + ", description=" + description + ", corpus=" + corpus + "]";
    }

}
