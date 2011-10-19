package com.servicelibre.zk.recherche;

public class RechercheContexte extends Recherche {

	public enum PrécisionChaîne {EXACTEMENT_LE_MOT, TOUTES_LES_FORMES_DU_MOT}
	
	// cible = toujours contexte
	
	@Override
	public String getDescription() {
		return "Tous les contextes du mot " + this.chaîne;
	}

}
