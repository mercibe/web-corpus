package com.servicelibre.corpus.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;

/**
 * Un filtre = un nom, une description et liste de valeurs(clé/valeur)
 * 
 * @author mercibe
 * 
 */
public class Filtre implements Comparable<Filtre> {

	public String nom;
	public String description;
	public List<DefaultKeyValue> keyValues;
	public String nomRôle;
	public String remarqueValeurs;

	public Filtre(String nom, String description, List<DefaultKeyValue> keyValues, String nomRôle, String remarqueValeurs) {
		super();
		this.nom = nom;
		this.description = description;
		this.keyValues = keyValues;
		this.nomRôle = nomRôle;
		this.remarqueValeurs = remarqueValeurs;
	}

	public Filtre(String nom, String description, Object[] values, String nomRôle, String remarqueValeurs) {
		super();
		this.nom = nom;
		this.description = description;
		this.nomRôle = nomRôle;
		this.remarqueValeurs = remarqueValeurs;

		List<DefaultKeyValue> keyValues = new ArrayList<DefaultKeyValue>(values.length);
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
		return "Filtre [nom=" + nom + ", description=" + description + ", keyValues=" + keyValues + ", nomRôle=" + nomRôle + "]";
	}

	public List<DefaultKeyValue> getKeyValues() {
		return keyValues;
	}

	public void setKeyValues(List<DefaultKeyValue> keyValues) {
		this.keyValues = keyValues;
	}

	public Filtre getCopie() {
		List<DefaultKeyValue> copieKv = new ArrayList<DefaultKeyValue>(this.keyValues.size());
		for (DefaultKeyValue kv : this.keyValues) {
			copieKv.add(kv);
		}

		Filtre copieFiltre = new Filtre(this.nom, this.description, copieKv, this.nomRôle, this.remarqueValeurs);
		return copieFiltre;
	}

	@Override
	public int compareTo(Filtre autreFiltre) {
		if (autreFiltre != null) {
			return this.description.compareTo(autreFiltre.description);
		}
		return 0;
	}

}