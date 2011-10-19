package com.servicelibre.zk.controller;

public class RechercheMot extends Recherche {

	enum Cible {MOT, PRONONCIATION};
	
	// Précision chaîne = MotManager.Condition
	
	@Override
	public String getDescription() {
		return "Le mot " + this.chaîne;
	}

}
