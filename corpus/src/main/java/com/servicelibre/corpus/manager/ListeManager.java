package com.servicelibre.corpus.manager;

import java.util.List;
import java.util.Map;

import com.servicelibre.corpus.liste.Liste;
import com.servicelibre.corpus.liste.Mot;


public interface ListeManager
{

    Map<Integer, Liste> getListes();
    
    List<Mot> getMots(int listeId);

    Liste getListe(int listeId);
    
    Liste save(Liste liste);

    void setMaxMots(int maxMots);

    int getMaxMots();
}
