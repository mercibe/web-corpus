package com.servicelibre.zk.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Span;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.servicelibre.controller.ServiceLocator;
import com.servicelibre.corpus.manager.FiltreMot;
import com.servicelibre.corpus.service.Contexte;
import com.servicelibre.corpus.service.ContexteSet;
import com.servicelibre.corpus.service.ContexteSet.Position;
import com.servicelibre.corpus.service.CorpusPhraseService;
import com.servicelibre.corpus.service.CorpusService;
import com.servicelibre.corpus.service.InfoCooccurrent;
import com.servicelibre.corpus.service.PhraseService;

/**
 * 
 * 
 * @author benoitm
 * 
 */
public class ContexteCtrl extends CorpusCtrl {

	// private static final int CORPUS_ID_PAR_DÉFAUT = 1;

	private static final int MAX_COOCCURRENTS = 100;
	Combobox condition; // autowire car même type/ID que le composant dans la
	// page ZUL
	Textbox cherche; // autowire car même type/ID que le composant dans la page
	// ZUL
	Button boutonRecherche;
	Combobox voisinage;

	Grid contextesGrid; // autowire car même type/ID que le composant dans la
	// page ZUL

	Window contexteWindow;

	Label infoRésultats;

	A cooccurrentLien;

	PhraseService phraseService = new CorpusPhraseService();

	CorpusService corpusService = ServiceLocator.getCorpusService();

	private static final long serialVersionUID = 779679285074159073L;

	private boolean phraseComplète;
	private ContexteSet contexteSetCourant;

	// Enregistrement des événements onOK (la touche ENTER) sur tous les
	// composants de la recherche
	public void onOK$cherche(Event event) {
		chercheEtAffiche();
	}

	public void onClick$boutonRecherche(Event event) {

		chercheEtAffiche();

	}

	public void onOK$liste(Event event) {
		chercheEtAffiche();
	}

	public void onClick$cooccurrentLien(Event event) {
		afficheCooccurrents();
	}

	public void onOK$condition(Event event) {
		chercheEtAffiche();
	}

	public void onOK$gp(Event event) {
		chercheEtAffiche();
	}

	public void onAfficheContexte$contexteWindow(Event event) {

		// System.err.println(event.getName() + ": affiche contexte de " +
		// event.getData());
		String lemme = (String) contexteWindow.getAttribute("lemme");

		// Mettre le lemme dans le champ recherche
		cherche.setValue(lemme);

		// Chercher toutes les formes
		condition.setSelectedIndex(1);

		// TODO devrions-nous réinitialiser filtre ?

		// Sélectionner l'onglet Contexte
		Tab contexteTab = (Tab) webCorpusWindow.getFellow("contexteTab");
		contexteTab.setSelected(true);

		// Lancer la recherche
		chercheEtAffiche();

	}

	private String getInfoRésultat(ContexteSet contexteSet) {

		StringBuilder sb = new StringBuilder();

		// TODO rendre plus « intelligent » (cf. listes)
		String terminaison = contexteSet.size() > 1 ? "s" : "";
		sb.append(contexteSet.size())
				.append(" occurrence")
				.append(terminaison)
				.append(contexteSet.isFormesDuLemme() ? " des formes du mot « "
						: " du mot « ").append(contexteSet.getMotCherché())
				.append(" » trouvée").append(terminaison).append(" dans ")
				.append(contexteSet.getDocumentCount()).append(" document")
				.append(contexteSet.getDocumentCount() > 1 ? "s" : "")
				.append(".");

		return sb.toString();
	}

	private ContexteSet getContexteSet() {

		phraseComplète = false;

		ContexteSet contexteSet = new ContexteSet();

		String aChercher = cherche.getText();

		if (aChercher == null || aChercher.trim().isEmpty()) {
			return contexteSet;
		}

		System.out.println(getDescriptionRecherche());

		int voisinnage = Integer.parseInt(voisinage.getSelectedItem()
				.getValue().toString());
		// Recherche phrase complète
		if (voisinnage == 0) {
			phraseComplète = true;
		}

		corpusService.setTailleVoisinnage(phraseComplète ? 50 : voisinnage);

		FiltreMot filtres = getFiltres();

		// Chercher toutes les formes en fonction de condition.getValue()
		if (condition.getItemAtIndex(condition.getSelectedIndex()).getValue()
				.equals("TOUTES_LES_FORMES_DU_MOT")) {
			// FIXME quid si le mot n'est pas un lemme? Rechercher son lemme et
			// lancer la recherche?
			contexteSet = corpusService.getContextesLemme(aChercher, filtres);
		} else {
			contexteSet = corpusService.getContextesMot(aChercher, filtres);
		}

		return contexteSet;
	}

	/**
	 * Retourne une phrase qui décrit les critères de recherche courant
	 * 
	 * @return
	 */
	public String getDescriptionRecherche() {
		StringBuilder desc = new StringBuilder();

		String aChercher = cherche.getValue().trim();

		if (!aChercher.isEmpty()) {
			desc.append("Rechercher les contextes pour ").append(
					condition.getValue().toLowerCase());

			desc.append(" « ").append(aChercher).append(" »");
		}

		return desc.toString();
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		initialiseChamps();

		initialiseContexteGrid();

	}

	private void initialiseChamps() {
		Page webCorpusPage = desktop.getPage("webCorpusPage");
		webCorpusWindow = (Window) webCorpusPage.getFellow("webCorpusWindow");

	}

	private void initialiseContexteGrid() {

		contextesGrid.setModel(new ListModelList(getContexteSet()
				.getContextes()));

		contextesGrid.setRowRenderer(new RowRenderer() {

			@Override
			public void render(Row row, Object model) throws Exception {
				Contexte contexteInitial = (Contexte) model;

				if (phraseComplète) {
					contexteInitial = phraseService
							.getContextePhraseComplète(contexteInitial);
				}

				final Contexte contexte = contexteInitial;

				Span ctxSpan = new Span();
				ctxSpan.appendChild(new Label(contexte.texteAvant));

				Label mot = new Label(contexte.mot);
				mot.setTooltiptext(contexte.getId());
				mot.setStyle("font-weight: bold; cursor:hand;cursor:pointer;");

				mot.addEventListener(Events.ON_CLICK, new EventListener() {

					@Override
					public void onEvent(Event arg0) throws Exception {
						Label l = (Label) arg0.getTarget();

						créeEtAfficheOngletInfoContexte(contexte);

					}

				});

				// mot.setHeight("20px");
				ctxSpan.appendChild(mot);

				ctxSpan.appendChild(new Label(contexte.texteAprès));

				row.appendChild(ctxSpan);

				// row.appendChild(new Label(contexte.texteAvant + contexte.mot
				// + contexte.texteAprès));

			}
		});

	}

	private void afficheCooccurrents() {

		// TODO prévenir/possibilité annuler si voisinage trop grand et beaucoup de contexte: 5
		// minutes au moins!
		// conseil: réduire le voisinage à moins de 5 mots... (phrase complète)
		
		if(contexteSetCourant.getContextesSize() > 200 || contexteSetCourant.getTailleVoisinage() > 10)
		{
			System.err.println("Cela va être long...");
		}
		
		if (contexteSetCourant == null) {
			return;
		}
		
		String id = contexteSetCourant.getMotCherché() + "_" + contexteSetCourant.getTailleVoisinage();

		// TODO pour améliorer vue des cooccurrents
					/*
					 * - lemmatiser les tokens + comptage sur lemme et plus sur mot
					 * - afficher catgram du lemme + filtre sur catgram
					 * - cliquer sur un cooccurrent recherche les contextes du terme et du cooccurrent (SpanNear query custom) 
					 */
		
		Tab infoCooccurrentTab = getTabDéjàOuvert(id);

		if (infoCooccurrentTab == null) {
			infoCooccurrentTab = new Tab(id);
			infoCooccurrentTab.setId(id);
			infoCooccurrentTab.setClosable(true);
			infoCooccurrentTab.setTooltiptext(id);
			infoCooccurrentTab.setParent(corpusTabs);

			// Ajouter panel
			Tabpanel tabpanel = new Tabpanel();

			Map<String, Object> args = new HashMap<String, Object>();
			args.put("terme", contexteSetCourant.getMotCherché());
			contexteSetCourant.setMaxCooccurrent(MAX_COOCCURRENTS);
			Map<Position, List<InfoCooccurrent>> infoCooccurrents = contexteSetCourant.getInfoCooccurrents();
			args.put("infoG", infoCooccurrents.get(Position.AVANT));
			args.put("infoM", infoCooccurrents.get(Position.AVANT_APRÈS));
			args.put("infoD", infoCooccurrents.get(Position.APRÈS));

			Executions.createComponents("/infoCooccurrents.zul", tabpanel, args);

			tabpanel.setParent(corpusTabpanels);
		} else {
			System.out.println("Onglet info cooccurrent déjà ouvert: "
					+ id);
		}

		infoCooccurrentTab.setSelected(true);

		

	}

	private void créeEtAfficheOngletInfoContexte(Contexte contexte) {
		System.out.println(contexte.getDocMétadonnées());

		// Vérifier si contexte déjà ouvert

		Tab infoContexteTab = getTabDéjàOuvert(contexte.getId());

		if (infoContexteTab == null) {
			infoContexteTab = new Tab(contexte.getId());
			infoContexteTab.setId(contexte.getId());
			infoContexteTab.setClosable(true);
			infoContexteTab.setTooltiptext(contexte.getId());
			infoContexteTab.setParent(corpusTabs);

			// Ajouter panel
			Tabpanel tabpanel = new Tabpanel();

			// TODO pour améliorer vue des contextes
			/*
			 * - s'asurer que tous les contextes aient des métadonnées -
			 * naviguer au contexte suivant/précédent (cf. grid de l'onglet
			 * contexte. Via Model?) => changer id du tab aussi! - afficher
			 * numéro de ligne/contexte - mapping DB entre nom champ index
			 * Lucene et nom logique - phrases du voisinnage non nettoyées
			 * (conserver retours à la ligne, etc.) - afficher document binaire
			 * source (téléchargement) si rôle admin
			 */

			Map<String, Object> args = new HashMap<String, Object>();
			args.put("mot", contexte.mot);
			args.put("métadonnées", contexte.getDocMétadonnées());

			Contexte contexteSource = contexte.getContexteSource();
			if (contexteSource != null) {
				// La phrase complète a déjà été extraite. Il s'agit du contexte
				// lui-même
				args.put("phrase_g", contexte.texteAvant);
				args.put("phrase_m", contexte.mot);
				args.put("phrase_d", contexte.texteAprès);

				// Le voisinnage plus complet = contexteSource
				args.put("voisinnage_g", contexteSource.texteAvant);
				args.put("voisinnage_m", contexteSource.mot);
				args.put("voisinnage_d", contexteSource.texteAprès);
			} else {

				Contexte contextePhraseComplète = phraseService
						.getContextePhraseComplète(contexte);
				args.put("phrase_g", contextePhraseComplète.texteAvant);
				args.put("phrase_m", contextePhraseComplète.mot);
				args.put("phrase_d", contextePhraseComplète.texteAprès);

				args.put("voisinnage_g", contexte.texteAvant);
				args.put("voisinnage_m", contexte.mot);
				args.put("voisinnage_d", contexte.texteAprès);
			}

			Executions.createComponents("/infoContexte.zul", tabpanel, args);

			tabpanel.setParent(corpusTabpanels);
		} else {
			System.out.println("Onglet info contexte déjà ouvert: "
					+ contexte.getId());
			// Donner le focus
		}

		infoContexteTab.setSelected(true);

	}

	private Tab getTabDéjàOuvert(String id) {
		@SuppressWarnings("unchecked")
		List<Tab> children = corpusTabs.getChildren();
		for (Tab tab : children) {
			if (tab.getId().equals(id)) {
				return tab;
			}
		}
		return null;
	}

	@Override
	public void chercheEtAffiche() {
		contexteSetCourant = getContexteSet();

		contextesGrid.setModel(new ListModelList(contexteSetCourant
				.getContextes()));
		contextesGrid.getPaginal().setActivePage(0);

		infoRésultats.setValue(getInfoRésultat(contexteSetCourant));

		// mettre à jour les informations sur les résultats

		// les contextes sont toujours retournés par ordre de documents =>
		// refléter dans la colonne (réinitialisation du marqueur de tri)
		// tri ZK
		// TODO conserver le tri de l'utilisateur avant de lancer la recherche
		// et le réappliquer après
		// Column motColumn = (Column)
		// contextesGrid.getColumns().getFellow("mot");
		// motColumn.sort(true);

	}

	@Override
	public void initialiseRecherche() {
		condition.setSelectedIndex(0);
		voisinage.setSelectedIndex(0);
	}

	@Override
	public void initFiltreManager() {
		this.filtreManager = ServiceLocator.getContexteFiltreManager();

	}

}
