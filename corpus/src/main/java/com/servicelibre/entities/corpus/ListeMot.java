package com.servicelibre.entities.corpus;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Association d'un mot à une liste
 * 
 * @author benoitm
 * 
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "liste_id", "mot_id" }))
public class ListeMot {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long Id;

	@ManyToOne(optional = false)
	Mot mot;

	@ManyToOne(optional = false)
	Liste liste;

	@OneToMany(mappedBy = "listeMot", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Contexte> contextes = new ArrayList<Contexte>();

	@Column
	String note;

	public ListeMot() {
		super();
	}

	public ListeMot(Mot mot, Liste liste) {
		super();
		this.mot = mot;
		this.liste = liste;
		this.note = "";
	}

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

	public Mot getMot() {
		return mot;
	}

	public void setMot(Mot mot) {
		this.mot = mot;
	}

	public Liste getListe() {
		return liste;
	}

	public void setListe(Liste liste) {
		this.liste = liste;
	}

	public List<Contexte> getContextes() {
		return contextes;
	}

	public void setContextes(List<Contexte> contextes) {
		this.contextes = contextes;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
