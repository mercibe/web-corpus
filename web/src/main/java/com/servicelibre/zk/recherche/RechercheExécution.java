package com.servicelibre.zk.recherche;

import java.util.Date;

/**
 * Conteneur d'information sur le résultat de l'exécution d'une recherche dans l'interface Web.
 * 
 * @author mercibe
 * 
 */
public class RechercheExécution {

	public Recherche recherche;
	public Date dateExécution;
	public int nbRésultats;

	public RechercheExécution() {
		super();
	}

	public RechercheExécution(Recherche recherche, Date dateExécution, int nbRésultats) {
		super();
		this.recherche = recherche;
		this.dateExécution = dateExécution;
		this.nbRésultats = nbRésultats;
	}

}
