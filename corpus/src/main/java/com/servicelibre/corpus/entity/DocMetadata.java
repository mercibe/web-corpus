package com.servicelibre.corpus.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

@Entity
public class DocMetadata {

	@Id
	@SequenceGenerator(name = "docmetadata_seq", sequenceName = "docmetadata_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "docmetadata_seq")
	long id;

	@Column
	String nom;

	@Column
	String description;

	@Column
	String champIndex;

	@Column
	int ordre;

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
		if (this.corpus != corpus) {
			if (this.corpus != null) {
				this.corpus.enleverDocMetadata(this);
			}
			this.corpus = corpus;
			if (corpus != null) {
				corpus.ajouterDocMetadata(this);
			}
		}

	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "DocMetadata [id=" + id + ", nom=" + nom + ", description=" + description + ", champIndex=" + champIndex + ", ordre=" + ordre + "]";
	}

}
