package com.servicelibre.zk.recherche;

import com.servicelibre.corpus.manager.Filtre;
import com.servicelibre.corpus.manager.FiltreRecherche;
import com.servicelibre.corpus.manager.MotManager;
import com.servicelibre.corpus.manager.MotManager.Condition;

public class RechercheMot extends Recherche {

	// Précision chaîne = MotManager.Condition

	@Override
	public String getDescriptionChaîne() {

		StringBuilder desc = new StringBuilder();
		
		// Avec chaîne
		if (chaîne != null && !chaîne.isEmpty()) {
			
			desc.append("les mots qui ");
			
			String enrobée = chaîne;
			String laCible = "";
			String préposition = "à ";
			
			if (cible == Cible.PRONONCIATION) {
				enrobée = "[" + chaîne + "]";
				laCible = "la prononciation ";
			}
			else {
				enrobée = "« " + chaîne + " »";
				// la lettre, les lettres
				if (chaîne.length() == 1){
				    laCible = "la lettre ";
				}
				else
				{
				    préposition = "aux ";
				    laCible = "les lettres ";
				}
			}

			Condition précision = MotManager.Condition.valueOf(this.précisionChaîne);

			switch (précision) {
			case ENTIER:
				desc.append("correspondent exactement ").append(préposition).append(laCible).append(enrobée);
				break;
			case COMMENCE_PAR: 
				desc.append("commencent par ").append(laCible).append(enrobée);
				break;
			case FINIT_PAR:
				desc.append("se terminent par ").append(laCible).append(enrobée);
				break;
			case CONTIENT:
				desc.append("contiennent ").append(laCible).append(enrobée);
				break;
			}

		} else {
			
			desc.append("tous les mots");

		}
		return desc.toString();
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

		if (cible == Cible.PRONONCIATION) {
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

		if (cible == Cible.PRONONCIATION) {
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

		for (Filtre filtre : this.filtres.getFiltres()) {
			copieFiltres.addFiltre(filtre.getCopie());
		}

		copie.filtres = copieFiltres;

		return copie;
	}

	@Override
	public String getDescriptionPortéeFiltre() {
		return "et qui satisfont aux conditions suivantes:";
	}

}
