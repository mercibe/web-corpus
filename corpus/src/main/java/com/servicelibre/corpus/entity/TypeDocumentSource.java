package com.servicelibre.corpus.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class TypeDocumentSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    @Column
    String nom;

    @Column
    String description;

    @Column
    String extracteurDeDonnéesFQCN;

    public TypeDocumentSource(String nom, String description, String extracteurDeDonnéesFQCN) {
	super();
	this.nom = nom;
	this.description = description;
	this.extracteurDeDonnéesFQCN = extracteurDeDonnéesFQCN;
    }

    public TypeDocumentSource() {
	super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExtracteurDeDonnéesFQCN() {
        return extracteurDeDonnéesFQCN;
    }

    public void setExtracteurDeDonnéesFQCN(String extracteurDeDonnéesFQCN) {
        this.extracteurDeDonnéesFQCN = extracteurDeDonnéesFQCN;
    }
    
    
}
