package com.servicelibre.entities.corpus;

import java.util.ArrayList;
import java.util.List;

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
public class CatégorieListe {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

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

	@Override
	public String toString() {
		return "CatégorieListe [id=" + id + ", nom=" + nom + ", description=" + description + "]";
	}

}
