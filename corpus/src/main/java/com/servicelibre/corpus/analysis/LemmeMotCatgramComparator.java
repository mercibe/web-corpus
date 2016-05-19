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

package com.servicelibre.corpus.analysis;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Égalité sur base de l'equals du MotInfo (mot + lemme + catgram). Si égalité, tri alphabétique CA_fr sur lemme puis
 * mot
 * Pas du tout performant - réécrire à l'occasion (en se basant sur equals
 * 
 * @author benoitm
 * 
 * @param <T>
 */
public class LemmeMotCatgramComparator<T extends MotInfo> implements Comparator<MotInfo> {

	private Collator collator = Collator.getInstance(Locale.CANADA_FRENCH);

	
	@Override
	public int compare(MotInfo o1, MotInfo o2) {

		if(o1 == o2) {
			return 0;
		}
		
		if(o2 == null) {
			return 1;
		}
		
		if (o1.equals(o2)) {
			return 0;
		} else {
			// Trier par lemme / mot /catgram
			int comparaisonLemme = 0;
			if(o1.lemme != null && o2.lemme != null) {
				comparaisonLemme = collator.compare(o1.lemme, o2.lemme);
			}
			if (comparaisonLemme == 0) {
				int comparaisonMot = collator.compare(o1.mot, o2.mot);
				if (comparaisonMot == 0) {
					// comparer catgram
					if(o1.catgram == null) {
						if(o2.catgram != null) {
							return -1;
						}
					} else if (o1.catgram.equals(o2.catgram)) {
						return 0;
					}
					return 1;
				} else {
					return comparaisonMot;
				}
			} else {
				return comparaisonLemme;
			}
		}
	}

}
