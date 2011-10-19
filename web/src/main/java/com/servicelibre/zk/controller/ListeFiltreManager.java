package com.servicelibre.zk.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;

import com.servicelibre.corpus.entity.Liste;
import com.servicelibre.corpus.manager.Filtre;
import com.servicelibre.corpus.manager.FiltreRecherche;

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
		List<Liste> listesPrimaires = listeManager.findPrimaireByCorpusId(corpusService.getCorpus().getId());
		
		List<DefaultKeyValue> listesClésValeurs = new ArrayList<DefaultKeyValue>(listesPrimaires.size() + 1);
		
		//listesClésValeurs.add(keyValueVide);
		
		for (Liste liste : listesPrimaires) {
			listesClésValeurs.add(new DefaultKeyValue(liste.getId(), liste.getNom()));
		}
		
		filtres.add(new Filtre(FiltreRecherche.CléFiltre.liste.name(), "Liste", listesClésValeurs));
		
		// TODO gérer des catégories de listes et faire un filtre par catégorie! => bien plus générique
		
		// ajout du filtre des listes thématiques
		List<Liste> listesThématiques = listeManager.findThématiquesByCorpusId(corpusService.getCorpus().getId());
		
		List<DefaultKeyValue> listesThématiquesClésValeurs = new ArrayList<DefaultKeyValue>(listesThématiques.size() + 1);
		
		for (Liste liste : listesThématiques) {
			listesThématiquesClésValeurs.add(new DefaultKeyValue(liste.getId(), liste.getNom()));
		}
		
		filtres.add(new Filtre(FiltreRecherche.CléFiltre.liste.name() + "_thématiques", "Thèmes", listesThématiquesClésValeurs));		
		
		
		// ajout du filtre des listes de particularités
		List<Liste> listesParticularités = listeManager.findParticularitésByCorpusId(corpusService.getCorpus().getId());
		
		List<DefaultKeyValue> listesSecondairesClésValeurs = new ArrayList<DefaultKeyValue>(listesParticularités.size() + 1);
		
		for (Liste liste : listesParticularités) {
			listesSecondairesClésValeurs.add(new DefaultKeyValue(liste.getId(), liste.getNom()));
		}
		
		filtres.add(new Filtre(FiltreRecherche.CléFiltre.liste.name() + "_particularités", "Particularités", listesSecondairesClésValeurs));		
		
		

		// Ajout de la liste des catgram
		// TODO select distinct catgram from mot; et rechercher description dans service catgram
		List<DefaultKeyValue> catgramClésValeurs = new ArrayList<DefaultKeyValue>(4);
		//catgramClésValeurs.add(keyValueVide);
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
		//genreClésValeurs.add(keyValueVide);
		genreClésValeurs.add(new DefaultKeyValue("f.", "féminin"));
		genreClésValeurs.add(new DefaultKeyValue("m.", "masculin"));
		filtres.add(new Filtre(FiltreRecherche.CléFiltre.genre.name(), "Genre", genreClésValeurs));

		// Ajout de la liste des nombres
		List<DefaultKeyValue> nombreClésValeurs = new ArrayList<DefaultKeyValue>(3);
		//nombreClésValeurs.add(keyValueVide);
		nombreClésValeurs.add(new DefaultKeyValue("inv.", "invariable"));
		nombreClésValeurs.add(new DefaultKeyValue("pl.", "pluriel"));
		filtres.add(new Filtre(FiltreRecherche.CléFiltre.nombre.name(), "Nombre", nombreClésValeurs));

		// Ajout de la liste RO
		List<DefaultKeyValue> roClésValeurs = new ArrayList<DefaultKeyValue>(3);
		//roClésValeurs.add(keyValueVide);
		roClésValeurs.add(new DefaultKeyValue(Boolean.TRUE, "graphies rectifiées"));
//		roClésValeurs.add(new DefaultKeyValue(Boolean.FALSE, "graphies traditionnelles"));
		filtres.add(new Filtre(FiltreRecherche.CléFiltre.ro.name(), "Orthographe", roClésValeurs));
		
	}

}
