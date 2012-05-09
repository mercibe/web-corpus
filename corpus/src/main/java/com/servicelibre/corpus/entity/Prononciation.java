package com.servicelibre.corpus.entity;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "prononciation" }))
public class Prononciation implements Comparable<Prononciation> {

	static Collator collator = Collator.getInstance(Locale.CANADA_FRENCH);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false)
	public String prononciation;

	@OneToMany(mappedBy = "prononciation", cascade = CascadeType.ALL)
	private List<MotPrononciation> motPrononciations = new ArrayList<MotPrononciation>();

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
		return "Prononciation [id=" + id + ", prononciation=" + prononciation + ", nbMotsMappés=" + motPrononciations.size() + "]";
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

	public String getPrononciation() {
		return prononciation;
	}

	public void setPrononciation(String prononciation) {
		this.prononciation = prononciation;
	}

	public List<MotPrononciation> getMotPrononciations() {
		return motPrononciations;
	}

	public void setMotPrononciations(List<MotPrononciation> motPrononciations) {
		this.motPrononciations = motPrononciations;
	}

}
