package com.servicelibre.zk.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.keyvalue.DefaultKeyValue;

import com.servicelibre.corpus.manager.Filtre;
import com.servicelibre.corpus.manager.FiltreRecherche;
import com.servicelibre.corpus.service.CorpusService;
import com.servicelibre.entities.corpus.CatégorieListe;
import com.servicelibre.entities.corpus.Liste;
import com.servicelibre.repositories.corpus.ListeRepository;

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

		ajoutFiltresStatiques();

		ajoutFiltreDynamiques();
		
		// Tri alphabétique des filtres
		// FIXME L'attribut « ordre » n'est donc plus d'aucune utilité.  Rendre paramétrisable ou séparer filtres dynamiques et filtres statiques
		Collections.sort(filtres);

	}

	private void ajoutFiltreDynamiques() {
		
		ajoutFiltreClasseDeMot();

		ajoutFiltreGenre();

		ajoutFiltreNombre();

		ajoutFiltreRO();
	}

	private void ajoutFiltresStatiques() {
		// Création des filtres pour toutes les catégories de liste
		// Récupération des catégories de liste
		for (CatégorieListe catégorie : corpusService.getCatégorieListes()) {
			if (isRôleAdmin() || catégorie.getPublique()) {
				// Récupération de l'ensembles des listes de la catégorie donnée
				List<Liste> listes = corpusService.getCatégorieListeListes(catégorie);
				List<DefaultKeyValue> listesClésValeurs = new ArrayList<DefaultKeyValue>(listes.size() + 1);
				for (Liste liste : listes) {
					if (isRôleAdmin() || liste.getPublique()) {
						listesClésValeurs.add(new DefaultKeyValue(liste.getId(), liste.getNom()));
					}
				}
				filtres.add(new Filtre(FiltreRecherche.CléFiltre.liste.name() + "_" + catégorie.getNom(), catégorie.getNom(), listesClésValeurs,"",""));
			}
		}
	}

	private void ajoutFiltreRO() {
		// Ajout de la liste RO
		List<DefaultKeyValue> roClésValeurs = new ArrayList<DefaultKeyValue>(3);
		// roClésValeurs.add(keyValueVide);
		// FIXME
		roClésValeurs.add(new DefaultKeyValue(Boolean.TRUE, "Nouvelles graphies"));
		//roClésValeurs.add(new DefaultKeyValue(Boolean.FALSE, "Graphies traditionnelles"));
		filtres.add(new Filtre(FiltreRecherche.CléFiltre.ro.name(), "Orthographe rectifiée", roClésValeurs,"",""));
	}

	private void ajoutFiltreNombre() {
		// Ajout de la liste des nombres
		List<DefaultKeyValue> nombreClésValeurs = new ArrayList<DefaultKeyValue>(3);
		// nombreClésValeurs.add(keyValueVide);
		nombreClésValeurs.add(new DefaultKeyValue("s.", "Singulier"));
		nombreClésValeurs.add(new DefaultKeyValue("pl.", "Pluriel"));
		nombreClésValeurs.add(new DefaultKeyValue("s. et pl.", "Singulier et pluriel"));
		nombreClésValeurs.add(new DefaultKeyValue("inv.", "Invariable"));
		filtres.add(new Filtre(FiltreRecherche.CléFiltre.nombre.name(), "Nombre", nombreClésValeurs,"",""));
	}

	private void ajoutFiltreGenre() {
		// Ajout de la liste des genres
		List<DefaultKeyValue> genreClésValeurs = new ArrayList<DefaultKeyValue>(2);
		// genreClésValeurs.add(keyValueVide);
		genreClésValeurs.add(new DefaultKeyValue("f.", "Féminin"));
		genreClésValeurs.add(new DefaultKeyValue("m.", "Masculin"));
		genreClésValeurs.add(new DefaultKeyValue("m. ou f.", "Masculin ou féminin"));
		filtres.add(new Filtre(FiltreRecherche.CléFiltre.genre.name(), "Genre", genreClésValeurs,"",""));
	}

	private void ajoutFiltreClasseDeMot() {
		// Ajout de la liste des catgram
		// TODO select distinct catgram from mot; et rechercher description dans
		// service catgram
		List<DefaultKeyValue> catgramClésValeurs = new ArrayList<DefaultKeyValue>(4);
		// catgramClésValeurs.add(keyValueVide);
		catgramClésValeurs.add(new DefaultKeyValue("adj.", "adjectif"));
		catgramClésValeurs.add(new DefaultKeyValue("adv.", "adverbe"));
		catgramClésValeurs.add(new DefaultKeyValue("conj.", "conjonction"));
		catgramClésValeurs.add(new DefaultKeyValue("dét.", "déterminant"));
		// catgramClésValeurs.add(new DefaultKeyValue("interj.", "interjection"));
		catgramClésValeurs.add(new DefaultKeyValue("n.", "nom"));
		catgramClésValeurs.add(new DefaultKeyValue("prép.", "préposition"));
		catgramClésValeurs.add(new DefaultKeyValue("pron.", "pronom"));
		catgramClésValeurs.add(new DefaultKeyValue("v.", "verbe"));
		filtres.add(new Filtre(FiltreRecherche.CléFiltre.catgram.name(), "Classe de mot", catgramClésValeurs,"",""));
		
		
	}

	@Override
	public CorpusService getCorpusService() {
		return super.getCorpusService();
	}

	@Override
	public ListeRepository getListeRepository() {
		return super.getListeRepository();
	}

	@Override
	public void setCorpusService(CorpusService corpusService) {
		super.setCorpusService(corpusService);
	}

	@Override
	public void setListeRepository(ListeRepository listeRepo) {
		super.setListeRepository(listeRepo);
	}
	
	

}
