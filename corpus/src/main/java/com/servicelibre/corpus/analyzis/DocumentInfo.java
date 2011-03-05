package com.servicelibre.corpus.analyzis;

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
    protected String docId;
    
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
