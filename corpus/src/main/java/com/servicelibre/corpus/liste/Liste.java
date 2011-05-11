package com.servicelibre.corpus.liste;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "liste")
public class Liste {
	@Id
	String id;
	
	@Column
	String nom;
	
	@Column
	String description;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="liste_id")
	List<Lemme> lemmes = new ArrayList<Lemme>();

	public Liste(String id, String nom, String description) {
		this.id = id;
		this.nom = nom;
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public List<Lemme> getLemmes() {
		return lemmes;
	}

	public void setLemmes(List<Lemme> lemmes) {
		this.lemmes = lemmes;
	}

	public int size() {
		return lemmes.size();
	}

}
