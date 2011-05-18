package com.servicelibre.corpus.liste;

import java.util.List;

public interface LigneSplitter
{

	List<Mot> splitLigne(String ligne, Liste liste);
    
}
