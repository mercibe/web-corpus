package com.servicelibre.corpus.liste;

import java.util.List;

import com.servicelibre.entities.corpus.Liste;
import com.servicelibre.entities.corpus.Mot;


public interface LigneSplitter
{

	//List<Mot> splitLigne(String ligne, Liste liste);
	
	List<Mot> splitLigne(String ligne);
    
}
