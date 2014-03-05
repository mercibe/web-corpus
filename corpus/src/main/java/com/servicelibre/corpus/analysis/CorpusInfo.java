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

import java.util.ArrayList;
import java.util.List;

import com.servicelibre.corpus.metadata.Metadata;

/**
 * Conteneur de métainformation sur un corpus textuel:
 * - métadonnées du corpus
 * - liste des documents qui composent le corpus
 * - résultats d'une analyse lexicale: information sur les mots et lemmes (fréquences, etc.)
 * 
 * @author benoitm
 *
 */
public class CorpusInfo
{

    protected String corpusId;
    
    protected List<Metadata> metadatas = new ArrayList<Metadata>();

    protected List<DocumentInfo> documents = new ArrayList<DocumentInfo>();
    
    protected List<MotInfo> motsLemmes = new ArrayList<MotInfo>(25000);
    
    protected List<MotInfo> absents = new ArrayList<MotInfo>(20000);
    

    public CorpusInfo(String corpusId)
    {
        super();
        this.corpusId = corpusId;
    }

    public List<Metadata>  getMetadatas()
    {
        return metadatas;
    }

    public void setMetadatas(List<Metadata>  metadatas)
    {
        this.metadatas = metadatas;
    }

    public List<DocumentInfo> getDocuments()
    {
        return documents;
    }

    public void setDocuments(List<DocumentInfo> documents)
    {
        this.documents = documents;
    }


    public List<MotInfo> getMotsLemmes() {
		return motsLemmes;
	}

	public void setMotsLemmes(List<MotInfo> motsLemmes) {
		this.motsLemmes = motsLemmes;
	}

	public List<MotInfo> getAbsents()
    {
        return absents;
    }

    public void setAbsents(List<MotInfo> absents)
    {
        this.absents = absents;
    }
    
    
}
