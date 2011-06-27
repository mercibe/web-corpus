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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.bidimap.TreeBidiMap;

public abstract class CatgramsManager {
    
    // Catégories grammaticales pivot
    private static CatgramsPivot catgramsPivot = new CatgramsPivot();
    
    // Catégories grammaticales 
    public Map<String, Catgram> catgrams = new HashMap<String, Catgram>(60);
    public Map<String, Catgram> catgramsÉtiquette;
    
    // Correspondances Catgram / pivot
    public TreeBidiMap catgramBijection = new TreeBidiMap();
    
    // pivot
    public Map<String, Catgram> getCatgrams()
    {
        return new HashMap<String, Catgram>(catgrams);
    }

    //équivalent de l'ancien CatgramFranqus.valueOfLabel(String label)
    public Catgram getCatgramFromÉtiquette(String étiquette)
    {
        // Chargement d'une Map avec étiquettes comme clé (lazy loading) pour des raisons de performance
        if(catgramsÉtiquette == null) {

            catgramsÉtiquette = new HashMap<String, Catgram>(catgrams.size());
            
            for (Catgram catgram : catgrams.values())
            {
                catgramsÉtiquette.put(catgram.étiquette.toUpperCase(), catgram);
            }
        }
        
        return catgramsÉtiquette.get(étiquette.toUpperCase());
        
    }

    public Catgram getCatgramFromId(String id)
    {
        return catgrams.get(id.toUpperCase());
    }

    public Catgram convertFromPivot(String catgramPivotId)
    {
        String catgramId = (String) catgramBijection.getKey(catgramPivotId.toUpperCase());

        if(catgramId != null)
        {
            return this.getCatgramFromId(catgramId);
        }

        return null;
    }

    public Catgram convertToPivot(String catgramId)
    {
        String catgramPivotId = (String) catgramBijection.get(catgramId.toUpperCase());

        if(catgramPivotId != null)
        {
            return catgramsPivot.getCatgramFromId(catgramPivotId);
        }

        return null;
    }
	
}
