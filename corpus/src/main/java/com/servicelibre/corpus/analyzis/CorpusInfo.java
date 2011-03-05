package com.servicelibre.corpus.analyzis;

import java.util.ArrayList;
import java.util.List;

import com.servicelibre.corpus.metadata.Metadata;

/**
 * Contenueur de métainformation sur un corpus textual:
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
    
    protected List<MotInfo> mots = new ArrayList<MotInfo>(25000);
    protected List<MotInfo> lemmes = new ArrayList<MotInfo>(20000);
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

    public List<MotInfo> getMots()
    {
        return mots;
    }

    public void setMots(List<MotInfo> mots)
    {
        this.mots = mots;
    }

    public List<MotInfo> getLemmes()
    {
        return lemmes;
    }

    public void setLemmes(List<MotInfo> lemmes)
    {
        this.lemmes = lemmes;
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
