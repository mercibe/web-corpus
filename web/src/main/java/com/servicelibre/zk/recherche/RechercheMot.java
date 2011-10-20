package com.servicelibre.zk.recherche;

import com.servicelibre.corpus.manager.Filtre;
import com.servicelibre.corpus.manager.FiltreRecherche;
import com.servicelibre.corpus.manager.MotManager;
import com.servicelibre.corpus.manager.MotManager.Condition;

public class RechercheMot extends Recherche {

	// Précision chaîne = MotManager.Condition
	
	@Override
	public String getDescription() {
		return "Le mot " + this.chaîne;
	}

	/**
	 * Retourne la chaîne sous une des formes suivantes:
	 * 
	 * poires
	 * bl*
	 * *ment
	 * *ou*
	 * 
	 * recherche phonétique = entre crochets
	 * 
	 * @return
	 */
	@Override
	public String getChaîneEtPrécision() {
	   
	    StringBuilder chaînePrécsision = new StringBuilder();
	    
	    if(cible == Cible.PRONONCIATION) {
		chaînePrécsision.append("[");
	    }
	    
	    Condition précision = MotManager.Condition.valueOf(this.précisionChaîne);
	    
	    switch (précision) {
	    case ENTIER:
		chaînePrécsision.append(chaîne);
		break;
	    case COMMENCE_PAR:
		chaînePrécsision.append(chaîne).append("*");
		break;
	    case FINIT_PAR:
		chaînePrécsision.append("*").append(chaîne);
		break;
	    case CONTIENT:
		chaînePrécsision.append("*").append(chaîne).append("*");
		break;
	    default:
		chaînePrécsision.append("précision inconnue: ").append(précision);
		break;
	    }
	    
	    if(cible == Cible.PRONONCIATION) {
		chaînePrécsision.append("]");
	    }
	    
	    return chaînePrécsision.toString();
	}

	/**
	 * Deep copy d'une recherche
	 */
	@Override
	public Recherche getCopie() {
	    
	    Recherche copie = new RechercheMot();
	    
	    copie.chaîne = this.chaîne;
	    copie.cible = this.cible;
	    copie.précisionChaîne = this.précisionChaîne;
	    copie.précisionRésultat = this.précisionRésultat;
	    
	    FiltreRecherche copieFiltres = new FiltreRecherche();
	    
	    for(Filtre filtre : this.filtres.getFiltres()) {
		copieFiltres.addFiltre(filtre.getCopie());
	    }
	    
	    copie.filtres = copieFiltres;
	    
	    return copie;
	}

}
