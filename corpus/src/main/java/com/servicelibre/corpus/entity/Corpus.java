package com.servicelibre.corpus.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "nom"))
public class Corpus {

	@Id
	@SequenceGenerator(name = "corpus_seq", sequenceName = "corpus_seq", allocationSize=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "corpus_seq")
	long id;

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
	String analyseurRechercheFQCN = "com.servicelibre.corpus.analysis.FrenchAnalyzer";

	@Column
	String analyseurLexicalFQCN = "org.apache.lucene.analysis.standard.StandardAnalyzer";
	
	@Column
	boolean parDéfaut;

	public Corpus() {
		super();
	}

	public Corpus(String nom, String description) {
		super();
		this.nom = nom;
		this.description = description;
	}

	public Corpus(String nom, String description, String dossierData, String analyseurRechercheFQCN, String analyseurLexicalFQCN) {
		super();
		this.nom = nom;
		this.description = description;
		this.dossierData = dossierData;
		this.analyseurRechercheFQCN = analyseurRechercheFQCN;
		this.analyseurLexicalFQCN = analyseurLexicalFQCN;
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

	public String getDossierData() {
		return this.dossierData;
	}

	public void setDossierData(String dossierData) {
		this.dossierData = dossierData;
	}

	public String getAnalyseurRechercheFQCN() {
		return analyseurRechercheFQCN;
	}

	public void setAnalyseurRechercheFQCN(String analyseurRechercheFQCN) {
		this.analyseurRechercheFQCN = analyseurRechercheFQCN;
	}

	public String getAnalyseurLexicalFQCN() {
		return analyseurLexicalFQCN;
	}

	public void setAnalyseurLexicalFQCN(String analyseurLexicalFQCN) {
		this.analyseurLexicalFQCN = analyseurLexicalFQCN;
	}
	
	public boolean isParDéfaut() {
		return parDéfaut;
	}

	public void setParDéfaut(boolean parDéfaut) {
		this.parDéfaut = parDéfaut;
	}

	@Override
	public String toString() {
		return "Corpus [id=" + id + ", nom=" + nom + ", description=" + description + "]";
	}

}
