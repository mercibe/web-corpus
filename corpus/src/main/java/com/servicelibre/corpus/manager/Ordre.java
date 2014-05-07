package com.servicelibre.corpus.manager;

/**
 * Un Ordre = un nom logique + un nom de colonne BD + une direction
 * 
 * @author mercibe
 * 
 */
public class Ordre implements Comparable<Ordre> {

	public String nom;
	public String nomColonne;
	public boolean ascendant = true;

	public Ordre(String nomColonne, boolean ascendant) {
		this(nomColonne, nomColonne, ascendant);
	}

	public Ordre(String nom, String nomColonne) {
		this(nom, nomColonne, true);
	}

	public Ordre(String nom, String nomColonne, boolean ascendant) {
		super();
		this.nom = nom;
		this.nomColonne = nomColonne;
		this.ascendant = ascendant;
	}

	@Override
	public int compareTo(Ordre o) {
		return this.nomColonne.compareTo(o.nomColonne);
	}

	@Override
	public String toString() {
		return nomColonne + " " + ascendant;
	}

}