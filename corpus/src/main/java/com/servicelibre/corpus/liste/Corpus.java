package com.servicelibre.corpus.liste;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "nom"))
public class Corpus
{

    @Id
    @GeneratedValue
    int id;

    @Column
    String nom;

    @Column
    String description;

    public Corpus()
    {
        super();
    }

    public Corpus(String nom, String description)
    {
        super();
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

    @Override
    public String toString()
    {
        return "Corpus [id=" + id + ", nom=" + nom + ", description=" + description + "]";
    }

}
