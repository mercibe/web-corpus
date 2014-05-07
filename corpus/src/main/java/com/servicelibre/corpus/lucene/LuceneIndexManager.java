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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.MapFieldSelector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.PositionBasedTermVectorMapper;
import org.apache.lucene.index.PositionBasedTermVectorMapper.TVPositionInfo;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.TermVectorOffsetInfo;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
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
import com.servicelibre.corpus.lucene.RésultatRecherche.Contexte;
import com.servicelibre.corpus.manager.Filtre;
import com.servicelibre.corpus.manager.FiltreRecherche;

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

	private String champRecherche;

	public LuceneIndexManager(FSDirectory dossierIndex, Analyzer analyseur) {
		super();
		this.analyseur = analyseur;
		this.champRecherche = DEFAULT_SEARCH_FIELD;
		setDossierIndex(dossierIndex);
	}

	public LuceneIndexManager(FSDirectory dossierIndex, Analyzer analyseur, String champRecherche) {
		super();
		this.analyseur = analyseur;
		this.champRecherche = champRecherche;
		setDossierIndex(dossierIndex);
	}

	public FSDirectory getDossierIndex() {
		return dossierIndex;
	}

	public void setDossierIndex(FSDirectory dossierIndex) {
		this.dossierIndex = dossierIndex;

		try {
			reader = IndexReader.open(dossierIndex, true);
		} catch (CorruptIndexException e) {
			logger.error("Erreur lors de la récupération d'un reader sur l'index Lucene", e);
		} catch (IOException e) {
			logger.error("Erreur lors de la récupération d'un reader sur l'index Lucene", e);

		}
		searcher = new IndexSearcher(reader);

		// Toujours s'assurer que le query parser utilise bien l'analyseur qui a
		// été utilisé pour indexer
		// le champ sur le quel s'effectue la recherche!
		queryParser = new QueryParser(Version.LUCENE_33, champRecherche, analyseur);
	}

	public Document getDocument(String docId) {
		return lh.getDoc(searcher, docId);
	}

	public ScoreDoc[] getDocuments(String query) {
		return getDocuments(query, null);
	}

	public ScoreDoc[] getDocuments(String query, BooleanQuery optionsBooleanQuery) {
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
	public ScoreDoc[] getDocuments(String query, BooleanQuery booleanQuery, int slop) {

		ScoreDoc scoreDocs[] = new ScoreDoc[] {};

		BooleanQuery bQuery = new BooleanQuery();
		if (query != null && query.trim().length() > 0) {

			// PhraseQuery pq = new PhraseQuery();
			// pq.setSlop(0);
			// query.add(new Term("subject","job"));
			// query.add(new Term("subject","opening"));
			// query.add(new Term("subject","j2ee"));
			// si query entre "" => phrase query / split sur le blanc

			if (slop > 0) {
				// TODO quid MultiPhraseQuery?
				PhraseQuery pq = new PhraseQuery();
				pq.setSlop(slop);
				// supprimer les guillemets
				query = query.replaceAll("\"", "");

				// Split sur blanc et construction du phrase query
				String[] terms = query.split(" ");
				for (String term : terms) {
					pq.add(new Term(this.champRecherche, term));
				}
				bQuery.add(pq, Occur.MUST);
			} else {
				try {
					Query parsedQuery = queryParser.parse(query);
					// FIXME
					// SpanTermQuery spanQ = new SpanTermQuery(null);
					bQuery.add(parsedQuery, Occur.MUST);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

		}

		logger.debug("booleanQuery.getClauses().length = {}", booleanQuery.getClauses().length);
		if (booleanQuery != null && booleanQuery.getClauses().length > 0) {
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

	private BooleanQuery getFiltresBooleanQuery(FiltreRecherche filtres) {

		LinkedHashSet<Filtre> f = filtres.getFiltres();

		BooleanQuery filtresBooleanQuery = new BooleanQuery();

		for (Iterator<Filtre> it = f.iterator(); it.hasNext();) {

			Filtre filtre = it.next();

			BooleanQuery champBooleanQuery = new BooleanQuery();

			for (DefaultKeyValue kv : filtre.keyValues) {
				champBooleanQuery.add(new TermQuery(new Term(filtre.nom, kv.getKey() + "")), BooleanClause.Occur.SHOULD);
			}

			if (champBooleanQuery.getClauses().length > 0) {
				logger.debug("ajout d'une clause pour champ {}", filtre.nom);
				filtresBooleanQuery.add(champBooleanQuery, BooleanClause.Occur.MUST);
			}

		}

		logger.debug("filtresBooleanQuery = {}", filtresBooleanQuery);
		return filtresBooleanQuery;

	}

	/**
	 * 
	 * @param query
	 * @param booleanQuery
	 * @param slop
	 * @param tailleVoisinage
	 * @param filtres
	 * @return
	 */
	public RésultatRecherche getDocumentsWithContexts(String query, int slop, int tailleVoisinage, FiltreRecherche filtres, int deIndex,
			int taillePage) {

		RésultatRecherche résultat = new RésultatRecherche();

		// TODO idéalement le query devrait être parsé par un Query Parser
		// «amélioré» (SpanNearQuery à la place d'un PhraseQuery
		// il serait ensuite filtré par l'éventuel BooleanQuery

		// TODO IMPORTANT
		// Si pas de blanc => SpanTermQuery
		// Si un blanc pas entre "" dans le query => SpanNearQuery
		// Si un * => wildcard Query - Compatible avec Span via
		// SpanMultiTermQueryWrapper

		logger.debug("Query string = {}", query);
		SpanNearQuery spanNearQuery = getSpanNearQuery(query, slop);

		if (filtres != null && filtres.getFiltres().size() > 0) {

			// Construction du boolean query pour le filtre
			BooleanQuery bQuery = getFiltresBooleanQuery(filtres);

			// TODO execute (filter) from BooleanQuery, get the DocIdSet and
			// then advance through the Spans and the DocIdSetIterator, as they
			// will both be forward facing. For each span, check to see whether
			// that doc is in the filter or not.

			// Query Span filtré avec boolean
			QueryWrapperFilter bQueryFilter = new QueryWrapperFilter(bQuery);

			logger.debug("Query Span filtré avec boolean. {} - {}", spanNearQuery, bQueryFilter);

			// SpanQueryFilter spanQueryFilter = new
			// SpanQueryFilter(spanNearQuery);
			return executeSpanQuery(tailleVoisinage, résultat, spanNearQuery, bQueryFilter, deIndex, taillePage);

		} else {

			logger.debug("Query Span {}", spanNearQuery);

			return executeSpanQuery(tailleVoisinage, résultat, spanNearQuery, deIndex, taillePage);
		}

	}

	public RésultatRecherche getDocumentsWithContexts(List<String> formes, int tailleVoisinage, FiltreRecherche filtres, int deIndex,
			int taillePage) {
		RésultatRecherche résultat = new RésultatRecherche();

		logger.debug("formes à chercher: " + formes);
		logger.debug("Query string = {}", queryParser.toString());
		SpanOrQuery spanOrQuery = getSpanOrQuery(formes);
		logger.debug("Query Span {}", spanOrQuery);

		if (filtres != null && filtres.getFiltres().size() > 0) {

			// Construction du boolean query pour le filtre
			BooleanQuery bQuery = getFiltresBooleanQuery(filtres);

			// Query Span filtré avec boolean
			QueryWrapperFilter bQueryFilter = new QueryWrapperFilter(bQuery);

			logger.debug("SpanOrQuery filtré avec boolean. {} - {}", spanOrQuery, bQueryFilter);

			return executeSpanQuery(tailleVoisinage, résultat, spanOrQuery, bQueryFilter, deIndex, taillePage);

		} else {

			return executeSpanQuery(tailleVoisinage, résultat, spanOrQuery, deIndex, taillePage);
		}
	}

	private SpanOrQuery getSpanOrQuery(List<String> formes) {

		SpanOrQuery spanOrQuery = null;

		if (formes != null && formes.size() > 0) {
			SpanTermQuery clauses[] = new SpanTermQuery[formes.size()];

			for (int i = 0; i < formes.size(); i++) {
				clauses[i] = new SpanTermQuery(new Term(champRecherche, formes.get(i)));
			}

			spanOrQuery = new SpanOrQuery(clauses);

		}
		return spanOrQuery;
	}

	private SpanNearQuery getSpanNearQuery(String query, int slop) {
		SpanNearQuery spanNearQuery = null;

		if (query != null && query.trim().length() > 0) {
			// FIXME créer/étendre un QueryParser spécifique pour s'assurer
			// d'utiliser l'analyseur utilisé pour l'indexation!!!
			query = query.toLowerCase();

			// Parse query
			// Si tout entre guillemets ou un seul mot => SpanTermQuery
			SpanTermQuery clauses[] = new SpanTermQuery[0];

			query = query.replaceAll("\"", "").trim();

			if (query.indexOf(" ") > 0) {

				String[] strings = query.split(" ");
				clauses = new SpanTermQuery[strings.length];
				for (int i = 0; i < strings.length; i++) {
					clauses[i] = new SpanTermQuery(new Term(this.champRecherche, strings[i]));
				}
			} else {
				clauses = new SpanTermQuery[1];
				clauses[0] = new SpanTermQuery(new Term(this.champRecherche, query));
			}

			// FIXME configurable
			boolean spanInOrder = false;
			spanNearQuery = new SpanNearQuery(clauses, slop, spanInOrder);

		}
		return spanNearQuery;
	}

	private RésultatRecherche executeSpanQuery(int window, RésultatRecherche result, SpanQuery spanQuery, int deIndex, int taillePage) {
		return executeSpanQuery(window, result, spanQuery, null, deIndex, taillePage);
	}

	private RésultatRecherche executeSpanQuery(int window, RésultatRecherche result, SpanQuery spanQuery, QueryWrapperFilter bQueryFilter,
			int deIndex, int taillePage) {

		if (spanQuery == null || spanQuery.toString().trim().isEmpty()) {
			return result;
		}

		// Exécution du spanNearQuery
		if (bQueryFilter != null) {
			result.scoreDocs = lh.getScoreDocs(searcher, spanQuery, bQueryFilter);
		} else {
			result.scoreDocs = lh.getScoreDocs(searcher, spanQuery);
		}

		result.nbTotalDocs = result.scoreDocs.length;

		logger.debug("Trouvé {} scoreDocs.", result.nbTotalDocs);
		//System.err.println("Trouvé " + result.nbTotalDocs + " scoreDocs.");

		if (result.scoreDocs.length > 0) {

			long stop = System.currentTimeMillis();

			try {

				// Récupération des Spans. Les spans sont triés, par ordre
				// croissant
				// de document, ensuite par ordre croissant de position de début
				// (start) et
				// finalement par ordre croissant de position finale (end)

				Spans spans = spanQuery.getSpans(reader);
				List<Span> spanObjets = new ArrayList<Span>(50);
				
				// Conversion du tableau de documents (scoreDoc) uniquement si filtre 
				int[] scoreDocs = new int[result.scoreDocs.length];
				if (bQueryFilter != null) {
					for (int i =0 ; i< result.scoreDocs.length;i++) {
						scoreDocs[i] = result.scoreDocs[i].doc;
					}
					Arrays.sort(scoreDocs);
				}
				
				while (spans.next()) {
					// Ne conserver que les spans des documents retournés si filtre actif
					ScoreDoc d;
					if(bQueryFilter == null || Arrays.binarySearch(scoreDocs,spans.doc()) >= 0)
					{
						spanObjets.add(new Span(spans.doc(), spans.start(), spans.end()));
					}
				}

				result.nbTotalContextes = spanObjets.size();

				logger.debug("Trouvé {} spans.", result.nbTotalContextes);

				int àIndex = result.nbTotalContextes;

				// Ne conserver que les spans
				if (deIndex >= 0 && taillePage > 0) {

					àIndex = Math.min(deIndex + taillePage, result.nbTotalContextes);

					logger.debug("Ne conserver que {} sur {} de ces spans (pagination)", (àIndex - deIndex), result.nbTotalContextes);
					
				} else {
					deIndex = Math.max(deIndex, 0);
				}
				
				result.spanCount = àIndex-deIndex;

				// Création de la liste des contextes à retourner
				result.contextes = new ArrayList<RésultatRecherche.Contexte>(àIndex);

				List<Integer> docs = new ArrayList<Integer>(result.scoreDocs.length);

				if (bQueryFilter != null) {
					// filtrer les spans conserver uniquement ceux des docs
					// retournés par le query
					logger.debug("Il faut filtrer les Spans");

					// Pour facilter le traitement, conversion du tableau en
					// liste.
					for (ScoreDoc scoreDoc : result.scoreDocs) {
						docs.add(scoreDoc.doc);
					}

				}


				long start = System.currentTimeMillis();
				logger.debug("Avant search : {}", start);

				int oldDoc = -1;

				Map<Integer, TVPositionInfo> tokenPositions = null;
				String txtField = "";

				for (int spanIdx = deIndex; spanIdx < àIndex; spanIdx++) {

					Span span = spanObjets.get(spanIdx);

					if (bQueryFilter == null || docs.contains(span.doc)) {

						int currentDocId = span.doc;

						// Ne faire qu'une fois par doc
						if (oldDoc != currentDocId) {

							// extraire le voisinage avec un TermVectorMapper
							PositionBasedTermVectorMapper tvm = new PositionBasedTermVectorMapper(false);

							try {

								// Chargement du vecteur des termes dans une
								// structure
								// qui facilite la gestion de leur position
								reader.getTermFreqVector(currentDocId, champRecherche, tvm);

								// Recupération du champ texte du document
								// courant
								txtField = reader.document(currentDocId, new MapFieldSelector(champRecherche)).getField(champRecherche)
										.stringValue();

							} catch (IOException e) {
								logger.error("Erreur lors de l'accès à l'index Lucene", e);
							}

							Map<String, Map<Integer, TVPositionInfo>> fieldToTerms = tvm.getFieldToTerms();

							tokenPositions = fieldToTerms.get(champRecherche);
						}

						oldDoc = currentDocId;
						// Fin « ne faire qu'une fois»

						String[] contextParts = new String[4];

						// TODO gérer ici voisinage / window en terme de phrase

						int startWindow = span.start - window;
						int stopWindow = span.end + window - 1;

						// Rechercher l'offset du début de la fenêtre
						TVPositionInfo tvPositionInfo;
						while ((tvPositionInfo = tokenPositions.get(startWindow++)) == null)
							;
						int startContextIndex = tvPositionInfo.getOffsets().get(0).getStartOffset();
						int startSpanIndex = tokenPositions.get(span.start).getOffsets().get(0).getStartOffset();

						while ((tvPositionInfo = tokenPositions.get(stopWindow--)) == null)
							;
						int stopContextIndex = tvPositionInfo.getOffsets().get(0).getEndOffset();
						// FIXME pourquoi -1 ???
						TVPositionInfo endPosInfo = tokenPositions.get(span.end - 1);
						List<TermVectorOffsetInfo> endOffsets = endPosInfo.getOffsets();
						// FIXME quid si plusieurs offsets ???
						TermVectorOffsetInfo endOffsetInfo = endOffsets.get(0);
						int stopSpanIndex = endOffsetInfo.getEndOffset();

						// Récupération des contextes sous forme de 4 Strings
						if (stopContextIndex >= txtField.length()) {
							System.err.println("analyseur: " + this.analyseur.getClass().getName());
							System.err.println("queryParser: " + this.queryParser.getClass().getName());

							System.err.println("txtField.length() = " + txtField.length() + ", startContextIndex=" + startContextIndex
									+ ", stopContextIndex=" + stopContextIndex);
							System.err.println(txtField.substring(startContextIndex));
							for (Contexte contexte : result.contextes) {
								System.err.println(contexte.partiesDeContexte[1] + "=>" + contexte.partiesDeContexte[2] + "<="
										+ contexte.partiesDeContexte[3]);

								// for(String s : c)
								// {
								// System.err.println("=====================");
								// }

							}
						}

						contextParts[0] = txtField.substring(startContextIndex, stopContextIndex); // gauche
						// +
						// mot
						// +
						// droite
						contextParts[1] = txtField.substring(startContextIndex, startSpanIndex); // gauche
						contextParts[2] = txtField.substring(startSpanIndex, stopSpanIndex); // mot
						contextParts[3] = txtField.substring(stopSpanIndex, stopContextIndex); // droite

						result.contextes.add(new RésultatRecherche().new Contexte(currentDocId, contextParts));

						// List<String[]> docContexts = result.documentContexts.get(currentDocId);
						//
						// if (docContexts == null) {
						// docContexts = new ArrayList<String[]>(5);
						// }
						// docContexts.add(contextParts);
						// result.documentContexts.put(currentDocId, docContexts);

					} else {
						System.err.println("NE FAIT RIEN!!!!!");
					}
				}

				stop = System.currentTimeMillis();
				logger.debug("Après search : {}", stop + " => " + (stop - start) + "ms");


			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}

	public Document getDocument(int luceneDocId) {
		Document doc = null;
		try {
			doc = searcher.doc(luceneDocId);
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return doc;
	}

	public Analyzer getAnalyseur() {
		return analyseur;
	}

	public void setAnalyseur(Analyzer analyzer) {
		this.analyseur = analyzer;
	}

	public void setIdFieldName(String idFieldName) {
		this.lh.setIdFieldName(idFieldName);
	}

	/**
	 * Par défaut, TermInfo triés par fréquences décroissantes
	 * 
	 * @param fieldName
	 * @return
	 */
	public List<InformationTerme> getTopTerms(String fieldName) {
		return getTopTerms(fieldName, null);
	}

	/**
	 * Retourne toutes les valeurs possibles d'un champ stocké (stored) donné
	 * ainsi que leurs fréquences dans l'index. Si le champ est tokenizé, cette
	 * fonction retourne l'ensemble des tokens.
	 * 
	 * TODO : Les topterms pourraient tous être stockés en mémoire (static) pour
	 * de meilleure performances
	 * 
	 * @param fieldName
	 * @param comparator
	 * @return
	 */
	public List<InformationTerme> getTopTerms(String fieldName, Comparator<InformationTerme> comparator) {
		List<InformationTerme> topTerms = new ArrayList<InformationTerme>();

		try {
			InformationTerme[] terms = TermeFréquenceÉlevée.getTermesFréquenceÉlevée(reader, null, 1000, new String[] { fieldName });
			for (InformationTerme termInfo : terms) {
				topTerms.add(termInfo);
			}

			// Trier selon le comparateur passé en paramètre
			if (comparator != null) {
				Collections.sort(topTerms, comparator);
			}

		} catch (Exception e) {
			logger.error("Erreur lors de l'extraction des topTerms de l'index {}", reader, e);
		}

		return topTerms;
	}

	/**
	 * Retourne toutes les valeurs possibles d'un champ stocké (stored) donné.
	 * Si le champ est tokenizé, cette fonction retourne l'ensemble des tokens.
	 * Contrairement à la fonction {@link #getTopTerms(String)}, cette fonction
	 * ne retourne aucune information sur la fréquence de la valeur dans l'index
	 * ({@link InformationTerme#docFreq} = -1).
	 * 
	 * @param fieldName
	 * @param comparator
	 * @return
	 */
	public List<InformationTerme> getValeursChamp(String fieldName, Comparator<InformationTerme> comparator) {

		List<InformationTerme> topTerms = new ArrayList<InformationTerme>();

		try {
			TermEnum terms = reader.terms(new Term(fieldName, ""));
			while (fieldName.equals(terms.term().field())) {
				topTerms.add(new InformationTerme(terms.term(), -1));
				if (!terms.next()) {
					break;
				}
			}

			// Trier selon le comparateur passé en paramètre
			if (comparator != null) {
				Collections.sort(topTerms, comparator);
			}

		} catch (Exception e) {
			logger.error("Erreur lors de l'extraction des topTerms de l'index {}", reader, e);
		}

		return topTerms;
	}

	// public static String getDefaultSearchField() {
	// return DEFAULT_SEARCH_FIELD;
	// }

	public IndexReader getReader() {
		return reader;
	}

	public class Span {
		public int doc;
		public int start;
		public int end;

		public Span(int doc, int start, int end) {
			super();
			this.doc = doc;
			this.start = start;
			this.end = end;
		}

	}

}
