package com.servicelibre.entities.corpus;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "corpus_id", "nom" }))
public class CatégorieListe implements Comparable<CatégorieListe> {

    static Collator collator = Collator.getInstance(Locale.CANADA_FRENCH);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column
    String nom;

    @Column
    String description;

    @Column
    Integer ordre;

    @ManyToOne(optional = false)
    @JoinColumn(name = "corpus_id")
    Corpus corpus;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "catégorie", orphanRemoval = true)
    List<Liste> listes = new ArrayList<Liste>();

    @ManyToOne
    Utilisateur utilisateur;

    @Column(nullable = false)
    Boolean publique = true;
    
    @Column(nullable = false)
    Boolean partition = false;

    public CatégorieListe() {
	super();
    }

    public CatégorieListe(String nom, String description) {
	super();
	this.nom = nom;
	this.description = description;
    }

    public CatégorieListe(String nom, String description, Corpus corpus) {
	super();
	this.nom = nom;
	this.description = description;
	this.corpus = corpus;
    }

    public long getId() {
	return id;
    }

    public void setId(long id) {
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

    public Integer getOrdre() {
	return ordre;
    }

    public void setOrdre(Integer ordre) {
	this.ordre = ordre;
    }

    public List<Liste> getListes() {
	return listes;
    }

    public void setListes(List<Liste> listes) {
	this.listes = listes;
    }

    public Corpus getCorpus() {
	return corpus;
    }

    public void setCorpus(Corpus corpus) {
	this.corpus = corpus;
    }

    public Boolean getPublique() {
	return publique;
    }

    public void setPublique(Boolean publique) {
	this.publique = publique;
    }

    public Utilisateur getUtilisateur() {
	return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
	this.utilisateur = utilisateur;
    }
    
    public Boolean getPartition() {
        return partition;
    }

    public void setPartition(Boolean partition) {
        this.partition = partition;
    }

    @Override
    public String toString() {
	return "CatégorieListe [id=" + id + ", nom=" + nom + ", description=" + description + "]";
    }

    @Override
    public int compareTo(CatégorieListe autreCatégorieListe) {
	return collator.compare(this.nom, autreCatégorieListe.nom);
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((corpus == null) ? 0 : corpus.hashCode());
	result = prime * result + ((nom == null) ? 0 : nom.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	CatégorieListe other = (CatégorieListe) obj;
	if (corpus == null) {
	    if (other.corpus != null)
		return false;
	} else if (!corpus.equals(other.corpus))
	    return false;
	if (nom == null) {
	    if (other.nom != null)
		return false;
	} else if (!nom.equals(other.nom))
	    return false;
	return true;
    }

}
