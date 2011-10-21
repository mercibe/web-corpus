package com.servicelibre.corpus.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "nom"))
public class Liste implements Comparable<Liste> {

    @Id
    @SequenceGenerator(name = "liste_seq", sequenceName = "liste_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "liste_seq")
    Long id;

    @Column
    String nom;

    @Column
    String description;

    @Column
    Integer ordre;

    @ManyToMany(mappedBy = "listes", cascade = CascadeType.ALL)
    private List<Mot> mots = new ArrayList<Mot>();

    @ManyToOne
    @JoinColumn(name = "corpus_id")
    Corpus corpus;

    @ManyToOne
    @JoinColumn(name = "catégorie_id", nullable=false)
    CatégorieListe catégorie;

    @Transient
    File fichierSource;

    @Transient
    String fichierEncoding = "UTF-8";

    @Transient
    boolean listesPrimaire = true;

    public Liste() {
	super();
    }

    public Liste(String nom, String description) {
	this(nom, description, null);
    }

    public Liste(String nom, String description, Corpus corpus) {
	super();
	this.nom = nom;
	this.description = description;
	this.corpus = corpus;
    }

    public Liste(long id) {
	this.id = id;
    }

    public Long getId() {
	return id;
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

    public void setMots(List<Mot> mots) {
	this.mots = mots;
    }

    public int size() {
	return mots.size();
    }

    public Corpus getCorpus() {
	return corpus;
    }

    public void setCorpus(Corpus corpus) {
	if (this.corpus != corpus) {
	    if (this.corpus != null) {
		this.corpus.enleverListe(this);
	    }
	    this.corpus = corpus;
	    if (corpus != null) {
		corpus.ajouterListe(this);
	    }
	}
    }

    @Override
    public String toString() {
	return "Liste [id=" + id + ", nom=" + nom + ", description=" + description + ", corpus=" + corpus + "]";
    }

    /*
     * Gestion relation bidirectionnelle liste/mot
     */

    public List<Mot> getMots() {
	return mots;
    }

    public void ajouteMot(Mot mot) {
	mot.setListe(this);
    }

    public void supprimeMot(Mot mot) {
	mot.setListe(null);
    }

    public File getFichierSource() {
	return fichierSource;
    }

    public void setFichierSource(File fichierSource) {
	this.fichierSource = fichierSource;
    }

    public void setId(long id) {
	this.id = id;
    }

    public Integer getOrdre() {
	return ordre;
    }

    public void setOrdre(Integer ordre) {
	this.ordre = ordre;
    }

    public String getFichierEncoding() {
	return fichierEncoding;
    }

    public void setFichierEncoding(String fichierEncoding) {
	this.fichierEncoding = fichierEncoding;
    }

    @Override
    public int compareTo(Liste o) {
	return this.getNom().compareTo(o.getNom());
    }

    public boolean isListesPrimaire() {
	return listesPrimaire;
    }

    public void setListesPrimaire(boolean listesPrimaire) {
	this.listesPrimaire = listesPrimaire;
    }

    public CatégorieListe getCatégorie() {
	return catégorie;
    }

    public void setCatégorieListe(CatégorieListe catégorie) {
	if (this.catégorie != catégorie) {
	    if (this.catégorie != null) {
		this.catégorie.enleverListe(this);
	    }
	    this.catégorie = catégorie;
	    if (catégorie != null) {
		catégorie.ajouterListe(this);
	    }
	}

    }

}
