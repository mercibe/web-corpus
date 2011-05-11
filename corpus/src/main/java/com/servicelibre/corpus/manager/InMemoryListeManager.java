package com.servicelibre.corpus.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.servicelibre.corpus.liste.Liste;
import com.servicelibre.corpus.liste.Mot;

public class InMemoryListeManager implements ListeManager {

	Map<Integer, Liste> listes = new HashMap<Integer, Liste>();
	private int maxLemmes;
	
	@Override
	public Map<Integer, Liste> getListes() {
		return listes;
	}

	@Override
	public List<Mot> getMots(int listeId) {
		return listes.get(listeId).getMots();
	}

	@Override
	public void setMaxMots(int maxLemmes) {
		this.maxLemmes = maxLemmes;
	}

	@Override
	public int getMaxMots() {
		return maxLemmes;
	}

	@Override
	public Liste save(Liste liste) {
		listes.put(liste.getId(), liste);
		return liste;
		
	}

    @Override
    public Liste getListe(int listeId)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
