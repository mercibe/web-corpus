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
 * Contenueur d'information sur un document d'un corpus
 * - métadonnées 
 * - information sur les mots contenus dans le document (fréquence, etc.)
 * @author benoitm
 *
 */
public class DocumentInfo
{
    public String docId;
    
    protected List<Metadata> metadatas = new ArrayList<Metadata>();
    
    protected List<MotInfo> mots = new ArrayList<MotInfo>(25000);

    public DocumentInfo(String docId)
    {
        super();
        this.docId = docId;
    }

    public List<Metadata> getMetadatas()
    {
        return metadatas;
    }

    public void setMetadatas(List<Metadata> metadatas)
    {
        this.metadatas = metadatas;
    }

    public List<MotInfo> getMots()
    {
        return mots;
    }

    public void setMots(List<MotInfo> mots)
    {
        this.mots = mots;
    }
    
    

}
