package com.servicelibre.corpus.entity;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"lemme", "mot", "catgram", "genre" }))
/**
 * Cette classe représente un conteneur d'information pour un mot donné de la langue française.
 * 
 * @author mercibe
 *
 */
public class Mot implements Comparable<Mot> {

	static Collator collator = Collator.getInstance(Locale.CANADA_FRENCH);

	@Id
	@SequenceGenerator(name = "mot_seq", sequenceName = "mot_seq")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mot_seq")
	private long id;

	// Liste primaire
	@OneToOne
	private Liste liste;

	/*
	 * Mot est maître de la relation ManyToMany (il n'a pas le « mappedBy») Il
	 * est donc responsable de la gestion bi-directionnelle de la relation
	 * (insert/update)
	 */
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "mot_liste")
	Collection<Liste> listes = new ArrayList<Liste>();

	@Column(nullable = false)
	String mot;

	
	/**
	 * Graphie alternative.  Si la graphie du mot est rectifiée (ro = true), ce champ contient la graphie traditionnelle.
	 * Si la graphie du mot est traditionnelle, ce champ contient la graphie rectifiée.
	 */
	@Column
	String autreGraphie;
	
	@Column
	public String lemme;

	@Column
	boolean ro;
	
	@Column
	public boolean isLemme;

	/*
	 * Mot est maître de la relation ManyToMany (il n'a pas le « mappedBy») Il
	 * est donc responsable de la gestion bi-directionnelle de la relation
	 * (insert/update)
	 */
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "mot_prononciation")
	Collection<Prononciation> prononciations = new ArrayList<Prononciation>();

	@Column(nullable = false)
	String catgram;

	/**
	 * Genre du mot: m., f., épicène
	 */
	@Column
	String genre;

	/**
	 * Nombre du mot: sing., pl., inv.
	 */
	@Column
	String nombre;

	/**
	 * Information additionnelles sur la classe du mot (et donc du lemme également)
	 * Transitivité du verbe (tr. dir., tr. indir. ou  intr.)
	 * Mais aussi: pron., impers., etc.
	 */
	@Column(name = "catgram_precision")
	String catgramPrésicion;

	@Column
	String note;

	public Mot() {
		super();
	}

	public Mot(String mot, String lemme, boolean isLemme, String catgram) {
		this(mot, lemme, isLemme, catgram, null, null);
	}

	public Mot(String mot, String lemme, boolean isLemme, String catgram,
			String note, Liste liste) {
		this(liste, mot, lemme, isLemme, catgram, "", "", "", false, note);
	}

	public Mot(Liste liste, String mot, String lemme, boolean isLemme,
			String catgram, String genre, String nombre,
			String catgramPrécision, boolean ro, String note) {
		super();
		this.liste = liste;
		this.listes.add(liste);
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
		return "Mot [id=" + id + ", liste=" + liste + ", mot=" + mot
				+ ", lemme=" + lemme + ", isLemme=" + isLemme
				+ ", prononciations=" + prononciations + ", catgram=" + catgram
				+ ", genre=" + genre + ", nombre=" + nombre
				+ ", catgramPrésicion=" + catgramPrésicion + ", ro=" + ro
				+ ", note=" + note + "]";
	}

	public Liste getListe() {
		return liste;
	}

	public void setListe(Liste liste) {
		this.liste = liste;
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

	
	
	public String getMot_autreGraphie() {
		return autreGraphie;
	}

	public void setAutreGraphie(String autreGraphie) {
		this.autreGraphie = autreGraphie;
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

	
	
	public String getPrononciationsString() {
		StringBuilder prononcs = new StringBuilder();
		String sep = "";
		for (Prononciation p : prononciations) {
			prononcs.append(sep).append("[").append(p.prononciation).append("]");
			sep = ", ";
		}
		return prononcs.toString();
	}

	public Collection<Liste> getListes() {
		return listes;
	}

	public void setListes(Collection<Liste> listes) {
		this.listes = listes;
	}

	
	
	public String getLemme() {
		return lemme;
	}

	public void setLemme(String lemme) {
		this.lemme = lemme;
	}

	public String getAutreGraphie() {
		return autreGraphie;
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

	/**
	 * Gestion de l'ajout de couple mot/liste (étiquette) dans la relation
	 * ManyToMany qui unit Mot et Liste
	 * 
	 * @param liste
	 * @return
	 */
	public Mot ajouteListe(Liste liste) {
		if (!getListes().contains(liste)) {
			getListes().add(liste);
		}

		if (!liste.getMots().contains(this)) {
			liste.getMots().add(this);
		}

		return this;
	}

}
