package com.servicelibre.zk.controller;

import com.servicelibre.corpus.manager.FiltreRecherche;

public abstract class Recherche {
	
	// Sur quoi porte la recherche 
	String cible;
	
	// La chaîne de caractère à rechercher
	String chaîne;
	
	// Précision additionnelle sur la chaîne de caractère
	String précisionChaîne;
	
	// Information complémentaire sur le résultat attendu (nombre maximum, phrase complète, voisinnage, etc.)
	String précisionRésultat;
	

	// Les filtres à appliquer
	FiltreRecherche filtres;
	
	public abstract String getDescription();
	
	
	
}
