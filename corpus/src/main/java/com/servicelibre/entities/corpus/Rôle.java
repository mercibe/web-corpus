package com.servicelibre.entities.corpus;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.servicelibre.entities.corpus.UtilisateurRôle;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "nom"))
public class Rôle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;

	@Column
	String nom;

	@Column
	String description;

	@OneToMany(mappedBy = "rôle", cascade = { CascadeType.REMOVE }, orphanRemoval = true)
	List<UtilisateurRôle> utilisateurRôles;

	public Rôle() {
		super();
	}

	public Rôle(String nom) {
		super();
		this.nom = nom;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
