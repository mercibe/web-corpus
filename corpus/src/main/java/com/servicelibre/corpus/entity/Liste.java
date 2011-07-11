package com.servicelibre.corpus.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cascade;

import com.servicelibre.corpus.liste.LigneSplitter;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "nom"))
public class Liste implements Comparable<Liste> {

	@Id
	@GeneratedValue
	long id;

	@Column
	String nom;

	@Column
	String description;

	@Column
	Integer ordre;

	/*
	 * Liste est maître de la relation ManyToMany avec Mot (il n'a pas le «
	 * mappedBy ») Il est donc responsable de la gestion bi-directionnelle de la
	 * relation (insert/update)
	 */
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "liste_mot")
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)   
	List<Mot> mots = new ArrayList<Mot>();

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
		return "Liste [id=" + id + ", nom=" + nom + ", description=" + description + ", corpus=" + corpus + "]";
	}

	/*
	 * Gestion relation bidirectionnelle liste/mot
	 */

	public List<Mot> getMots() {
		return Collections.unmodifiableList(mots);
	}

	public void ajouteMot(Mot mot) {
		if (!mots.contains(mot)) {
			mots.add(mot);
		}

		if (!mot.getListes().contains(this)) {
			mot.getListes().add(this);
		}
	}

	public boolean supprimeMot(Mot mot) {
		boolean succes = true;
		succes = succes && mots.remove(mot);
		succes = succes && mot.getListes().remove(this);
		return succes;
	}

	public int supprimeMots() {
		
		//int deleted = getMots().size();
		//getMots().clear();
		int deleted = 0;
		for (Mot mot : mots) {
			if (supprimeMot(mot)) {
				deleted++;
			}
		}
		return deleted;
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
