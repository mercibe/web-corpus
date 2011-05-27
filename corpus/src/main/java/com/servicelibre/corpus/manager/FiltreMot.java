package com.servicelibre.corpus.manager;

import java.util.HashMap;
import java.util.Map;

public class FiltreMot
{
    public enum CléFiltre
    {
        corpus, liste, catgram, genre
    };
    
    Map<CléFiltre, Object[]> filtres= new HashMap<FiltreMot.CléFiltre, Object[]>();

    public void addFiltre(CléFiltre clé, Object[] valeurs)
    {
        filtres.put(clé, valeurs);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("FiltreMot [\n");
        
        for (FiltreMot.CléFiltre clé : filtres.keySet())
        {
            sb.append(clé).append(": ");
            String séparateur = "";
            for(Object val : filtres.get(clé)) {
                sb.append(séparateur).append(val);
                séparateur = " OU ";
            }
            sb.append("\n");
        }
        sb.append("]\n");
        
        return sb.toString();
    }

	public Map<CléFiltre, Object[]> getFiltres() {
		return filtres;
	}
    
    
}
