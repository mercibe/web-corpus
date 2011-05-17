package com.servicelibre.corpus.liste;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "liste_id", "mot" }))
public class Mot {
	@Id
	@GeneratedValue
	private int id;

	@ManyToOne(optional = false)
	private Liste liste;

	@Column(nullable = false)
	String mot;

	@Column
	String lemme;

	@Column
	boolean isLemme;

	@Column
	String catgram;

	@Column
	String note;

	public Mot() {
		super();
	}

	public Mot(String mot, String lemme, boolean isLemme, String catgram) {
		this(mot, lemme, isLemme, catgram, null, null);
	}

	public Mot(String mot, String lemme, boolean isLemme, String catgram, String note, Liste liste) {
		super();
		this.mot = mot;
		this.lemme = lemme;
		this.isLemme = isLemme;
		this.catgram = catgram;
		this.note = note;
		this.liste = liste;
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

}