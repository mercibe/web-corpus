package com.servicelibre.corpus.lucene;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.ScoreDoc;

public class RésultatRecherche
{

    public int spanCount;
    public ScoreDoc[] scoreDocs = new ScoreDoc[] {};
    public Map<Integer, List<String[]>> documentContexts = new HashMap<Integer, List<String[]>>();

}
