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

package com.servicelibre.corpus.analyzis;

public class Catgram {

	
	public String id;
	public String nom;
	public String description;
	public String étiquette;
	public boolean motGrammatical;
	
	public Catgram(String id, String nom, String étiquette, boolean motGrammatical) {
		this(id, nom, étiquette, motGrammatical, "");
	}

	public Catgram(String id, String nom, String étiquette, boolean motGrammatical, String description) {
		super();
		this.id = id;
		this.nom = nom;
		this.étiquette = étiquette;
		this.motGrammatical = motGrammatical;
		this.description = description;
	}

    @Override
    public String toString()
    {
        
        return id;
    }

	
	
}
