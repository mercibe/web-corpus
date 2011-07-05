package com.servicelibre.zk.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.zkoss.zul.SimpleGroupsModel;

import com.servicelibre.corpus.manager.Filtre;
import com.servicelibre.corpus.manager.ListeManager;
import com.servicelibre.corpus.service.CorpusService;

public abstract class FiltreManager {

	protected List<Filtre> filtres = new ArrayList<Filtre>(3);

	protected CorpusService corpusService;

	protected ListeManager listeManager;
	
	protected Set<DefaultKeyValue> exclusions = new HashSet<DefaultKeyValue>();

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

			values = supprimeFiltreActif(f.keyValues);

		}

		return values;
	}

	private Set<DefaultKeyValue> supprimeFiltreActif(Set<DefaultKeyValue> keyValues) {
		keyValues.removeAll(exclusions);
		return keyValues;
	}

	public CorpusService getCorpusService() {
		return corpusService;
	}

	public void setCorpusService(CorpusService corpusService) {
		this.corpusService = corpusService;
	}

	public ListeManager getListeManager() {
		return listeManager;
	}

	public void setListeManager(ListeManager listeManager) {
		this.listeManager = listeManager;
	}

	abstract public void init();

	public void exclus(DefaultKeyValue keyValue) {
		System.out.println("Exclusion de " + keyValue);
		this.exclusions.add(keyValue);
		
	}

	public void inclus(DefaultKeyValue keyValue) {
		boolean remove = this.exclusions.remove(keyValue);
		System.out.println("Inclusion de " + keyValue + " : " + remove);
	}

}
