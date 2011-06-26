package com.servicelibre.zk.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;

import com.servicelibre.corpus.manager.Filtre;

public abstract class FiltreManager {

	protected List<Filtre> filtres = new ArrayList<Filtre>(3);


	public FiltreManager() {
		super();
	}


	public List<DefaultKeyValue> getFiltreNoms() {

		List<DefaultKeyValue> noms = new ArrayList<DefaultKeyValue>(filtres.size());
		for (Filtre filtre : filtres) {
			noms.add(new DefaultKeyValue(filtre.nom, filtre.description));
		}

		return noms;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.servicelibre.zk.controller.FiltreManager#getFiltreValeurs(java.lang
	 * .String)
	 */
	public Set<DefaultKeyValue> getFiltreValeurs(String nom) {
		Set<DefaultKeyValue> values = new HashSet<DefaultKeyValue>();

		// recherche du filtre
		Filtre f = null;
		for (Filtre filtre : filtres) {
			if (filtre.nom.equalsIgnoreCase(nom)) {
				f = filtre;
				break;
			}
		}

		if (f != null) {
			values = f.keyValues;
		}

		return values;
	}

	abstract public void init();


}
