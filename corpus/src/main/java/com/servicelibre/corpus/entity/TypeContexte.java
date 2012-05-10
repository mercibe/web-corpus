package com.servicelibre.corpus.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class TypeContexte {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int Id;

	@Column(nullable = false, unique = true)
	String nom;

	@Column
	String description;

	@OneToMany(mappedBy = "typeContexte", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Contexte> contextes = new ArrayList<Contexte>();

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
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

	public List<Contexte> getContextes() {
		return contextes;
	}

	public void setContextes(List<Contexte> contextes) {
		this.contextes = contextes;
	}
	
	
	
	
}
