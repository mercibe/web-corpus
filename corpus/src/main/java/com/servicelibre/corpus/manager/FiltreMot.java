package com.servicelibre.corpus.manager;

import java.util.LinkedHashSet;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;

public class FiltreMot
{
	/**
	 * Cette classe permet de construire un filtre logique, une liste d'objet Filtre qui
	 * sera passée à la couche JPA pour filtrer les query (filtreNom IN (...) AND filtreNom IN (...))
	 * 
	 * Les Filtre.nom doivent correspondre exactement aux noms des colonnes dans la DB.  Utiliser pour ce faire l'enum CléFiltre
	 * @author mercibe
	 *
	 */
    public enum CléFiltre
    {
        corpus, liste, catgram, genre, nombre
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

    public void removeFiltre(String nom, String value) {
        
        System.err.println("Suppression de la valeur " + value + " du groupe " + nom);
        
        // Recherche le filtre
        for (Filtre f : filtres)
        {
            System.err.println("f.nom = " + f.nom);
            if(f.nom.equals(nom)) {
                System.err.println("Trouvé nom " + nom + " dans le filtre...");
                for(DefaultKeyValue cléVal : f.keyValues) {
                    System.err.println("Recherche de la valeur " + value + " dans " + cléVal);
                    if(cléVal.getKey().equals(value)) {
                        System.err.println("Trouvé cléVal: suppression!");
                        f.keyValues.remove(cléVal);
                        break;
                    }
                }
                if(f.keyValues.size() == 0) {
                    filtres.remove(f);
                    break;
                }
            }
        }
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("FiltreMot [\n");
        
        
        
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
			System.err.println("filtre.nom, filtre.description => " + filtre.nom +", "  +filtre.description);
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
    
}
