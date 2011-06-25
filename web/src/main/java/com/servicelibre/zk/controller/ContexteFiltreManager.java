package com.servicelibre.zk.controller;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;

import com.servicelibre.corpus.manager.Filtre;

public class ContexteFiltreManager extends FiltreManager {

	@Override
	public void init() {
		
		// MOCK => utiliser un service propre à un corpus donné
		// Ajout de la liste des catégories d'ouvrage
		
		Set<DefaultKeyValue> catégorieClésValeurs = new HashSet<DefaultKeyValue>(4);
		catégorieClésValeurs.add(new DefaultKeyValue("album", "Album (64)"));
		catégorieClésValeurs.add(new DefaultKeyValue("chanson", "Chansons (7)"));
		catégorieClésValeurs.add(new DefaultKeyValue("premier_roman", "Premier roman (21)"));
		
		// param1 => vient du service
		filtres.add(new Filtre("catégorie", "Catégorie d'ouvrage", catégorieClésValeurs));


	}

}
