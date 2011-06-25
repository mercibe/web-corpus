package com.servicelibre.zk.controller;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;

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
		// List<Liste> listes = listeManager.findByCorpusId(corpusId);

		// List<DefaultKeyValue> listesClésValeurs = new
		// ArrayList<DefaultKeyValue>(listes.size());
		// for (Liste liste : listes)
		// {
		// listesClésValeurs.add(new DefaultKeyValue(liste.getId(),
		// liste.getNom()));
		// }
		// filtres.add(new Filtre(FiltreMot.CléFiltre.liste.name(), "Liste",
		// listesClésValeurs));

		// Ajout de la liste des catgram
		Set<DefaultKeyValue> catgramClésValeurs = new HashSet<DefaultKeyValue>(4);
		catgramClésValeurs.add(new DefaultKeyValue("adj.", "adjectif"));
		catgramClésValeurs.add(new DefaultKeyValue("n.", "nom"));
		catgramClésValeurs.add(new DefaultKeyValue("v.", "verbe"));
		catgramClésValeurs.add(new DefaultKeyValue("adv.", "adverbe"));
		filtres.add(new Filtre(FiltreMot.CléFiltre.catgram.name(), "Catégorie grammaticale", catgramClésValeurs));

		// Ajout de la liste des genres
		Set<DefaultKeyValue> genreClésValeurs = new HashSet<DefaultKeyValue>(2);
		genreClésValeurs.add(new DefaultKeyValue("m.", "masculin"));
		genreClésValeurs.add(new DefaultKeyValue("f.", "féminin"));
		filtres.add(new Filtre(FiltreMot.CléFiltre.genre.name(), "Genre", genreClésValeurs));

		// Ajout de la liste des nombres
		Set<DefaultKeyValue> nombreClésValeurs = new HashSet<DefaultKeyValue>(3);
		nombreClésValeurs.add(new DefaultKeyValue("s.", "singulier"));
		nombreClésValeurs.add(new DefaultKeyValue("inv.", "invariable"));
		nombreClésValeurs.add(new DefaultKeyValue("pl.", "pluriel"));
		filtres.add(new Filtre(FiltreMot.CléFiltre.nombre.name(), "Nombre", nombreClésValeurs));

	}

}
