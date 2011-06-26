/**
 * Librairie de code pour la création d'index textuels
 *
 * Copyright (C) 2009 Benoit Mercier <benoit.mercier@servicelibre.com> — Tous droits réservés.
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

package com.servicelibre.corpus.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.MapFieldSelector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.PositionBasedTermVectorMapper;
import org.apache.lucene.index.PositionBasedTermVectorMapper.TVPositionInfo;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermVectorOffsetInfo;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.servicelibre.corpus.helpers.LuceneHelper;

/**
 * Interface de gestion et d'interrogation d'un index Lucene
 * 
 * @author mercibe
 * 
 */
public class LuceneIndexManager

{
    private static final String DEFAULT_SEARCH_FIELD = "txt";

    private static final Logger logger = LoggerFactory.getLogger(LuceneIndexManager.class);

    private IndexReader reader;

    private Searcher searcher;
    private LuceneHelper lh = new LuceneHelper();
    private QueryParser queryParser;

    private Analyzer analyseur;

    private FSDirectory dossierIndex;

    public LuceneIndexManager(FSDirectory dossierIndex, Analyzer analyseur)
    {
        super();
        this.analyseur = analyseur;
        setDossierIndex(dossierIndex);
    }

    public FSDirectory getDossierIndex()
    {
        return dossierIndex;
    }

    public void setDossierIndex(FSDirectory dossierIndex)
    {
        this.dossierIndex = dossierIndex;

        try
        {
            reader = IndexReader.open(dossierIndex, true);
        }
        catch (CorruptIndexException e)
        {
            logger.error("Erreur lors de la récupération d,un reader sur l'index Lucene", e);
        }
        catch (IOException e)
        {
            logger.error("Erreur lors de la récupération d,un reader sur l'index Lucene", e);

        }
        searcher = new IndexSearcher(reader);

        // FIXME txt devrait venir via config (Spring ou configurator?)
        queryParser = new QueryParser(Version.LUCENE_30, DEFAULT_SEARCH_FIELD, analyseur);
    }

    public Document getDocument(String docId)
    {
        return lh.getDoc(searcher, docId);
    }

    public ScoreDoc[] getDocuments(String query)
    {
        return getDocuments(query, null);
    }

    public ScoreDoc[] getDocuments(String query, BooleanQuery optionsBooleanQuery)
    {
        return getDocuments(query, optionsBooleanQuery, 0);
    }

    /**
     * Construction et exécution d'un BooleanQuery strict dont tous les termes
     * sont Occur.MUST
     * 
     * @param query
     * @param booleanQuery
     * @return
     */
    public ScoreDoc[] getDocuments(String query, BooleanQuery booleanQuery, int slop)
    {

        ScoreDoc scoreDocs[] = new ScoreDoc[] {};

        BooleanQuery bQuery = new BooleanQuery();
        if (query != null && query.trim().length() > 0)
        {

            // PhraseQuery pq = new PhraseQuery();
            // pq.setSlop(0);
            // query.add(new Term("subject","job"));
            // query.add(new Term("subject","opening"));
            // query.add(new Term("subject","j2ee"));
            // si query entre "" => phrase query / split sur le blanc

            if (slop > 0)
            {
                // TODO quid MultiPhraseQuery?
                PhraseQuery pq = new PhraseQuery();
                pq.setSlop(slop);
                // supprimer les guillemets
                query = query.replaceAll("\"", "");

                // Split sur blanc et construction du phrase query
                String[] terms = query.split(" ");
                for (String term : terms)
                {
                    pq.add(new Term(DEFAULT_SEARCH_FIELD, term));
                }
                bQuery.add(pq, Occur.MUST);
            }
            else
            {
                try
                {
                    Query parsedQuery = queryParser.parse(query);
                    // FIXME
                    // SpanTermQuery spanQ = new SpanTermQuery(null);
                    bQuery.add(parsedQuery, Occur.MUST);
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }
            }

        }

        logger.debug("booleanQuery.getClauses().length = {}", booleanQuery.getClauses().length);
        if (booleanQuery != null && booleanQuery.getClauses().length > 0)
        {
            bQuery.add(booleanQuery, Occur.MUST);
        }

        logger.debug("Exécution du Query Lucene {}", bQuery);

        scoreDocs = lh.getScoreDocs(searcher, bQuery);

        // Query parsedQuery = null;
        // try
        // {
        // parsedQuery = queryParser.parse(query);
        // logger.debug("Exécution du Query Lucene {}", parsedQuery);
        // scoreDocs = lh.getScoreDocs(searcher, parsedQuery);
        // }
        // catch (ParseException e)
        // {
        // e.printStackTrace();
        // }

        return scoreDocs;
    }

    /**
     * 
     * @param query
     * @param booleanQuery
     * @param slop
     * @param tailleVoisinage
     * @return
     */
    public RésultatRecherche getDocumentsWithContexts(String query, int slop, int tailleVoisinage)
    {

        RésultatRecherche résultat = new RésultatRecherche();

        // TODO idéalement le query devrait être parsé par un Query Parser
        // «amélioré» (SpanNearQuery à la place d'un PhraseQuery
        // il serait ensuite filtré par l'éventuel BooleanQuery

        // Si pas de blanc => SpanTermQuery
        // Si un blanc pas entre "" dans le query => SpanNearQuery
        // Si un * => wildcard Query - Compatible avec Span via SpanMultiTermQueryWrapper

        logger.debug("Query string = {}", query);
        SpanNearQuery spanNearQuery = getSpanNearQuery(query, slop);
        logger.debug("Query Span {}", spanNearQuery);

        return executeSpanQuery(tailleVoisinage, résultat, spanNearQuery);
    }

    public RésultatRecherche getDocumentsWithContexts(List<String> formes, int tailleVoisinage)
    {
        RésultatRecherche résultat = new RésultatRecherche();
        
        
        logger.debug("Query string = {}", queryParser.toString());
        SpanOrQuery spanOrQuery = getSpanOrQuery(formes);
        logger.debug("Query Span {}", spanOrQuery);

        
        return executeSpanQuery(tailleVoisinage, résultat, spanOrQuery);
    }
    
    private SpanOrQuery getSpanOrQuery(List<String> formes) {
        
        SpanOrQuery spanOrQuery = null;

        if (formes != null && formes.size() > 0)
        {
                SpanTermQuery clauses[] = new SpanTermQuery[formes.size()];
                
                for(int i = 0 ; i < formes.size(); i++) {
                    clauses[i] = new SpanTermQuery(new Term(DEFAULT_SEARCH_FIELD, formes.get(i)));
                }
                
                spanOrQuery = new SpanOrQuery(clauses);

        }
        return spanOrQuery;        
    }
    
    private SpanNearQuery getSpanNearQuery(String query, int slop)
    {
        SpanNearQuery spanNearQuery = null;

        if (query != null && query.trim().length() > 0)
        {
            // FIXME créer/étendre un QueryParser spécifique pour s'assurer
            // d'utiliser l'analyseur utilisé pour l'indexation!!!
            query = query.toLowerCase();

            // Parse query
            // Si tout entre guillemets ou un seul mot => SpanTermQuery
            SpanTermQuery clauses[] = new SpanTermQuery[0];

            query = query.replaceAll("\"", "").trim();

            if (query.indexOf(" ") > 0)
            {

                String[] strings = query.split(" ");
                clauses = new SpanTermQuery[strings.length];
                for (int i = 0; i < strings.length; i++)
                {
                    clauses[i] = new SpanTermQuery(new Term(DEFAULT_SEARCH_FIELD, strings[i]));
                }
            }
            else
            {
                clauses = new SpanTermQuery[1];
                clauses[0] = new SpanTermQuery(new Term(DEFAULT_SEARCH_FIELD, query));
            }

            // FIXME configurable
            boolean spanInOrder = false;
            spanNearQuery = new SpanNearQuery(clauses, slop, spanInOrder);

        }
        return spanNearQuery;
    }
    
    private RésultatRecherche executeSpanQuery(int window, RésultatRecherche result, SpanQuery spanQuery)
    {
        
        if(spanQuery == null || spanQuery.toString().trim().isEmpty()) {
            return result;
        }
        
        // Exécution du spanNearQuery
        result.scoreDocs = lh.getScoreDocs(searcher, spanQuery);

        if (result.scoreDocs.length > 0)
        {

            long stop = System.currentTimeMillis();

            try
            {

                // Récupération des Spans. Les spans sont triés, par ordre croissant
                // de document, ensuite par ordre croissant de position de début
                // (start) et
                // finalement par ordre croissant de position finale (end)
                Spans spans = spanQuery.getSpans(reader);

                int spanCount = 0;

                long start = System.currentTimeMillis();
                logger.debug("Avant search : {}", start);

                int oldDoc = -1;

                Map<Integer, TVPositionInfo> tokenPositions = null;
                String txtField = "";

                while (spans.next() == true)
                {

                    spanCount++;

                    int currentDocId = spans.doc();

                    // Ne faire qu'une fois par doc
                    if (oldDoc != currentDocId)
                    {

                        // extraire le voisinage avec un TermVectorMapper
                        PositionBasedTermVectorMapper tvm = new PositionBasedTermVectorMapper(false);

                        try
                        {

                            // Chargement du vecteur des termes dans une structure
                            // qui facilite la gestion de leur position
                            reader.getTermFreqVector(currentDocId, DEFAULT_SEARCH_FIELD, tvm);

                            // Recupération du champ texte du document courant
                            txtField = reader.document(currentDocId, new MapFieldSelector(DEFAULT_SEARCH_FIELD))
                                    .getField(DEFAULT_SEARCH_FIELD).stringValue();

                        }
                        catch (IOException e)
                        {
                            logger.error("Erreur lors de l'accès à l'index Lucene", e);
                        }

                        Map<String, Map<Integer, TVPositionInfo>> fieldToTerms = tvm.getFieldToTerms();

                        tokenPositions = fieldToTerms.get(DEFAULT_SEARCH_FIELD);

                    }

                    oldDoc = currentDocId;
                    // Fin « ne faire qu'une fois»

                    String[] contextParts = new String[4];
                    int startWindow = spans.start() - window;
                    int stopWindow = spans.end() + window - 1;

                    // Rechercher l'offset du début de la fenêtre
                    TVPositionInfo tvPositionInfo;
                    while ((tvPositionInfo = tokenPositions.get(startWindow++)) == null)
                        ;
                    int startContextIndex = tvPositionInfo.getOffsets().get(0).getStartOffset();
                    int startSpanIndex = tokenPositions.get(spans.start()).getOffsets().get(0).getStartOffset();

                    while ((tvPositionInfo = tokenPositions.get(stopWindow--)) == null)
                        ;
                    int stopContextIndex = tvPositionInfo.getOffsets().get(0).getEndOffset();
                    // FIXME pourquoi -1 ???
                    TVPositionInfo endPosInfo = tokenPositions.get(spans.end() - 1);
                    List<TermVectorOffsetInfo> endOffsets = endPosInfo.getOffsets();
                    // FIXME quid si plusieurs offsets ???
                    TermVectorOffsetInfo endOffsetInfo = endOffsets.get(0);
                    int stopSpanIndex = endOffsetInfo.getEndOffset();

                    // Récupération des contextes sous forme de 4 Strings
                    contextParts[0] = txtField.substring(startContextIndex, stopContextIndex); // gauche + mot + droite
                    contextParts[1] = txtField.substring(startContextIndex, startSpanIndex); // gauche
                    contextParts[2] = txtField.substring(startSpanIndex, stopSpanIndex); // mot
                    contextParts[3] = txtField.substring(stopSpanIndex, stopContextIndex); // droite

                    List<String[]> docContexts = result.documentContexts.get(currentDocId);

                    if (docContexts == null)
                    {
                        docContexts = new ArrayList<String[]>(5);
                    }
                    docContexts.add(contextParts);
                    result.documentContexts.put(currentDocId, docContexts);

                }

                stop = System.currentTimeMillis();
                logger.debug("Après search : {}", stop + " => " + (stop - start) + "ms");

                result.spanCount = spanCount;

            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        return result;
    }

   



    /**
     * 
     * @param query
     * @param booleanQuery
     * @param slop
     * @param window
     * @return
     */
    @Deprecated
    public RésultatRecherche getDocumentsWithContextsOld(String query, BooleanQuery booleanQuery, int slop, int window)
    {
        RésultatRecherche result = new RésultatRecherche();

        // TODO idéalement le query devrait être parsé par un Query Parser
        // «amélioré» (SpanNearQuery à la place d'un PhraseQuery
        // il serait ensuite filtré par l'éventuel BooleanQuery

        // Si un blanc pas entre "" dans le query => SpanNearQuery
        // Si un * => wildcard Query - Compatible avec Span???

        logger.debug("Query string = {}", query);

        BooleanQuery bQuery = new BooleanQuery();
        SpanNearQuery spanNearQuery = null;
        QueryWrapperFilter bQueryFilter = null;

        boolean hasBooleanQuery = false;
        boolean hasContextualQuery = false;

        if (booleanQuery != null && booleanQuery.getClauses().length > 0)
        {
            bQuery.add(booleanQuery, Occur.MUST);
            hasBooleanQuery = true;
        }

        if (query != null && query.trim().length() > 0)
        {
            // FIXME créer/étendre un QueryParser spécifique pour s'assurer
            // d'utiliser l'analyseur utilisé pour l'indexation!!!
            query = query.toLowerCase();

            // Parse query
            // Si tout entre guillemets ou un seul mot => SpanTermQuery
            SpanTermQuery clauses[] = new SpanTermQuery[0];

            query = query.replaceAll("\"", "").trim();
            if (query.indexOf(" ") > 0)
            {
                String[] strings = query.split(" ");
                clauses = new SpanTermQuery[strings.length];
                for (int i = 0; i < strings.length; i++)
                {
                    clauses[i] = new SpanTermQuery(new Term(DEFAULT_SEARCH_FIELD, strings[i]));
                }
            }
            else
            {
                clauses = new SpanTermQuery[1];
                clauses[0] = new SpanTermQuery(new Term(DEFAULT_SEARCH_FIELD, query));
            }

            // FIXME configurable
            boolean spanInOrder = false;
            spanNearQuery = new SpanNearQuery(clauses, slop, spanInOrder);

            hasContextualQuery = true;
        }

        long start = System.currentTimeMillis();
        logger.debug("Avant search : {}", start);
        if (hasBooleanQuery && !hasContextualQuery)
        {
            // Exécution d'un simple BooleanQuery - pas de Span
            logger.debug("d'un simple BooleanQuery - pas de Span. {}", bQuery);
            result.scoreDocs = lh.getScoreDocs(searcher, bQuery);
        }
        else if (hasContextualQuery && !hasBooleanQuery)
        {
            // Exécution d'un simple Query
            logger.debug("Exécution d'un simple Query : {}", spanNearQuery);
            result.scoreDocs = lh.getScoreDocs(searcher, spanNearQuery);

        }
        else if (hasBooleanQuery && hasContextualQuery)
        {
            // Query Span filtré avec boolean
            bQueryFilter = new QueryWrapperFilter(bQuery);

            logger.debug("Query Span filtré avec boolean. {} - {}", spanNearQuery, bQueryFilter);

            // SpanQueryFilter spanQueryFilter = new
            // SpanQueryFilter(spanNearQuery);
            result.scoreDocs = lh.getScoreDocs(searcher, spanNearQuery, bQueryFilter);

        }
        long stop = System.currentTimeMillis();
        logger.debug("Après search : {}", stop + " => " + (stop - start) + "ms");

        if (hasContextualQuery)
        {
            /*
             * I think maybe you could reverse this around. Get a filter from
             * your BooleanQuery and get the DocIdSet and then advance through
             * the Spans and the DocIdSetIterator, as they will both be forward
             * facing. For each span, check to see whether that doc is in the
             * filter or not.
             * http://lucene.apache.org/java/3_0_1/api/contrib-queries
             * /org/apache/lucene/search/BooleanFilter.html DocIdSet docIdSet =
             * bQueryFilter.getDocIdSet(reader); DocIdSetIterator
             * docIdSetIterator = docIdSet.iterator(); int docId; List<Integer>
             * docs = new ArrayList<Integer>(scoreDocs.length); while((docId =
             * docIdSetIterator.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS){
             * logger.debug("conserver les spans du doc {}", docId);
             * docs.add(docId); }
             */
            try
            {

                Spans spans = spanNearQuery.getSpans(reader);

                List<Integer> docs = new ArrayList<Integer>(result.scoreDocs.length);

                if (hasBooleanQuery)
                {
                    // filtrer les spans conserver uniquement ceux des docs
                    // retournés par le query
                    logger.debug("Il faut filtrer les Spans");

                    for (ScoreDoc scoreDoc : result.scoreDocs)
                    {
                        docs.add(scoreDoc.doc);
                    }

                }
                PositionBasedTermVectorMapper tvm = new PositionBasedTermVectorMapper(false);
                int spanCount = 0;

                start = System.currentTimeMillis();
                logger.debug("Avant getContexte : {}", start);

                while (spans.next() == true)
                {

                    if (!hasBooleanQuery || docs.contains(spans.doc()))
                    {
                        spanCount++;

                        // FIXME lent...
                        String[] contextes = getContexte(reader, DEFAULT_SEARCH_FIELD, spans, tvm, window);

                        List<String[]> docContexts = result.documentContexts.get(spans.doc());

                        if (docContexts == null)
                        {
                            docContexts = new ArrayList<String[]>(5);
                        }
                        docContexts.add(contextes);
                        result.documentContexts.put(spans.doc(), docContexts);
                    }

                }

                stop = System.currentTimeMillis();
                logger.debug("Après getContexte : {}", stop + " => " + (stop - start) + "ms");

                result.spanCount = spanCount;

            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return result;
    }

    /**
     * 
     * @param indexReader
     * @param fieldName
     * @param spans
     * @param tvm
     * @param window
     * @return
     */
    private String[] getContexte(IndexReader indexReader, String fieldName, Spans spans,
            PositionBasedTermVectorMapper tvm, int window)
    {
        // System.out.println("Doc: " + spans.doc() + " Start: " + spans.start()
        // + " End: " + spans.end());

        String[] contextParts = new String[4];
        int startWindow = spans.start() - window;
        int stopWindow = spans.end() + window - 1;

        String txtField = "";

        // extraire le voisinage avec un TermVectorMapper
        // (PositionBasedTermVectorMapper)
        try
        {

            indexReader.getTermFreqVector(spans.doc(), fieldName, tvm);
            txtField = indexReader.document(spans.doc(), new MapFieldSelector(fieldName)).getField(fieldName)
                    .stringValue();

        }
        catch (IOException e)
        {
            logger.error("Erreur lors de l'accès à l'index Lucene", e);
        }

        Map<String, Map<Integer, TVPositionInfo>> fieldToTerms = tvm.getFieldToTerms();
        Map<Integer, TVPositionInfo> tokenPositions = fieldToTerms.get(fieldName);

        // Rechercher l'offset du début de la fenêtre
        TVPositionInfo tvPositionInfo;
        while ((tvPositionInfo = tokenPositions.get(startWindow++)) == null)
            ;
        int startContextIndex = tvPositionInfo.getOffsets().get(0).getStartOffset();
        int startSpanIndex = tokenPositions.get(spans.start()).getOffsets().get(0).getStartOffset();

        // FIXME quid si plusieurs offsets ???
        // List<TermVectorOffsetInfo> startOffsets =
        // tvPositionInfo.getOffsets();
        // System.out.println("start offsetinfo : " + startOffsets);
        // for (TermVectorOffsetInfo termVectorOffsetInfo : startOffsets)
        // {
        // System.out.println(termVectorOffsetInfo.getStartOffset() + "=>" +
        // termVectorOffsetInfo.getEndOffset());
        // }

        while ((tvPositionInfo = tokenPositions.get(stopWindow--)) == null)
            ;
        int stopContextIndex = tvPositionInfo.getOffsets().get(0).getEndOffset();
        // FIXME pourquoi -1 ???
        TVPositionInfo endPosInfo = tokenPositions.get(spans.end() - 1);
        List<TermVectorOffsetInfo> endOffsets = endPosInfo.getOffsets();
        TermVectorOffsetInfo endOffsetInfo = endOffsets.get(0);
        int stopSpanIndex = endOffsetInfo.getEndOffset();
        // FIXME quid si plusieurs offsets ???
        // List<TermVectorOffsetInfo> stopOffsets = tvPositionInfo.getOffsets();
        // System.out.println("stop offsetinfo : " + stopOffsets);
        // for (TermVectorOffsetInfo termVectorOffsetInfo : stopOffsets)
        // {
        // System.out.println(termVectorOffsetInfo.getStartOffset() + "=>" +
        // termVectorOffsetInfo.getEndOffset());
        // }

        contextParts[0] = txtField.substring(startContextIndex, stopContextIndex);
        contextParts[1] = txtField.substring(startContextIndex, startSpanIndex);
        contextParts[2] = txtField.substring(startSpanIndex, stopSpanIndex);
        contextParts[3] = txtField.substring(stopSpanIndex, stopContextIndex);

        return contextParts;
    }

    public Document getDocument(int luceneDocId)
    {
        Document doc = null;
        try
        {
            doc = searcher.doc(luceneDocId);
        }
        catch (CorruptIndexException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return doc;
    }

    public Analyzer getAnalyseur()
    {
        return analyseur;
    }

    public void setAnalyseur(Analyzer analyzer)
    {
        this.analyseur = analyzer;
    }

    public void setIdFieldName(String idFieldName)
    {
        this.lh.setIdFieldName(idFieldName);
    }

    /**
     * Par défaut, TermInfo triés par fréquences décroissantes
     * 
     * @param fieldName
     * @return
     */
    public List<InformationTerme> getTopTerms(String fieldName)
    {
        return getTopTerms(fieldName, null);
    }

    /**
     * TODO : Les topterms pourraient tous être stocké en mémoire (static) pour
     * de meilleure performances
     * 
     * @param fieldName
     * @param comparator
     * @return
     */
    public List<InformationTerme> getTopTerms(String fieldName, Comparator<InformationTerme> comparator)
    {
        List<InformationTerme> topTerms = new ArrayList<InformationTerme>();

        try
        {
            InformationTerme[] terms = TermeFréquenceÉlevée.getTermesFréquenceÉlevée(reader, null, 1000,
                    new String[] { fieldName });
            for (InformationTerme termInfo : terms)
            {
                topTerms.add(termInfo);
            }

            // Trier selon le comparateur passé en paramètre
            if (comparator != null)
            {
                Collections.sort(topTerms, comparator);
            }

        }
        catch (Exception e)
        {
            logger.error("Erreur lors de l'extraction des topTerms de l'index {}", reader, e);
        }

        return topTerms;
    }

    public static String getDefaultSearchField()
    {
        return DEFAULT_SEARCH_FIELD;
    }

    public IndexReader getReader()
    {
        return reader;
    }

}
