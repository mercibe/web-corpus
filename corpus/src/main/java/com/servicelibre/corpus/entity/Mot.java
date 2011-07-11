package com.servicelibre.corpus.entity;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "mot", "lemme", "catgram", "genre" }))
public class Mot implements Comparable<Mot> {

	static Collator collator = Collator.getInstance(Locale.CANADA_FRENCH);

	@Id
	@GeneratedValue
	private long id;

	@Column(nullable = false)
	String mot;

	@Column
	public String lemme;

	@Column
	public boolean isLemme;

	/*
	 * Mot est maître de la relation ManyToMany avec Prononciation (il n'a pas
	 * le « mappedBy») Il est donc responsable de la gestion bi-directionnelle
	 * de la relation (insert/update)
	 */
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "mot_prononciation")
	Collection<Prononciation> prononciations = new ArrayList<Prononciation>();

	/**
	 * Mot est « esclave » de la relation ManyToMany avec Liste
	 */
	@ManyToMany(mappedBy = "mots", cascade = CascadeType.ALL)
	private Collection<Liste> listes = new ArrayList<Liste>();

	@Column(nullable = false)
	String catgram;

	@Column
	String genre;

	@Column
	String nombre;

	@Column(name = "catgram_precision")
	String catgramPrésicion;

	@Column
	boolean ro;

	@Column
	String note;

	public Mot() {
		super();
	}

	public Mot(String mot, String lemme, boolean isLemme, String catgram) {
		this(mot, lemme, isLemme, catgram, null);
	}

	public Mot(String mot, String lemme, boolean isLemme, String catgram, String note) {
		this(mot, lemme, isLemme, catgram, "", "", "", false, note);
	}

	public Mot(String mot, String lemme, boolean isLemme, String catgram, String genre, String nombre, String catgramPrécision, boolean ro, String note) {
		super();
		this.mot = mot;
		this.lemme = lemme;
		this.isLemme = isLemme;
		this.catgram = catgram;
		this.genre = genre;
		this.nombre = nombre;
		this.catgramPrésicion = catgramPrécision;
		this.ro = ro;
		this.note = note;
	}

	@Override
	public String toString() {
		return "Mot [id=" + id + ", mot=" + mot + ", lemme=" + lemme + ", isLemme=" + isLemme + ", prononciations=" + prononciations + ", catgram=" + catgram
				+ ", genre=" + genre + ", nombre=" + nombre + ", catgramPrésicion=" + catgramPrésicion + ", ro=" + ro + ", note=" + note + "]";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMot() {
		return mot;
	}

	public void setMot(String mot) {
		this.mot = mot;
	}

	public String getCatgram() {
		return catgram;
	}

	public void setCatgram(String catgram) {
		this.catgram = catgram;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCatgramPrésicion() {
		return catgramPrésicion;
	}

	public void setCatgramPrésicion(String catgramPrésicion) {
		this.catgramPrésicion = catgramPrésicion;
	}

	public boolean isRo() {
		return ro;
	}

	public void setRo(boolean ro) {
		this.ro = ro;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Collection<Prononciation> getPrononciations() {
		return prononciations;
	}

	public void setPrononciations(Collection<Prononciation> prononciations) {
		this.prononciations = prononciations;
	}

	public Collection<Liste> getListes() {
		return listes;
	}

	public void setListes(Collection<Liste> listes) {
		this.listes = listes;
	}

	@Override
	public int compareTo(Mot autreMot) {
		return collator.compare(this.lemme, autreMot.lemme);
	}

	/**
	 * Gestion de l'ajout de couple mot/prononciation dans la relation
	 * ManyToMany qui unit Mot et Prononciation
	 * 
	 * @param prononc
	 * @return
	 */
	public Mot ajoutePrononciation(Prononciation prononc) {
		
		if (!getPrononciations().contains(prononc)) {
			getPrononciations().add(prononc);
		}

		if (!prononc.getMots().contains(this)) {
			prononc.getMots().add(this);
		}

		return this;
	}

}
