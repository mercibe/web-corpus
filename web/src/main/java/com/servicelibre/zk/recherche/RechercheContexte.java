package com.servicelibre.zk.recherche;

import com.servicelibre.corpus.manager.Filtre;
import com.servicelibre.corpus.manager.FiltreRecherche;

public class RechercheContexte extends Recherche {

	public enum PrécisionChaîne {EXACTEMENT_LE_MOT, TOUTES_LES_FORMES_DU_MOT}
	
	// cible = toujours contexte
	
	@Override
	public String getDescriptionChaîne() {

		StringBuilder desc = new StringBuilder();
		
		// Avec chaîne
		if (chaîne != null && !chaîne.isEmpty()) {
			
			desc.append("les contextes ");
			
			String chaîneEnrobée =  "« " + chaîne + " »";
			

			PrécisionChaîne précision = PrécisionChaîne.valueOf(this.précisionChaîne);

			switch (précision) {
			case EXACTEMENT_LE_MOT:
				desc.append("du mot ").append(chaîneEnrobée);
				break;
			case TOUTES_LES_FORMES_DU_MOT: 
				desc.append("de toutes les formes du mot ").append(chaîneEnrobée);
				break;
			}

		} else {
			
			desc.append("");

		}
		return desc.toString();
	}

	/**
	 * Retourne la chaîne sous une des formes suivantes:
	 * 
	 * poires
	 * [poire]
	 * 
	 * @return
	 */
	@Override
	public String getChaîneEtPrécision() {

	    PrécisionChaîne précision = PrécisionChaîne.valueOf(précisionChaîne);
	    switch(précision) {
	    case EXACTEMENT_LE_MOT:
		return chaîne;
	    case TOUTES_LES_FORMES_DU_MOT:
		return "[" + chaîne + "]";
	    }
	    
	    return "Précision inconnue: " + précision;
	}

	/**
	 * Deep copy d'une recherche
	 */
	@Override
	public Recherche getCopie() {
	    
	    Recherche copie = new RechercheContexte();
	    
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

	@Override
	public String getDescriptionPortéeFiltre() {
		return "dont les ouvrages desquels ils sont issus satisfont aux conditions suivantes:";
	}
}
