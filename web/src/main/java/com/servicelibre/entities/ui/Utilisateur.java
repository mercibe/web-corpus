package com.servicelibre.entities.ui;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "pseudo"))
public class Utilisateur {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;

	@Column
	String pseudo;

	@Column
	String prénom;

	@Column
	String nom;

	@Column
	String courriel;

	@Column
	String motDePasse;

	@OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.EAGER)
	List<UtilisateurRôle> utilisateurRôles;

	public Utilisateur() {
		super();
	}

	
	
	public Utilisateur(String pseudo, String prénom, String nom, String courriel, String motDePasse) {
		super();
		this.pseudo = pseudo;
		this.prénom = prénom;
		this.nom = nom;
		this.courriel = courriel;
		this.motDePasse = motDePasse;
	}



	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPseudo() {
		return pseudo;
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	public String getPrénom() {
		return prénom;
	}

	public void setPrénom(String prénom) {
		this.prénom = prénom;
	}

	public String getCourriel() {
		return courriel;
	}

	public void setCourriel(String courriel) {
		this.courriel = courriel;
	}

	public String getMotDePasse() {
		return motDePasse;
	}

	public void setMotDePasse(String motDePasse) {
		this.motDePasse = motDePasse;
	}



	public List<UtilisateurRôle> getUtilisateurRôles() {
		return utilisateurRôles;
	}



	public void setUtilisateurRôles(List<UtilisateurRôle> utilisateurRôles) {
		this.utilisateurRôles = utilisateurRôles;
	}

	
	
}
