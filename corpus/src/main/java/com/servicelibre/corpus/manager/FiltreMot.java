package com.servicelibre.corpus.manager;

import java.util.HashMap;
import java.util.Map;

public class FiltreMot
{
    public enum cleFiltre
    {
        corpus, liste
    };
    
    Map<cleFiltre, String[]> filtres= new HashMap<FiltreMot.cleFiltre, String[]>();

    public void addFiltre(cleFiltre clé, String[] valeurs)
    {
        filtres.put(clé, valeurs);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("FiltreMot [\n");
        
        for (FiltreMot.cleFiltre clé : filtres.keySet())
        {
            sb.append(clé).append(": ");
            String séparateur = "";
            for(String val : filtres.get(clé)) {
                sb.append(séparateur).append(val);
                séparateur = " OU ";
            }
            sb.append("\n");
        }
        sb.append("]\n");
        
        return sb.toString();
    }
    
    
}
