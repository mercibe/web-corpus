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
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "prononciation" }))
public class Prononciation implements Comparable<Prononciation> {

	static Collator collator = Collator.getInstance(Locale.CANADA_FRENCH);

	@Id
	@SequenceGenerator(name = "prononciation_seq", sequenceName = "prononciation_seq", allocationSize=100)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prononciation_seq")
	private long id;

	@ManyToMany(mappedBy = "prononciations", cascade = CascadeType.ALL)
	private Collection<Mot> mots = new ArrayList<Mot>();

	@Column(nullable = false)
	public String prononciation;

	public Prononciation() {
		super();
	}

	public Prononciation(String prononciation) {

		super();

		this.prononciation = prononciation;
	}

	@Override
	public String toString() {
		// return "Prononciation [id=" + id + ", mot=" + mots + ", prononciation=" + prononciation + "]";
		return "Prononciation [id=" + id + ", prononciation=" + prononciation + ", nbMotsMappés=" + getMots().size() + "]";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public int compareTo(Prononciation autreMot) {
		return collator.compare(this.prononciation, autreMot.prononciation);
	}

	public Collection<Mot> getMots() {
		return mots;
	}

	public void setMots(Collection<Mot> mots) {
		this.mots = mots;
	}

}
