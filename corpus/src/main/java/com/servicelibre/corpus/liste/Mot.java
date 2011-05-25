package com.servicelibre.corpus.liste;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "liste_id", "lemme", "mot", "catgram", "genre" }))
public class Mot {
	@Id
	@GeneratedValue
	private int id;

	@ManyToOne(optional = false)
	private Liste liste;

	@Column(nullable = false)
	String mot;

	@Column
	public
	String lemme;

	@Column
	public
	boolean isLemme;

	@Column(nullable = false)
	String catgram;
	
	@Column
	String genre;
	
	@Column
	String nombre;
	
	@Column (name="catgram_precision")
	String catgramPrésicion;
	
	@Column
	boolean ro;

	@Column
	String note;

	public Mot() {
		super();
	}

	public Mot(String mot, String lemme, boolean isLemme, String catgram) {
		this(mot, lemme, isLemme, catgram, null, null);
	}

	public Mot(String mot, String lemme, boolean isLemme, String catgram, String note, Liste liste) {
	    this(liste,mot,lemme, isLemme, catgram, "","", "", false, note);
	}

	public Mot(Liste liste, String mot, String lemme, boolean isLemme, String catgram, String genre, String nombre,
            String catgramPrécision, boolean ro, String note)
    {
        super();
        this.liste = liste;
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
		return "Mot [id=" + id + ", mot=" + mot + ", lemme=" + lemme + ", isLemme=" + isLemme + ", catgram=" + catgram + ", note=" + note + "]";
	}

	public Liste getListe() {
		return liste;
	}

	public void setListe(Liste liste) {
		if (this.liste != null) {
			this.liste.internalSupprimeMot(this);
		}
		this.liste = liste;
		if (liste != null) {
			liste.internalAjouteMot(this);
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
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
	
	

}
