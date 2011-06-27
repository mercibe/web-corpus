package com.servicelibre.corpus.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class DocMetadata {

	@Id
	@GeneratedValue
	long id;
	
	@Column
	String nom;
	
	@Column
	String champIndex;
	
	@Column
	int ordre;
	
    @ManyToOne(optional = false)
    Corpus corpus;

    
	public DocMetadata() {
		super();
	}

	public DocMetadata(String nom, String champIndex, int ordre, Corpus corpus) {
		super();
		this.nom = nom;
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
	
	
    
    
}
