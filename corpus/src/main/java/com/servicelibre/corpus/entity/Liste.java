package com.servicelibre.corpus.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.servicelibre.corpus.liste.LigneSplitter;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "nom"))
public class Liste implements Comparable<Liste> {

	@Id
	@SequenceGenerator(name = "liste_seq", sequenceName = "liste_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "liste_seq")
	long id;

	@Column
	String nom;

	@Column
	String description;

	@Column
	Integer ordre;

	// @OneToMany(cascade = CascadeType.ALL)
	// @JoinColumn(name = "liste_id")
	// List<Mot> mots = new ArrayList<Mot>();

	@ManyToMany(mappedBy = "listes", cascade = CascadeType.ALL)
	private List<Mot> mots = new ArrayList<Mot>();

	@ManyToOne(optional = false)
	Corpus corpus;

	@Transient
	File fichierSource;

	@Transient
	LigneSplitter ligneSplitter;

	public Liste() {
		super();
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

	public long getId() {
		return id;
	}

	public void setId(int id) {
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
		this.corpus = corpus;
	}

	@Override
	public String toString() {
		return "Liste [id=" + id + ", nom=" + nom + ", description="
				+ description + ", corpus=" + corpus + "]";
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

	public int supprimeTousLesMots() {
		int cptSupprimé = 0;
		for (Mot mot : mots) {
			mot.setListe(null);
		}
		return cptSupprimé;
	}

	public void internalAjouteMot(Mot mot) {
		mots.add(mot);

	}

	public void internalSupprimeMot(Mot mot) {
		mots.remove(mot);

	}

	public File getFichierSource() {
		return fichierSource;
	}

	public void setFichierSource(File fichierSource) {
		this.fichierSource = fichierSource;
	}

	public LigneSplitter getLigneSplitter() {
		return ligneSplitter;
	}

	public void setLigneSplitter(LigneSplitter ligneSplitter) {
		this.ligneSplitter = ligneSplitter;
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

	@Override
	public int compareTo(Liste o) {
		return this.getNom().compareTo(o.getNom());
	}

}
