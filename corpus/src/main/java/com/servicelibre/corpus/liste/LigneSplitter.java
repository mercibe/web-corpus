package com.servicelibre.corpus.liste;

import java.util.List;

import com.servicelibre.corpus.entity.Liste;
import com.servicelibre.corpus.entity.Mot;


public interface LigneSplitter
{

	//List<Mot> splitLigne(String ligne, Liste liste);
	
	List<Mot> splitLigne(String ligne);
    
}
