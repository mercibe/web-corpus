package com.servicelibre.zk.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;

import com.servicelibre.corpus.entity.Liste;
import com.servicelibre.corpus.manager.Filtre;
import com.servicelibre.corpus.manager.FiltreMot;

/**
 * Cette classe fournit les filtres (Filtre), disponibles pour filtrer les mots
 * des différentes listes présentées dans l'application pour un corpus donné,
 * ainsi que les valeurs possibles pour chacun de ces filtres.
 * 
 * @author mercibe
 * 
 */
public class ListeFiltreManager extends FiltreManager {

	@Override
	public void init() {
		
		// ajout du filtre des listes
		List<Liste> listes = listeManager.findByCorpusId(corpusService.getCorpus().getId());
		
		List<DefaultKeyValue> listesClésValeurs = new ArrayList<DefaultKeyValue>(listes.size() + 1);
		
		listesClésValeurs.add(keyValueVide);
		
		for (Liste liste : listes) {
			listesClésValeurs.add(new DefaultKeyValue(liste.getId(), liste.getNom()));
		}
		
		filtres.add(new Filtre(FiltreMot.CléFiltre.liste.name(), "Liste", listesClésValeurs));
		

		// Ajout de la liste des catgram
		List<DefaultKeyValue> catgramClésValeurs = new ArrayList<DefaultKeyValue>(4);
		catgramClésValeurs.add(keyValueVide);
		catgramClésValeurs.add(new DefaultKeyValue("adj.", "adjectif"));
		catgramClésValeurs.add(new DefaultKeyValue("adv.", "adverbe"));
		catgramClésValeurs.add(new DefaultKeyValue("n.", "nom"));
		catgramClésValeurs.add(new DefaultKeyValue("v.", "verbe"));
		filtres.add(new Filtre(FiltreMot.CléFiltre.catgram.name(), "Classe de mot", catgramClésValeurs));

		// Ajout de la liste des genres
		List<DefaultKeyValue> genreClésValeurs = new ArrayList<DefaultKeyValue>(2);
		genreClésValeurs.add(keyValueVide);
		genreClésValeurs.add(new DefaultKeyValue("f.", "féminin"));
		genreClésValeurs.add(new DefaultKeyValue("m.", "masculin"));
		filtres.add(new Filtre(FiltreMot.CléFiltre.genre.name(), "Genre", genreClésValeurs));

		// Ajout de la liste des nombres
		List<DefaultKeyValue> nombreClésValeurs = new ArrayList<DefaultKeyValue>(3);
		nombreClésValeurs.add(keyValueVide);
		nombreClésValeurs.add(new DefaultKeyValue("inv.", "invariable"));
		nombreClésValeurs.add(new DefaultKeyValue("pl.", "pluriel"));
		filtres.add(new Filtre(FiltreMot.CléFiltre.nombre.name(), "Nombre", nombreClésValeurs));

	}

}
