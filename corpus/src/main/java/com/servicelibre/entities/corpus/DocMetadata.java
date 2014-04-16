package com.servicelibre.entities.corpus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "corpus_id", "champindex" }))
public class DocMetadata {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;

	@Column
	String nom;

	@Column
	String description;

	@Column
	String champIndex;

	@Column
	int ordre;

	@Column(nullable = false)
	Boolean primaire = false;

	@Column(nullable = false)
	Boolean filtre = false;
	
	@Column(nullable = true)
	String remarqueValeurFiltre;

	/**
	 * Rôle dont il faut disposer pour accéder à cette métadonnée
	 */
	@ManyToOne(optional = true)
	Rôle rôle;
	
	/**
	 * Liste de valeurs (id séparés par des virgules) qu'il faut cacher. 
	 */
	@Column
	String valeursCachées;

	/*
	 * DocMetadata est maître de la relation OneToMany (il n'a pas le « mappedBy») Il
	 * est donc responsable de la gestion bi-directionnelle de la relation
	 * (insert/update) avec Corpus
	 */
	@ManyToOne
	@JoinColumn(name = "corpus_id", nullable = true)
	Corpus corpus;

	public DocMetadata() {
		super();
	}

	public DocMetadata(String nom, String description, String champIndex, int ordre, Corpus corpus) {
		super();
		this.nom = nom;
		this.description = description;
		this.champIndex = champIndex;
		this.ordre = ordre;
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

	public String getChampIndex() {
		return champIndex;
	}

	public void setChampIndex(String champIndex) {
		this.champIndex = champIndex;
	}

	public int getOrdre() {
		return ordre;
	}

	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}

	public Corpus getCorpus() {
		return corpus;
	}

	public void setCorpus(Corpus corpus) {
		this.corpus = corpus;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean isPrimaire() {
		return this.primaire;
	}

	public void setPrimaire(Boolean primaire) {
		this.primaire = primaire;
	}

	public Boolean isFiltre() {
		return filtre;
	}

	public void setFiltre(Boolean filtre) {
		this.filtre = filtre;
	}

	public Rôle getRôle() {
		return rôle;
	}

	public void setRôle(Rôle rôle) {
		this.rôle = rôle;
	}
	

	public String getRemarqueValeurFiltre() {
		return remarqueValeurFiltre;
	}

	public void setRemarqueValeurFiltre(String remarqueValeurFiltre) {
		this.remarqueValeurFiltre = remarqueValeurFiltre;
	}
	

	public String getValeursCachées() {
		return valeursCachées;
	}

	public void setValeursCachées(String valeursCachées) {
		this.valeursCachées = valeursCachées;
	}

	@Override
	public String toString() {
		return "DocMetadata [id=" + id + ", nom=" + nom + ", description=" + description + ", champIndex=" + champIndex + ", ordre="
				+ ordre + ", primaire=" + primaire + "]";
	}

}
