package com.servicelibre.corpus.manager;

import java.util.List;
import java.util.Map;

import com.servicelibre.corpus.liste.Lemme;
import com.servicelibre.corpus.liste.Liste;

public interface ListeManager {

	Map<String,Liste> getListes();
	
	List<Lemme> getListeLemmes(String listeId);
	
	void addListe(Liste liste);
	
	
	void setMaxLemmes(int maxLemmes);
	int getMaxLemmes();
}
