package com.servicelibre.corpus.liste;

import java.util.ArrayList;
import java.util.List;

import com.servicelibre.corpus.entity.Liste;
import com.servicelibre.corpus.entity.Mot;


/**
 * 
 * mot\tlemme\tcatgram\tgenre\tnombre\tcatgram_precision\tro
 * 
 * ro=> true ou false
 * 
 * @author benoitm
 * 
 */
public class LigneSimpleSplitter implements LigneSplitter
{
    private static final String SÉPARATEUR = "\\t";

    @Override
    public List<Mot> splitLigne(String ligne, Liste liste)
    {
    	 List<Mot> mots = new ArrayList<Mot>(1);
    	 
        String[] cols = ligne.split(SÉPARATEUR);

        nettoie(cols);
        
        Mot mot = new Mot(cols[0], cols[1], cols[0].equals(cols[1]), cols[2], cols[3], cols[4], cols[5], Boolean.parseBoolean(cols[6]), "");
        liste.ajouteMot(mot);
        
		mots.add(mot);
        
        return mots;
    }

 
    private void nettoie(String[] cols)
    {
        for (int i = 0; i < cols.length; i++)
        {
            cols[i] = cols[i].trim();
        }
    }
    
    @Override
    public List<Mot> splitLigne(String ligne)
    {
        return splitLigne(ligne, new Liste());
    }
}
