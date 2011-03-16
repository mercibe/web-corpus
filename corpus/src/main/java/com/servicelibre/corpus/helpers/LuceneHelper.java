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

package com.servicelibre.corpus.helpers;

import java.io.IOException;
import java.util.Locale;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.BooleanClause.Occur;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.servicelibre.corpus.CorpusFieldConstants;

public class LuceneHelper
{
    
    private static final Logger logger = LoggerFactory.getLogger(LuceneHelper.class);

    String idFieldName = "doc_id";
    
    
    public LuceneHelper()
    {
        super();
    }

    public LuceneHelper(String idFieldName)
    {
        this.idFieldName = idFieldName;
    }

    /**
     * 
     * @param searcher
     * @param query
     * @return
     */
    public ScoreDoc[] getScoreDocs(Searcher searcher, Query query)
    {
        return getTopDocs(searcher, query).scoreDocs;
    }
    public TopDocs getTopDocs(Searcher searcher, Query query, Filter queryFilter)
    {

        boolean docsScoredInOrder = true;

        TopScoreDocCollector collector = TopScoreDocCollector.create(60000, docsScoredInOrder);

        try
        {
                searcher.search(query, queryFilter, collector);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return collector.topDocs();
    }
    /**
     * 
     * @param searcher
     * @param query
     * @return
     */
    public TopDocs getTopDocs(Searcher searcher, Query query)
    {

        boolean docsScoredInOrder = true;

        TopScoreDocCollector collector = TopScoreDocCollector.create(60000, docsScoredInOrder);

        try
        {
                searcher.search(query, collector);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return collector.topDocs();
    }

    /**
     * Retourne le document pour le idFieldName donné
     * 
     * @param searcher
     * @param docId
     * @return
     */
    public Document getDoc(Searcher searcher, String docId)
    {
        BooleanQuery query = new BooleanQuery();
        query.add(new TermQuery(new Term(idFieldName, docId)), Occur.MUST);

        TopDocs docs;
        Document doc = null;
        try
        {
            docs = searcher.search(query, 1);

            if (docs.totalHits == 1)

            {
                int docID = docs.scoreDocs[0].doc;

                doc = searcher.doc(docID);

                logger.debug("Premier document trouvé avec le query [{}]  => {}", query, docID);
            }
            else {
                logger.warn("Le query {} ne retourne aucun résultat.", query);
            }

        }
        catch (IOException e)
        {
            logger.warn("Erreur lors de l'excécution du query {}. Erreur: {}", query, e);
        }

        return doc;
    }

    /**
     * 
     * @param searcher
     * @param query
     * @param sort
     * @return
     */
    public TopDocs getTopDocs(Searcher searcher, Query query, Sort sort)
    {

        if (sort == null)
        {
            return getTopDocs(searcher, query);
        }
        else
        {
            // Par défaut, calcul des scores des résultats, et du score le plus élevé
            return getTopDocs(searcher, query, sort, true, true);
        }
    }

    public TopDocs getTopDocs(Searcher searcher, Query query, Sort sort, boolean trackDocScores, boolean trackMaxScore)
    {
        // La valeur des champs doivent être retournées
        boolean fillFields = true;

        // Les document doivent être triés en fonction de leur docID si ex-aequo en score (inutile dans ce cas-ci il me semble...)
        boolean docsScoredInOrder = true;

        TopFieldCollector collector = null;

        try
        {
            collector = TopFieldCollector.create(sort, searcher.maxDoc(), fillFields, trackDocScores, trackMaxScore, docsScoredInOrder);
            searcher.search(query, collector);

            return collector.topDocs();
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }

        // Renvoie un TopDocs vide
        return new TopDocs(0, new ScoreDoc[] {}, 0.0f);
    }

    public ScoreDoc[] getAllDocs(Searcher searcher)
    {
        
        if(searcher == null)
        {
            return null;
        }

        // Trier les résultats sur base du champ Ordre et en respectant les règles de tri du français canadien
        // Le champ ordre doir remplir les quelques conditions suivantes:
        // - il ne doit contenir qu'un seul Term;
        // - la valeur de ce terme doit indiquer la position relative du document pour un tri donné
        // - le champ doit être indexé;
        // - le champ ne doit pas être tokenisé (NOT_ANALYZED);
        // - peut-être stocké        

        Sort sort = new Sort(new SortField(CorpusFieldConstants.ORDRE_FIELD, Locale.CANADA_FRENCH));

        // La valeur des champs doivent être retournées
        boolean fillFields = true;
        // Pas besoin de calculer les scores des résultats
        boolean trackDocScores = false;
        // Pas besoin de calculer le plus haut score
        boolean trackMaxScore = false;
        // Les document doivent être triés en fonction de leur docID si ex-aequo en score (inutile dans ce cas-ci il me semble...)
        boolean docsScoredInOrder = true;

        //Création d'un requête qui retroune tous les documents
        Query query = new MatchAllDocsQuery();

        TopFieldCollector collector = null;

        try
        {
            collector = TopFieldCollector.create(sort, searcher.maxDoc(), fillFields, trackDocScores, trackMaxScore, docsScoredInOrder);
            searcher.search(query, collector);
            return collector.topDocs().scoreDocs;
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }

        return new ScoreDoc[] {};
    }
    
    public String getIdFieldName()
    {
        return idFieldName;
    }

    public void setIdFieldName(String idFieldName)
    {
        this.idFieldName = idFieldName;
    }

    public ScoreDoc[] getScoreDocs(Searcher searcher, Query query, Filter queryFilter)
    {
        return getTopDocs(searcher, query, queryFilter).scoreDocs;
    }
}
