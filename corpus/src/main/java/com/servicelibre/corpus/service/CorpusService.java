package com.servicelibre.corpus.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.dom4j.CDATA;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;

import com.servicelibre.corpus.analysis.Catgram;
import com.servicelibre.corpus.analysis.MotInfo;
import com.servicelibre.corpus.lucene.InformationTerme;
import com.servicelibre.corpus.lucene.InformationTermeTextComparator;
import com.servicelibre.corpus.lucene.LuceneIndexManager;
import com.servicelibre.corpus.lucene.RésultatRecherche;
import com.servicelibre.corpus.manager.FiltreRecherche;
import com.servicelibre.corpus.metadata.Metadata;
import com.servicelibre.corpus.metadata.StringMetadata;
import com.servicelibre.entities.corpus.CatégorieListe;
import com.servicelibre.entities.corpus.Corpus;
import com.servicelibre.entities.corpus.DocMetadata;
import com.servicelibre.entities.corpus.Liste;
import com.servicelibre.repositories.corpus.CatégorieListeRepository;
import com.servicelibre.repositories.corpus.CorpusRepository;
import com.servicelibre.repositories.corpus.ListeRepository;

public class CorpusService {

	private static final String TXT_FIELDNAME = "txt";

	private static final String TXTLEX_FIELDNAME = "txtlex";

	private static Logger logger = LoggerFactory.getLogger(CorpusService.class);

	private Corpus corpus;

	private CorpusRepository corpusRepo;

	private CatégorieListeRepository catégorieListeRepo;

	private ListeRepository listeRepository;

	private LuceneIndexManager indexManager;

	private FormeService formeService;

	protected int tailleVoisinage = 10;

	public CorpusService(CorpusRepository corpusRepo, Corpus corpus) {
		this.corpusRepo = corpusRepo;
		this.corpus = ouvreOuCréeCorpus(corpusRepo, corpus);
	}

	public CorpusService(CorpusRepository corpusRepo) {
		this.corpusRepo = corpusRepo;
		this.corpus = ouvreCorpusParDéfaut(corpusRepo);
	}

	public Corpus getCorpus() {
		return this.corpus;
	}

	public CorpusRepository getCorpusRepository() {
		return corpusRepo;
	}

	public void setCorpusRepository(CorpusRepository corpusRepo) {
		this.corpusRepo = corpusRepo;
	}

	public void setCorpus(Corpus corpus) {
		this.corpus = corpus;
	}

	protected Corpus ouvreOuCréeCorpus(CorpusRepository corpusRepo, Corpus corpus) {

		Corpus corpusTrouvéOuCréé = corpusRepo.findByNom(corpus.getNom());

		if (corpusTrouvéOuCréé == null) {
			logger.info("Création du nouveau corpus " + corpus);
			try {
				corpus = corpusRepo.save(corpus);
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

	private Corpus ouvreCorpusParDéfaut(CorpusRepository corpusRepo) {

		if (corpusRepo == null) {
			return new Corpus();
		}

		Corpus corpusParDéfaut = corpusRepo.findByParDéfaut(true);

		if (corpusParDéfaut == null) {
			// Création d'un corpus vide par défaut
			String corpusIdString = "DÉMARRAGE";
			String tmpDir = System.getProperty("java.io.tmpdir") + File.separatorChar + "index-" + corpusIdString;
			corpusParDéfaut = new Corpus(corpusIdString, "Corpus de démarrage", tmpDir, "com.servicelibre.corpus.analysis.FrenchAnalyzer",
					"org.apache.lucene.analysis.standard.StandardAnalyzer");
			corpusParDéfaut.setParDéfaut(true);
			logger.info("Création du nouveau corpus " + corpusParDéfaut);
			corpusParDéfaut = corpusRepo.save(corpusParDéfaut);
			return corpusParDéfaut;
		} else {
			logger.info("Ouverture du corpus par défaut " + corpusParDéfaut);
			return corpusParDéfaut;
		}
	}

	public ContexteSet getContextesMot(String mot, int deIndex, int taillePage) {
		return getContextesMot(mot, null, deIndex, taillePage);
	}

	/**
	 * Recherche les contextes du mot donné
	 * 
	 * @param mot
	 * @param filtres
	 * @return
	 */
	public ContexteSet getContextesMot(String mot, FiltreRecherche filtres, int deIndex, int taillePage) {

		// connecter à l'index Lucene et faire la recherche
		LuceneIndexManager manager = getLuceneIndexManager();

		if (manager == null) {
			return new ContexteSet();
		}

		RésultatRecherche résultats = manager.getDocumentsWithContexts(mot, 1, tailleVoisinage, filtres, deIndex, taillePage);

		logger.info("Trouvé {} spans depuis {} sur {} spans dans {} documents", new int[]{résultats.spanCount,deIndex, résultats.nbTotalContextes,résultats.nbTotalDocs});

		ContexteSet contexteSet = getContextes(résultats);
		contexteSet.setMotCherché(mot);
		contexteSet.setFormesDuLemme(false);
		contexteSet.tailleVoisinage = tailleVoisinage;
		
		return contexteSet;
	}

	public ContexteSet getContextesLemme(String lemme, int deIndex, int taillePage) {
		return getContextesLemme(lemme, null, deIndex, taillePage);
	}

	public ContexteSet getContextesLemme(String lemme, FiltreRecherche filtres, int deIndex, int taillePage) {

		// rechercher toutes les formes du lemme
		LuceneIndexManager luceneIndexManager = getLuceneIndexManager();

		if (luceneIndexManager == null || formeService == null) {
			return new ContexteSet();
		}

		RésultatRecherche résultats = luceneIndexManager.getDocumentsWithContexts(formeService.getFormes(lemme), tailleVoisinage, filtres,
				deIndex, taillePage);

		logger.debug("Trouvé " + résultats.scoreDocs.length + "/" + résultats.nbTotalContextes + " documents (lemme) => "
				+ résultats.spanCount);

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

		Map<Integer, List<Metadata>> docMétadonnéesCache = new HashMap<Integer, List<Metadata>>();

		// Pour chaque contexte du résultat de recherche...
		for (RésultatRecherche.Contexte contexte : résultats.contextes) {
			// récupérer les métadonnées du document d'où provient le contexte


			logger.debug("Recherce les métadonnées du document {}", contexte.docId);

			List<Metadata> docMétadonnées = docMétadonnéesCache.get(contexte.docId);
			if (docMétadonnées == null) {
				docMétadonnées = getMétadonnéesDocument(contexte.docId);
			}

			// ... extraire les contextes 
			int cptContext = 1;
			Contexte ctx = new Contexte(contexte.partiesDeContexte[1], contexte.partiesDeContexte[2], contexte.partiesDeContexte[3]);
			ctx.setDocMétadonnées(docMétadonnées);
			ctx.setId(new StringBuilder().append(ctx.mot).append("_").append(contexte.docId).append("_").append(cptContext).toString());
			cptContext++;
			contextes.add(ctx);

		}

		contexteSet.setContextes(contextes);
		contexteSet.setDocumentCount(résultats.nbTotalDocs);
		contexteSet.setTotalContextesCount(résultats.nbTotalContextes);

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
				métadonnées.add(new StringMetadata(docMetadata.getNom(), champ.stringValue(), docMetadata.isPrimaire()));
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

		List<InformationTerme> topTerms = luceneIndexManager
				.getTopTerms(champIndex, new InformationTermeTextComparator<InformationTerme>());

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
						// FIXME le champ de la recherche devrait provenir du query parser / analyser
						manager = new LuceneIndexManager(fsDirectory, perFieldAnalyzerWrapper, TXT_FIELDNAME);
					} else {
						logger.error("Le dossier qui contient l'index Lucene pour le corpus {} est introuvable: {}", corpus,
								dossierLuceneIndex);
					}
				} else {
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
			return (Analyzer) co.newInstance(new Object[] { Version.LUCENE_33 });
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

	public List<CatégorieListe> getCatégorieListes() {
		return catégorieListeRepo.findByCorpus(corpus, new Sort("ordre", "nom"));
	}

	public CatégorieListeRepository getCatégorieListeRepo() {
		return catégorieListeRepo;
	}

	public ListeRepository getListeRepository() {
		return listeRepository;
	}

	public void setListeRepository(ListeRepository listeRepository) {
		this.listeRepository = listeRepository;
	}

	public void setCatégorieListeRepository(CatégorieListeRepository catégorieListeRepo) {
		this.catégorieListeRepo = catégorieListeRepo;
	}

	// FIXME
	public List<Liste> getCatégorieListeListes(CatégorieListe catégorieListe) {
		return listeRepository.findByCatégorie(catégorieListe, new Sort("ordre"));
	}

	/**
	 * À partir d'une liste de lemmes, construit un fichier XML complet avec catgrams et prononciations
	 * prêt à être importé via l'interface Web.
	 * 
	 * @throws IOException
	 */
	public void créeFichierImportationDeLemmes(String listeLemmesChemin, String formesChemin, String prononciationsChemin,
			String nomPartition, File fichierImportationFichier) throws IOException {

		PrononciationService prononciationService = new PrononciationService(prononciationsChemin);
		prononciationService.init();

		if (formeService == null) {
			formeService = new FormeService(formesChemin);
			formeService.init();
		}

		org.dom4j.Document document = DocumentHelper.createDocument();

		Element root = document.addElement("mots");

		// Ouvrir le fichier des lemmes
		InputStream listeLemmesStream = this.getClass().getClassLoader().getResourceAsStream(listeLemmesChemin);

		// Lecture des lemmes
		BufferedReader in = new BufferedReader(new InputStreamReader(listeLemmesStream, "UTF-8"));
		String ligne = null;
		while ((ligne = in.readLine()) != null) {

			List<MotInfo> lemmesInfo = formeService.getMotInfo(ligne.trim());
			System.out.println("Rechercher info catgram pour lemme " + ligne + ": " + lemmesInfo.size());
			for (MotInfo lemmeInfo : lemmesInfo) {

				if (!lemmeInfo.isLemme()) {
					System.err.println(lemmeInfo.getMot() + " n'est pas un lemme.");
					continue;
				}

				Catgram catgram = lemmeInfo.getCatgram();
				if (catgram == null || catgram.abréviation.isEmpty()) {
					System.err.println("Catgram null ou vide pour " + lemmeInfo);
					continue;
				} else {
					System.out.println("Catgram de " + lemmeInfo.getLemme() + " == " + catgram);
				}

				Element motElem = root.addElement("mot");

				motElem.addElement("motGraphie").setText(lemmeInfo.getMot());
				motElem.addElement("estGraphieRO").setText("false");
				motElem.addElement("motAutreGraphie").setText("");
				motElem.addElement("lemmeGraphie").setText(lemmeInfo.getLemme());
				motElem.addElement("estUnLemme").setText("" + lemmeInfo.isLemme());
				motElem.addElement("lemmeNote").setText("");
				motElem.addElement("motNote").setText("");

				motElem.addElement("catgramAffichage").setText(catgram.abréviation);
				motElem.addElement("catgram").setText(catgram.abréviation);

				String genre = lemmeInfo.getGenre() == null ? "" : lemmeInfo.getGenre().toString();
				motElem.addElement("genre").setText(genre);

				String nombre = lemmeInfo.getNombre() == null ? "" : lemmeInfo.getNombre().toString();
				motElem.addElement("nombre").setText(nombre);

				CDATA partitionCDATA = DocumentHelper.createCDATA(nomPartition);
				motElem.addElement("partition").add(partitionCDATA);

				Element prononciationsElem = motElem.addElement("prononciations");
				List<String> graphiePrononciations = prononciationService.getGraphiePrononciations(lemmeInfo.getLemme());
				for (String prononciation : graphiePrononciations) {
					prononciationsElem.addElement("prononciation").addElement("api").setText(prononciation);
				}

				System.out.println("\t" + lemmeInfo);
			}
		}
		in.close();
		listeLemmesStream.close();

		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(new FileWriter(fichierImportationFichier), format);
		writer.write(document);
		writer.close();
	}

}
