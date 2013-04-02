package com.servicelibre.entities.corpus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


@Entity
public class UtilisateurRôle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;

	@ManyToOne(optional = false)
	Utilisateur utilisateur;

	@ManyToOne(optional = false)
	Rôle rôle;

	@Column
	String note;

	
	
	public UtilisateurRôle(Utilisateur utilisateur, Rôle rôle, String note) {
		super();
		this.utilisateur = utilisateur;
		this.rôle = rôle;
		this.note = note;
	}

	public UtilisateurRôle() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Rôle getRôle() {
		return rôle;
	}

	public void setRôle(Rôle rôle) {
		this.rôle = rôle;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
