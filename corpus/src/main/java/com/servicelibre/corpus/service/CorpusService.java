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
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.servicelibre.corpus.entity.Corpus;
import com.servicelibre.corpus.entity.DocMetadata;
import com.servicelibre.corpus.lucene.InformationTerme;
import com.servicelibre.corpus.lucene.InformationTermeTextComparator;
import com.servicelibre.corpus.lucene.LuceneIndexManager;
import com.servicelibre.corpus.lucene.RésultatRecherche;
import com.servicelibre.corpus.manager.CorpusManager;
import com.servicelibre.corpus.manager.FiltreMot;
import com.servicelibre.corpus.metadata.Metadata;
import com.servicelibre.corpus.metadata.StringMetadata;

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

	public CorpusService(CorpusManager cm) {
		this.corpusManager = cm;
		this.corpus = ouvreCorpusParDéfaut(cm);
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
			try {
				cm.save(corpus);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return corpus;
		} else {
			logger.info("Ouverture du corpus " + corpus);
			return corpusTrouvéOuCréé;
		}
	}

	private Corpus ouvreCorpusParDéfaut(CorpusManager cm) {

		Corpus corpusParDéfaut = cm.findByDefault();

		if (corpusParDéfaut == null) {
			// Création d'un corpus vide par défaut
			String corpusIdString = "DÉMARRAGE";
			String tmpDir = System.getProperty("java.io.tmpdir")
					+ File.separatorChar + "index-" + corpusIdString;
			corpusParDéfaut = new Corpus(corpusIdString, "Corpus de démarrage",
					tmpDir, "com.servicelibre.corpus.analysis.FrenchAnalyzer",
					"org.apache.lucene.analysis.standard.StandardAnalyzer");
			// FIXME s'assurer qu'un seul corpus par défaut existe!
			corpusParDéfaut.setParDéfaut(true);
			logger.info("Création du nouveau corpus " + corpusParDéfaut);
			cm.save(corpusParDéfaut);
			return corpusParDéfaut;
		} else {
			logger.info("Ouverture du corpus par défaut " + corpusParDéfaut);
			return corpusParDéfaut;
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

		if (manager == null) {
			return new ContexteSet();
		}

		RésultatRecherche résultats = manager.getDocumentsWithContexts(mot, 1,
				tailleVoisinage, filtres);

		logger.debug("Trouvé " + résultats.scoreDocs.length
				+ " documents (mot) => " + résultats.spanCount);

		ContexteSet contexteSet = getContextes(résultats);
		contexteSet.setMotCherché(mot);
		contexteSet.setFormesDuLemme(false);
		contexteSet.tailleVoisinage = tailleVoisinage;
		return contexteSet;
	}

	public ContexteSet getContextesLemme(String lemme) {
		return getContextesLemme(lemme, null);
	}

	public ContexteSet getContextesLemme(String lemme, FiltreMot filtres) {

		// rechercher toutes les formes du lemme
		LuceneIndexManager luceneIndexManager = getLuceneIndexManager();

		if (luceneIndexManager == null || formeService == null) {
			return new ContexteSet();
		}

		RésultatRecherche résultats = luceneIndexManager
				.getDocumentsWithContexts(formeService.getFormes(lemme),
						tailleVoisinage, filtres);

		logger.debug("Trouvé " + résultats.scoreDocs.length
				+ " documents (lemme) => " + résultats.spanCount);

		ContexteSet contexteSet = getContextes(résultats);
		contexteSet.setMotCherché(lemme);
		contexteSet.setFormesDuLemme(true);
		contexteSet.setTailleVoisinage(tailleVoisinage);

		return contexteSet;
	}

	public boolean isLemme(String mot) {
		return formeService.getFormes(mot).size() > 0;
	}

	private ContexteSet getContextes(RésultatRecherche résultats) {

		ContexteSet contexteSet = new ContexteSet();
		List<Contexte> contextes = new ArrayList<Contexte>(résultats.spanCount);

		// Pour chaque document du résultat de recherche...
		for (int i = 0; i < résultats.scoreDocs.length; i++) {
			// récupérer les métadonnées du document d'où sont extraits les
			// contextes
			List<Metadata> docMétadonnées = getMétadonnéesDocument(résultats.scoreDocs[i].doc);

			List<String[]> ctx = résultats.documentContexts
					.get(résultats.scoreDocs[i].doc);
			if (ctx != null && ctx.size() > 0) {
				// ... extraire les contextes trouvés pour ce document
				int cptContext = 1;
				for (String[] contextParts : ctx) {
					Contexte contexte = new Contexte(contextParts[1],
							contextParts[2], contextParts[3]);
					contexte.setDocMétadonnées(docMétadonnées);
					contexte.setId(new StringBuilder().append(contexte.mot)
							.append("_").append(résultats.scoreDocs[i].doc)
							.append("_").append(cptContext).toString());
					cptContext++;
					contextes.add(contexte);
				}
			}
		}

		contexteSet.setContextes(contextes);
		contexteSet.setDocumentCount(résultats.scoreDocs.length);

		return contexteSet;
	}

	private List<Metadata> getMétadonnéesDocument(int docId) {

		List<Metadata> métadonnées = new ArrayList<Metadata>();

		// FIXME passé en paramètre: dépend du corpus!
		LuceneIndexManager luceneIndexManager = getLuceneIndexManager();

		if (luceneIndexManager == null) {
			return métadonnées;
		}

		Document document = luceneIndexManager.getDocument(docId);

		// Récupère tous les champs à associer au contexte jugés pertinents
		// L'ordre des champs de la liste est respecté
		for (DocMetadata docMetadata : this.corpus.getMétadonnéesDoc()) {
			String nomChamp = docMetadata.getChampIndex();
			Field champ = (Field) document.getFieldable(nomChamp);
			if (champ != null && !champ.isBinary()) {
				// Utilisation du nom au lieu du champ (pour la présentation à
				// l'écran)
				métadonnées.add(new StringMetadata(docMetadata.getNom(), champ
						.stringValue(), docMetadata.isPrimaire()));
			}
		}

		return métadonnées;
	}

	public List<DefaultKeyValue> getValeursChampAvecFréquence(String champIndex) {

		// Récupérer les valeurs = topTerms Lucene , trié par ordre alpha fr_CA
		LuceneIndexManager luceneIndexManager = getLuceneIndexManager();

		if (luceneIndexManager == null) {
			return new ArrayList<DefaultKeyValue>();
		}

		List<InformationTerme> topTerms = luceneIndexManager.getTopTerms(champIndex,new InformationTermeTextComparator<InformationTerme>());

		List<DefaultKeyValue> valeurs = new ArrayList<DefaultKeyValue>(
				topTerms.size());

		for (InformationTerme topTerm : topTerms) {
			valeurs.add(new DefaultKeyValue(topTerm.term.text(),
					topTerm.docFreq));
		}

		return valeurs;
	}

	public LuceneIndexManager getLuceneIndexManager() {

		if (this.indexManager == null) {

			final Map<String, Analyzer> fieldAnalyzers = new HashMap<String, Analyzer>();

			Analyzer searchAnalyzer = getInstanceAnalyseur(corpus
					.getAnalyseurRechercheFQCN());

			fieldAnalyzers.put(TXT_FIELDNAME, searchAnalyzer);
			fieldAnalyzers.put(TXTLEX_FIELDNAME,
					getInstanceAnalyseur(corpus.getAnalyseurLexicalFQCN()));

			final PerFieldAnalyzerWrapper perFieldAnalyzerWrapper = new PerFieldAnalyzerWrapper(
					searchAnalyzer, fieldAnalyzers);

			FSDirectory fsDirectory = null;
			LuceneIndexManager manager = null;

			try {

				String dossierData = corpus.getDossierData();

				if (dossierData != null) {

					File dossierLuceneIndex = new File(dossierData);

					if (dossierLuceneIndex.exists()) {
						fsDirectory = FSDirectory.open(dossierLuceneIndex);
						manager = new LuceneIndexManager(fsDirectory,
								perFieldAnalyzerWrapper);
					} else {
						logger.error(
								"Le dossier qui contient l'index Lucene pour le corpus {} est introuvable: {}",
								corpus, dossierLuceneIndex);
					}
				} else {
					logger.error(
							"Le dossier qui contient l'index Lucene pour le corpus {} est NULL.",
							corpus);
				}
			} catch (IOException e) {
				logger.error(
						"Erreur lors de l'ouverture du dossier de l'index Lucene [{}]",
						corpus.getDossierData(), e);
			}
			this.indexManager = manager;
		}

		return this.indexManager;
	}

	private Analyzer getInstanceAnalyseur(String analyseurFQCN) {
		try {
			Class<?> cl = Class.forName(analyseurFQCN);
			@SuppressWarnings("rawtypes")
			java.lang.reflect.Constructor co = cl
					.getConstructor(new Class[] { Version.class });
			return (Analyzer) co
					.newInstance(new Object[] { Version.LUCENE_30 });
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

	public void setTailleVoisinage(int tailleVoisinnage) {
		this.tailleVoisinage = tailleVoisinnage;
	}

	public FormeService getFormeService() {
		return formeService;
	}

	public void setFormeService(FormeService formeService) {
		this.formeService = formeService;
	}

}
