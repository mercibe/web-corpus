package com.servicelibre.zk.recherche;

public class RechercheMot extends Recherche {

	// Précision chaîne = MotManager.Condition
	
	@Override
	public String getDescription() {
		return "Le mot " + this.chaîne;
	}

}
