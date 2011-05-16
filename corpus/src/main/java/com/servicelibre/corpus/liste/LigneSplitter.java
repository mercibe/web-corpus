package com.servicelibre.corpus.liste;

import java.util.List;

public interface LigneSplitter
{

    Mot splitLigne(String ligne, Liste liste);
    List<Mot> splitLigneMulti(String ligne, Liste liste);
    
}
