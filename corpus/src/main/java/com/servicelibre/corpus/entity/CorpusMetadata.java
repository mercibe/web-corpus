package com.servicelibre.corpus.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class CorpusMetadata {

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

    
	public CorpusMetadata() {
		super();
	}

	public CorpusMetadata(String nom, String champIndex, int ordre, Corpus corpus) {
		super();
		this.nom = nom;
		this.champIndex = champIndex;
		this.ordre = ordre;
		this.corpus = corpus;
	}
    
    
}
