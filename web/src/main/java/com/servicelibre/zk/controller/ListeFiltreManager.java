package com.servicelibre.zk.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;

import com.servicelibre.corpus.manager.Filtre;
import com.servicelibre.corpus.manager.FiltreRecherche;
import com.servicelibre.entities.corpus.CatégorieListe;
import com.servicelibre.entities.corpus.Liste;

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

	// Création des filtres pour toutes les catégories de liste
	// Récupération des catégories de liste
	for (CatégorieListe catégorie : corpusService.getCatégorieListes()) {
	    // Récupération de l'ensembles des listes de la catégorie donnée
	    List<Liste> listes = corpusService.getCatégorieListeListes(catégorie);
	    List<DefaultKeyValue> listesClésValeurs = new ArrayList<DefaultKeyValue>(listes.size() + 1);
	    for(Liste liste : listes)
	    {
		listesClésValeurs.add(new DefaultKeyValue(liste.getId(), liste.getNom()));
	    }
	    filtres.add(new Filtre(FiltreRecherche.CléFiltre.liste.name() + "_" + catégorie.getNom(), catégorie.getNom(), listesClésValeurs));
	}

//	

	// Ajout de la liste des catgram
	// TODO select distinct catgram from mot; et rechercher description dans
	// service catgram
	List<DefaultKeyValue> catgramClésValeurs = new ArrayList<DefaultKeyValue>(4);
	// catgramClésValeurs.add(keyValueVide);
	catgramClésValeurs.add(new DefaultKeyValue("adj.", "adjectif"));
	catgramClésValeurs.add(new DefaultKeyValue("adv.", "adverbe"));
	catgramClésValeurs.add(new DefaultKeyValue("conj.", "conjonction"));
	catgramClésValeurs.add(new DefaultKeyValue("dét.", "déterminant"));
	catgramClésValeurs.add(new DefaultKeyValue("interj.", "interjection"));
	catgramClésValeurs.add(new DefaultKeyValue("n.", "nom"));
	catgramClésValeurs.add(new DefaultKeyValue("prép.", "préposition"));
	catgramClésValeurs.add(new DefaultKeyValue("pron.", "pronom"));
	catgramClésValeurs.add(new DefaultKeyValue("v.", "verbe"));
	filtres.add(new Filtre(FiltreRecherche.CléFiltre.catgram.name(), "Classe de mot", catgramClésValeurs));

	// Ajout de la liste des genres
	List<DefaultKeyValue> genreClésValeurs = new ArrayList<DefaultKeyValue>(2);
	// genreClésValeurs.add(keyValueVide);
	genreClésValeurs.add(new DefaultKeyValue("f.", "féminin"));
	genreClésValeurs.add(new DefaultKeyValue("m.", "masculin"));
	filtres.add(new Filtre(FiltreRecherche.CléFiltre.genre.name(), "Genre", genreClésValeurs));

	// Ajout de la liste des nombres
	List<DefaultKeyValue> nombreClésValeurs = new ArrayList<DefaultKeyValue>(3);
	// nombreClésValeurs.add(keyValueVide);
	nombreClésValeurs.add(new DefaultKeyValue("inv.", "invariable"));
	nombreClésValeurs.add(new DefaultKeyValue("pl.", "pluriel"));
	filtres.add(new Filtre(FiltreRecherche.CléFiltre.nombre.name(), "Nombre", nombreClésValeurs));

	// Ajout de la liste RO
	List<DefaultKeyValue> roClésValeurs = new ArrayList<DefaultKeyValue>(3);
	// roClésValeurs.add(keyValueVide);
	roClésValeurs.add(new DefaultKeyValue(Boolean.TRUE, "graphies rectifiées"));
	// roClésValeurs.add(new DefaultKeyValue(Boolean.FALSE,
	// "graphies traditionnelles"));
	filtres.add(new Filtre(FiltreRecherche.CléFiltre.ro.name(), "Orthographe", roClésValeurs));

    }

}
