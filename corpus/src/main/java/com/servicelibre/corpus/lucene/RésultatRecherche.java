package com.servicelibre.corpus.lucene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.ScoreDoc;

public class RésultatRecherche
{

    public int spanCount;
    public ScoreDoc[] scoreDocs = new ScoreDoc[] {};
    public List<Contexte> contextes = new ArrayList<RésultatRecherche.Contexte>();
    //public Map<Integer, List<String[]>> documentContexts = new HashMap<Integer, List<String[]>>();
    public int nbTotalContextes;
	public int nbTotalDocs;
	
	
	public class Contexte{
		public int docId;
		public String[] partiesDeContexte;
		public Contexte(int docId, String[] partiesDeContexte) {
			super();
			this.docId = docId;
			this.partiesDeContexte = partiesDeContexte;
		}
		
	}

}
