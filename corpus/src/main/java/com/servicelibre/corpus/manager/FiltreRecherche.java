package com.servicelibre.corpus.manager;

import java.util.LinkedHashSet;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;

public class FiltreRecherche
{
	/**
	 * Cette classe permet de construire un filtre logique, une liste d'objet Filtre qui
	 * sera passée à la couche métier (JPA, Lucene) pour filtrer les query (filtreNom IN (...) AND filtreNom IN (...))
	 * 
	 * Les Filtre.nom doivent correspondre exactement aux noms des colonnes dans la DB.  Utiliser pour ce faire l'enum CléFiltre
	 * @author mercibe
	 *
	 */
    public enum CléFiltre
    {
        corpus, liste, catgram, genre, nombre, ro
    };
    
    LinkedHashSet<Filtre> filtres = new LinkedHashSet<Filtre>();

    public void addFiltre(Filtre filtre)
    {
    	// Si le filtre existe déjà, ajouter les éventuelles nouvelles valeurs
        if(!filtres.add(filtre)){
            for (Filtre f : filtres)
    		{
    			if(f.equals(filtre)) {
    				f.keyValues.addAll(filtre.keyValues);
    			}
    		}
        }
    }

    public Filtre getFiltre(String nom) {
    	Filtre filtre = null;
        // Recherche le filtre
        for (Filtre f : filtres)
        {
            if(f.nom.equals(nom)) {
            	filtre = f;
            }
        }
        return filtre;
    }
    
    public void removeFiltre(String nom, Object valeur) {
        
        // Recherche le filtre
        for (Filtre f : filtres)
        {
            if(f.nom.equals(nom)) {
                for(DefaultKeyValue cléVal : f.keyValues) {
                    if(cléVal.getKey().equals(valeur)) {
                        f.keyValues.remove(cléVal);
                        break;
                    }
                }
                // Si plus de valeurs pour ce filtre, supprimer le filtre également
                if(f.keyValues.size() == 0) {
                    filtres.remove(f);
                    break;
                }
            }
        }
    }
    
    public void removeAll() {
    	filtres.clear();
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("FiltreRecherche [\n");
        
        
        
        for (Filtre filtre : filtres)
        {
            sb.append(filtre.nom).append(": ");
            String séparateur = "";
            
            for(DefaultKeyValue cléVal : filtre.keyValues) {
                sb.append(séparateur).append(cléVal.getKey());
                séparateur = " OU ";
            }
            sb.append("\n");
        }
        sb.append("]\n");
        
        return sb.toString();
    }

	public LinkedHashSet<Filtre> getFiltres() {
		return filtres;
	}
    
	public DefaultKeyValue[] getFiltreGroupes() {
		
		DefaultKeyValue[] groupes = new DefaultKeyValue[filtres.size()];
		int index = 0;
        for (Filtre filtre : filtres)
		{
			groupes[index] = new DefaultKeyValue(filtre.nom, filtre.description);
			index++;
		}
		return groupes;
	}
	
	public Object[][] getFiltreValeurs() {
		
				
		Object[][] valeurs = new Object[filtres.size()][1];
		int index = 0;
		
        for (Filtre filtre : filtres)
		{
			//valeurs[index] contient un  DefaultKeyValue[]
			valeurs[index] = filtre.keyValues.toArray();
			index++;
		}
		
		return valeurs;
	}

	/**
	 * Détermine si la <i>valeur</i> du filtre <i>nom</i> est dans les filtres actifs 
	 * @param nom
	 * @param valeur
	 * @return
	 */
    public boolean isActif(String nom, Object valeur)
    {
        // Recherche le filtre
        for (Filtre f : filtres)
        {
            if(f.nom.equals(nom)) {
                for(DefaultKeyValue cléVal : f.keyValues) {
                	/* 
                	 * Il faut convertir en string car nous avons des booléen, long, string, etc.
                	 * Par contre, c'est important de conserver les objets car la couche JPA Criteria
                	 * en a besoin pour générer des requêtes correctes.
                	*/
                    if(cléVal.getKey().toString().equals(valeur.toString())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
}
