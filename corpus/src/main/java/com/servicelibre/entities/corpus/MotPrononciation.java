package com.servicelibre.entities.corpus;

import java.text.Collator;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "mot_id", "prononciation_id" }))
public class MotPrononciation {

	static Collator collator = Collator.getInstance(Locale.CANADA_FRENCH);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne(optional = false, fetch=FetchType.EAGER)
	Mot mot;

	@ManyToOne(optional = false, fetch=FetchType.EAGER)
	Prononciation prononciation;

	@Column
	String note;

	public MotPrononciation(Mot mot, Prononciation prononciation) {
		super();
		this.mot = mot;
		this.prononciation = prononciation;
	}

	public MotPrononciation(Mot mot, Prononciation prononciation, String note) {
		super();
		this.mot = mot;
		this.prononciation = prononciation;
		this.note = note;
	}
	
	

	public MotPrononciation() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Mot getMot() {
		return mot;
	}

	public void setMot(Mot mot) {
		this.mot = mot;
	}

	public Prononciation getPrononciation() {
		return prononciation;
	}

	public void setPrononciation(Prononciation prononciation) {
		this.prononciation = prononciation;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
