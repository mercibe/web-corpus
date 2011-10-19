package com.servicelibre.zk.recherche;

import com.servicelibre.corpus.manager.FiltreRecherche;

public abstract class Recherche {
	
    	public enum Cible {GRAPHIE, PRONONCIATION, CONTEXTE};
    
	// Sur quoi porte la recherche 
    	public Cible cible;
	
	// La chaîne de caractère à rechercher
    	public String chaîne;
	
	// Précision additionnelle sur la chaîne de caractère
	public String précisionChaîne;
	
	// Information complémentaire sur le résultat attendu (nombre maximum, phrase complète, voisinnage, etc.)
	public String précisionRésultat;
	

	// Les filtres à appliquer
	public FiltreRecherche filtres;
	
	public abstract String getDescription();

	public Cible getCible() {
	    return cible;
	}

	public void setCible(Cible cible) {
	    this.cible = cible;
	}

	public String getChaîne() {
	    return chaîne;
	}

	public void setChaîne(String chaîne) {
	    this.chaîne = chaîne;
	}

	public String getPrécisionChaîne() {
	    return précisionChaîne;
	}

	public void setPrécisionChaîne(String précisionChaîne) {
	    this.précisionChaîne = précisionChaîne;
	}

	public String getPrécisionRésultat() {
	    return précisionRésultat;
	}

	public void setPrécisionRésultat(String précisionRésultat) {
	    this.précisionRésultat = précisionRésultat;
	}

	public FiltreRecherche getFiltres() {
	    return filtres;
	}

	public void setFiltres(FiltreRecherche filtres) {
	    this.filtres = filtres;
	}
	
	
	
	
}
