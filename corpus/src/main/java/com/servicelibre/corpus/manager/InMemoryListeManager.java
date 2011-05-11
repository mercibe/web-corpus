package com.servicelibre.corpus.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.servicelibre.corpus.liste.Lemme;
import com.servicelibre.corpus.liste.Liste;

public class InMemoryListeManager implements ListeManager {

	Map<String, Liste> listes = new HashMap<String, Liste>();
	private int maxLemmes;
	
	@Override
	public Map<String, Liste> getListes() {
		return listes;
	}

	@Override
	public List<Lemme> getListeLemmes(String listeId) {
		return listes.get(listeId).getLemmes();
	}

	@Override
	public void setMaxLemmes(int maxLemmes) {
		this.maxLemmes = maxLemmes;
	}

	@Override
	public int getMaxLemmes() {
		return maxLemmes;
	}

	@Override
	public void addListe(Liste liste) {
		listes.put(liste.getId(), liste);
		
	}

}
