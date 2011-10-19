package com.servicelibre.zk.controller;

public class RechercheContexte extends Recherche {

	enum PrécisionChaîne {EXACTEMENT, FORMES}
	
	// cible = toujours contexte
	
	@Override
	public String getDescription() {
		return "Tous les contextes du mot " + this.chaîne;
	}

}
