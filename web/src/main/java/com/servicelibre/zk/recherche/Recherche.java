package com.servicelibre.zk.recherche;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;

import com.servicelibre.corpus.manager.Filtre;
import com.servicelibre.corpus.manager.FiltreRecherche;

public abstract class Recherche {

	public enum Cible {
		GRAPHIE, PRONONCIATION, CONTEXTE
	};

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

	public abstract String getDescriptionChaîne();
	public abstract String getDescriptionPortéeFiltre();

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

	public boolean isFiltrée() {
		return filtres.getFiltres().size() > 0;
	}

	public abstract String getChaîneEtPrécision();

	public int getNombreConditions() {
		int nbCondition = 0;
		for (Filtre f : filtres.getFiltres()) {
			nbCondition += f.getKeyValues().size();
		}
		return nbCondition;
	}

	public abstract Recherche getCopie();
	
	public String getDescriptionFiltre(){
		StringBuilder sb = new StringBuilder();
		
		String sep = "";
		
		for (Filtre f : filtres.getFiltres()) {
			
			// Ajout du nom du filtre
			sb.append(sep).append(f.description).append("\n");
			
			sep = "\n";
			
			// Ajout des valeurs actives du filtre
			for(DefaultKeyValue kv :f.getKeyValues()) {
				sb.append(sep).append("\t").append(kv.getValue());
			}
			
			sb.append(sep);
		}
		
		return sb.toString();
	}
	
	public String getDescriptionTextuelle() {
		return getDescriptionTextuelle(true);
	}
	
	public String getDescriptionTextuelle(boolean majusculeInitiale) {
		String description = getDescriptionChaîne() + " " + getDescriptionPortéeFiltre() + "\n\n" + getDescriptionFiltre();
		if(majusculeInitiale)
		{
			return Character.toUpperCase(description.charAt(0)) + description.substring(1);
		}
		else {
			return description;
		}
	}

}
