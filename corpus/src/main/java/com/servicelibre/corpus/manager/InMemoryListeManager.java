package com.servicelibre.corpus.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.servicelibre.corpus.entity.Mot;
import com.servicelibre.corpus.liste.Liste;


public class InMemoryListeManager  {

	Map<Long, Liste> listes = new HashMap<Long, Liste>();
	private int maxLemmes;
	
	public Map<Long, Liste> getListes() {
		return listes;
	}

	public List<Mot> getMots(int listeId) {
		return listes.get(listeId).getMots();
	}

	public void setMaxMots(int maxLemmes) {
		this.maxLemmes = maxLemmes;
	}

	public int getMaxMots() {
		return maxLemmes;
	}

	public Liste save(Liste liste) {
		listes.put(liste.getId(), liste);
		return liste;
		
	}

    public Liste getListe(int listeId)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
