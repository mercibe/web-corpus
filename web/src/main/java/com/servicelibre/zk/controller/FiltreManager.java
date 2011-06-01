package com.servicelibre.zk.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.springframework.beans.factory.annotation.Autowired;

import com.servicelibre.corpus.manager.Filtre;
import com.servicelibre.corpus.manager.FiltreMot;
import com.servicelibre.corpus.manager.ListeManager;

/**
 * Cette classe fournit les filtres (Filtre), disponibles pour filtrer les mots des
 * différentes listes présentées dans l'application pour un corpus donné, ainsi que les valeurs
 * possibles pour chacun de ces filtres.
 * 
 * @author mercibe
 * 
 */
public class FiltreManager {

	@Autowired
	ListeManager listeManager;

	// @Autowired
	// MotManager motManager;

	List<Filtre> filtres = new ArrayList<Filtre>(3);

	private long corpusId;

	public FiltreManager() {
		super();
	}

	public FiltreManager(long corpusId) {
		super();
		this.corpusId = corpusId;
	}

	List<DefaultKeyValue> getFiltreNoms() {

		List<DefaultKeyValue> noms = new ArrayList<DefaultKeyValue>(filtres.size());
		for (Filtre filtre : filtres) {
			noms.add(new DefaultKeyValue(filtre.nom, filtre.description));
		}

		return noms;
	}

	Set<DefaultKeyValue> getFiltreValeurs(String nom) {
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
		// filtres.add(new Filtre(FiltreMot.CléFiltre.liste.name(), "Liste", listesClésValeurs));

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

	public long getCorpusId() {
		return corpusId;
	}

	public void setCorpusId(long corpusId) {
		this.corpusId = corpusId;
	}

}
