package com.servicelibre.corpus.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "nom"))
public class Corpus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;

	@Column
	String nom;

	@Column
	String description;

	/**
	 * Chemin filesystem qui pointe vers la racine du dossier des données de ce
	 * corpus (index Lucene, etc.)
	 */
	@Column
	String dossierData;

	@Column
	String analyseurRechercheFQCN = "com.servicelibre.corpus.analysis.FrenchAnalyzer";

	@Column
	String analyseurLexicalFQCN = "org.apache.lucene.analysis.standard.StandardAnalyzer";

	@Column
	Boolean parDéfaut;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "corpus", orphanRemoval = true, fetch = FetchType.EAGER)
	@OrderBy("ordre")
	List<DocMetadata> métadonnéesDoc = new ArrayList<DocMetadata>();

//	@OneToMany(cascade = CascadeType.ALL, mappedBy = "corpus", orphanRemoval = true)
//	List<Liste> listes = new ArrayList<Liste>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "corpus", orphanRemoval = true)
	List<CatégorieListe> catégoriesListes = new ArrayList<CatégorieListe>();

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

	public List<DocMetadata> getMétadonnéesDoc() {
		return métadonnéesDoc;
	}

	@Override
	public String toString() {
		return "Corpus [id=" + id + ", nom=" + nom + ", description=" + description + ", dossierData=" + dossierData + ", parDéfaut="
				+ parDéfaut + "]";
	}

	public void setMétadonnéesDoc(List<DocMetadata> métadonnéesDoc) {
		this.métadonnéesDoc = métadonnéesDoc;
	}

	public Boolean getParDéfaut() {
		return parDéfaut;
	}

	public void setParDéfaut(Boolean parDéfaut) {
		this.parDéfaut = parDéfaut;
	}

	public List<CatégorieListe> getCatégoriesListes() {
		return catégoriesListes;
	}

	public void setCatégoriesListes(List<CatégorieListe> catégoriesListes) {
		this.catégoriesListes = catégoriesListes;
	}

	
}
