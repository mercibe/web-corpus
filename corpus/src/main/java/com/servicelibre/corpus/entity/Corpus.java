package com.servicelibre.corpus.entity;

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
    
    /**
     * Chemin filesystem qui pointe vers la racine du dossier des données de ce corpus (index Lucene, etc.)
     */
    @Column
    String dossierData;
    
    @Column
    String analyseurRechercheFQCN = "com.servicelibre.corpus.analyzis.FrenchAnalyzer";
    
    @Column
    String analyseurLexicalFQCN = "org.apache.lucene.analysis.standard.StandardAnalyzer";
    

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
    

    public Corpus(String nom, String description, String dossierData, String analyseurRechercheFQCN,
            String analyseurLexicalFQCN)
    {
        super();
        this.nom = nom;
        this.description = description;
        this.dossierData = dossierData;
        this.analyseurRechercheFQCN = analyseurRechercheFQCN;
        this.analyseurLexicalFQCN = analyseurLexicalFQCN;
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

    
    
    public String getDossierData()
    {
        System.err.println("GET: " + this.dossierData);
        return this.dossierData;
    }

    public void setDossierData(String dossierData)
    {
        this.dossierData = dossierData;
        System.err.println("SET: " + this.dossierData);
    }

    public String getAnalyseurRechercheFQCN()
    {
        return analyseurRechercheFQCN;
    }

    public void setAnalyseurRechercheFQCN(String analyseurRechercheFQCN)
    {
        this.analyseurRechercheFQCN = analyseurRechercheFQCN;
    }

    public String getAnalyseurLexicalFQCN()
    {
        return analyseurLexicalFQCN;
    }

    public void setAnalyseurLexicalFQCN(String analyseurLexicalFQCN)
    {
        this.analyseurLexicalFQCN = analyseurLexicalFQCN;
    }

    @Override
    public String toString()
    {
        return "Corpus [id=" + id + ", nom=" + nom + ", description=" + description + "]";
    }

}