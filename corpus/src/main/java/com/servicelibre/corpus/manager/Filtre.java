package com.servicelibre.corpus.manager;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;

/**
 * Un filtre = un nom, une description et liste de valeurs(clé/valeur)
 * 
 * @author mercibe
 * 
 */
public class Filtre {

	public String nom;
	public String description;
	public Set<DefaultKeyValue> keyValues;

	public Filtre(String nom, String description, Set<DefaultKeyValue> keyValues) {
		super();
		this.nom = nom;
		this.description = description;
		this.keyValues = keyValues;
	}
	
	public Filtre(String nom, String description, Object[] values) {
		super();
		this.nom = nom;
		this.description = description;
		
		Set<DefaultKeyValue> keyValues = new HashSet<DefaultKeyValue>(values.length);
		for (int i = 0; i < values.length; i++) {
			keyValues.add(new DefaultKeyValue(values[i], values[i]));
		}
		
		this.keyValues = keyValues;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Filtre other = (Filtre) obj;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Filtre [nom=" + nom + ", description=" + description + ", keyValues=" + keyValues + "]";
	}
	
	
	
}