package com.servicelibre.corpus.service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.servicelibre.corpus.entity.Corpus;
import com.servicelibre.corpus.lucene.InformationTerme;
import com.servicelibre.corpus.lucene.InformationTermeTextComparator;
import com.servicelibre.corpus.lucene.LuceneIndexManager;
import com.servicelibre.corpus.lucene.RésultatRecherche;
import com.servicelibre.corpus.manager.CorpusManager;
import com.servicelibre.corpus.manager.FiltreMot;

public class CorpusService {

	private static final String TXT_FIELDNAME = "txt";

	private static final String TXTLEX_FIELDNAME = "txtlex";

	private static Logger logger = LoggerFactory.getLogger(CorpusService.class);

	private Corpus corpus;

	private CorpusManager corpusManager;

	private LuceneIndexManager indexManager;

	private FormeService formeService;

	protected int tailleVoisinage = 10;

	public CorpusService(CorpusManager cm, Corpus corpus) {
		this.corpusManager = cm;
		this.corpus = ouvreOuCréeCorpus(cm, corpus);
	}

	public Corpus getCorpus() {
		return this.corpus;
	}

	public CorpusManager getCorpusManager() {
		return corpusManager;
	}

	public void setCorpusManager(CorpusManager cm) {
		this.corpusManager = cm;
	}

	public void setCorpus(Corpus corpus) {
		this.corpus = corpus;
	}

	protected Corpus ouvreOuCréeCorpus(CorpusManager cm, Corpus corpus) {

		Corpus corpusTrouvéOuCréé = cm.findByNom(corpus.getNom());

		if (corpusTrouvéOuCréé == null) {
			logger.info("Création du nouveau corpus " + corpus);
			cm.save(corpus);
			return corpus;
		} else {
			logger.info("Ouverture du corpus " + corpus);
			return corpusTrouvéOuCréé;
		}
	}

	public ContexteSet getContextesMot(String mot) {
		return getContextesMot(mot, null);
	}
	
	/**
	 * Recherche les contextes du mot donné
	 * 
	 * @param mot
	 * @param filtres 
	 * @return
	 */
	public ContexteSet getContextesMot(String mot, FiltreMot filtres) {

		// connecter à l'index Lucene et faire la recherche
		LuceneIndexManager manager = getLuceneIndexManager();

		RésultatRecherche résultats = manager.getDocumentsWithContexts(mot, 1, tailleVoisinage, filtres);

		logger.debug("Trouvé " + résultats.scoreDocs.length + " documents (mot) => " + résultats.spanCount);

		ContexteSet contexteSet = getContextes(résultats);
		contexteSet.setMotCherché(mot);
		contexteSet.setFormesDuLemme(false);
		return contexteSet;
	}

	public ContexteSet getContextesLemme(String lemme) {
		return getContextesLemme(lemme, null);
	}
	
	public ContexteSet getContextesLemme(String lemme, FiltreMot filtres) {

		// rechercher toutes les formes du lemme
		RésultatRecherche résultats = getLuceneIndexManager().getDocumentsWithContexts(formeService.getFormes(lemme), tailleVoisinage, filtres);

		logger.debug("Trouvé " + résultats.scoreDocs.length + " documents (lemme) => " + résultats.spanCount);

		ContexteSet contexteSet = getContextes(résultats);
		contexteSet.setMotCherché(lemme);
		contexteSet.setFormesDuLemme(true);
		return contexteSet;
	}

	private ContexteSet getContextes(RésultatRecherche résultats) {

		ContexteSet contexteSet = new ContexteSet();

		List<Contexte> contextes = new ArrayList<Contexte>(résultats.spanCount);

		for (int i = 0; i < résultats.scoreDocs.length; i++) {
			List<String[]> ctx = résultats.documentContexts.get(résultats.scoreDocs[i].doc);
			if (ctx != null && ctx.size() > 0) {
				for (String[] contextParts : ctx) {
					contextes.add(new Contexte(contextParts[1], contextParts[2], contextParts[3]));
				}
			}
		}

		contexteSet.setContextes(contextes);
		contexteSet.setDocumentCount(résultats.scoreDocs.length);

		return contexteSet;
	}

	public List<DefaultKeyValue> getValeursChamp(String champIndex) {

		// Récupérer les valeurs = topTerms Lucene , trié par ordre alpha fr_CA
		List<InformationTerme> topTerms = getLuceneIndexManager().getTopTerms(champIndex, new InformationTermeTextComparator<InformationTerme>());

		List<DefaultKeyValue> valeurs = new ArrayList<DefaultKeyValue>(topTerms.size());

		for (InformationTerme topTerm : topTerms) {
			valeurs.add(new DefaultKeyValue(topTerm.term.text(), topTerm.docFreq));
		}

		return valeurs;
	}

	public LuceneIndexManager getLuceneIndexManager() {

		if (this.indexManager == null) {

			final Map<String, Analyzer> fieldAnalyzers = new HashMap<String, Analyzer>();

			Analyzer searchAnalyzer = getInstanceAnalyseur(corpus.getAnalyseurRechercheFQCN());

			fieldAnalyzers.put(TXT_FIELDNAME, searchAnalyzer);
			fieldAnalyzers.put(TXTLEX_FIELDNAME, getInstanceAnalyseur(corpus.getAnalyseurLexicalFQCN()));

			final PerFieldAnalyzerWrapper perFieldAnalyzerWrapper = new PerFieldAnalyzerWrapper(searchAnalyzer, fieldAnalyzers);

			FSDirectory fsDirectory = null;
			LuceneIndexManager manager = null;

			try {

				String dossierData = corpus.getDossierData();

				if (dossierData != null) {

					File dossierLuceneIndex = new File(dossierData);

					if (dossierLuceneIndex.exists()) {
						fsDirectory = FSDirectory.open(dossierLuceneIndex);
						manager = new LuceneIndexManager(fsDirectory, perFieldAnalyzerWrapper);
					} else {
						logger.error("Le dossier qui contient l'index Lucene pour le corpus {} est introuvable: {}", corpus, dossierLuceneIndex);
					}
				}
				else {
					logger.error("Le dossier qui contient l'index Lucene pour le corpus {} est NULL.", corpus);
				}
			} catch (IOException e) {
				logger.error("Erreur lors de l'ouverture du dossier de l'index Lucene [{}]", corpus.getDossierData(), e);
			}
			this.indexManager = manager;
		}

		return this.indexManager;
	}

	private Analyzer getInstanceAnalyseur(String analyseurFQCN) {
		try {
			Class<?> cl = Class.forName(analyseurFQCN);
			@SuppressWarnings("rawtypes")
			java.lang.reflect.Constructor co = cl.getConstructor(new Class[] { Version.class });
			return (Analyzer) co.newInstance(new Object[] { Version.LUCENE_30 });
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public int getTailleVoisinnage() {
		return tailleVoisinage;
	}

	public void setTailleVoisinnage(int tailleVoisinnage) {
		this.tailleVoisinage = tailleVoisinnage;
	}

	public FormeService getFormeService() {
		return formeService;
	}

	public void setFormeService(FormeService formeService) {
		this.formeService = formeService;
	}

}
