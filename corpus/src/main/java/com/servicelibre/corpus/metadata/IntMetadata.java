/**
 * Code library for textual corpus management
 *
 * Copyright (C) 2011 Benoit Mercier <benoit.mercier@servicelibre.com> — Tous droits réservés.
 *
 * Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le
 * modifier suivant les termes de la “GNU General Public License” telle que
 * publiée par la Free Software Foundation : soit la version 3 de cette
 * licence, soit (à votre gré) toute version ultérieure.
 *
 * Ce programme est distribué dans l’espoir qu’il vous sera utile, mais SANS
 * AUCUNE GARANTIE : sans même la garantie implicite de COMMERCIALISABILITÉ
 * ni d’ADÉQUATION À UN OBJECTIF PARTICULIER. Consultez la Licence Générale
 * Publique GNU pour plus de détails.
 *
 * Vous devriez avoir reçu une copie de la Licence Générale Publique GNU avec
 * ce programme ; si ce n’est pas le cas, consultez :
 * <http://www.gnu.org/licenses/>.
 */

package com.servicelibre.corpus.metadata;

public class IntMetadata extends Metadata {

	int valeur;

	public IntMetadata(String nom) {
		this(nom, 0, false);
	}
	
	public IntMetadata(String nom, int valeur) {
		this(nom, valeur, false);
	}
	
	public IntMetadata(String nom, int valeur, boolean primaire) {
		this.nom = nom;
		this.valeur = valeur;
		this.primaire = primaire;
	}

	public int getValue() {
		return valeur;
	}

	public void setValue(int value) {
		this.valeur = value;
	}
	
	@Override
	public String toString() {
		return this.nom + "=" + valeur;
	}
	
	@Override
	public String getSimpleString() {
		return ""+valeur;
	}

}
