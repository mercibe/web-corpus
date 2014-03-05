package com.servicelibre.zk.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.zkoss.spring.security.SecurityUtil;
import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Column;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Html;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Window;

import com.servicelibre.controller.ServiceLocator;
import com.servicelibre.entities.corpus.CatégorieListe;
import com.servicelibre.entities.corpus.Liste;
import com.servicelibre.entities.corpus.ListeMot;
import com.servicelibre.entities.corpus.Mot;
import com.servicelibre.repositories.corpus.CatégorieListeRepository;
import com.servicelibre.repositories.corpus.ListeMotRepository;
import com.servicelibre.repositories.corpus.ListeRepository;
import com.servicelibre.repositories.corpus.MotRepository;
import com.servicelibre.repositories.corpus.MotRepositoryCustom;
import com.servicelibre.repositories.corpus.MotRepositoryCustom.Condition;
import com.servicelibre.zk.controller.IndexCtrl.Mode;
import com.servicelibre.zk.controller.renderer.ListeMotRowRenderer;
import com.servicelibre.zk.recherche.Recherche;
import com.servicelibre.zk.recherche.RechercheExécution;
import com.servicelibre.zk.recherche.RechercheMot;

/**
 * Démontre MVC: Autowire UI objects to data members
 * 
 * @author benoitm
 * 
 */
public class ListeCtrl extends CorpusCtrl {

	private static Logger logger = LoggerFactory.getLogger(ListeCtrl.class);

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

	Combobox gp; // autowire car même type/ID que le composant dans la page ZUL
	Combobox condition; // autowire car même type/ID que le composant dans la
	// page ZUL

	Combobox actionCombobox;
	Combobox catégoriesListeCombobox;
	Combobox listesCombobox;
	Button actionButton;

	Grid motsGrid; // autowire car même type/ID que le composant dans la page
	// ZUL
	Column mot;

	Div api;

	Column colonnePrononciation;
	Menuitem menuitemPrononciation;

	CatégorieListeRepository catégorieListeRepo = ServiceLocator.getCatégorieListeRepo();

	ListeRepository listeRepo = ServiceLocator.getListeRepo();

	MotRepository motRepository = ServiceLocator.getMotRepo();

	ListeMotRepository listeMotRepo = ServiceLocator.getListeMotRepo();

	private static final long serialVersionUID = 779679285074159073L;

	private Column motColumn;
	
	boolean rôleAdmin = SecurityUtil.isAnyGranted("ROLE_ADMINISTRATEUR");

	public void onClick$actionButton() {
		Comboitem selectedItem = actionCombobox.getSelectedItem();
		if (selectedItem != null) {
			String action = selectedItem.getValue();
			logger.debug(" exécuter l'action demandée : {}", action);

			if (action.equals("AJOUTER_À_LA_LISTE")) {

				ajouterMotsSélectionnésÀUneListeExistante();
			} else if (action.equals("AJOUTER_UN_MOT")) {
				IndexCtrl indexCtrl = getIndexCtrl();
				indexCtrl.ouvreOngletMot(Mode.CRÉATION, null);
			} else {
				supprimerMotsSélectionnés();
				// Messagebox.show("Fonctionnalité en cours d'implémentation.",
				// "En cours...", Messagebox.OK, Messagebox.INFORMATION);
			}

		}
	}

	public IndexCtrl getIndexCtrl() {
		Window webCorpusWindow = (Window) Path.getComponent("//webCorpusPage/webCorpusWindow");
		IndexCtrl indexCtrl = (IndexCtrl) webCorpusWindow.getAttribute("$composer");
		return indexCtrl;
	}

	private void supprimerMotsSélectionnés() {

		@SuppressWarnings("unchecked")
		List<Mot> mots = (List<Mot>) motsGrid.getModel();
		List<Integer> idxSupprimés = new ArrayList<Integer>();
		for (int i = 0; i < mots.size(); i++) {
			Mot mot = mots.get(i);
			if (mot.sélectionné) {
				logger.debug("{} => {}", new Object[] { "supprimer le mot sélectionné", mot });
				idxSupprimés.add(i);
				motRepository.delete(mot);
			}
		}

		// Suppression du Grid
		for (Integer index : idxSupprimés) {
			mots.remove((int) index);
		}

		// Rafraîchir les données (page courante)
		motsGrid.setModel((ListModel<?>) mots);

	}

	private void ajouterMotsSélectionnésÀUneListeExistante() {

		Comboitem actionItem = listesCombobox.getSelectedItem();
		Liste listeSélectionnée = actionItem.getValue();

		ListModel<Mot> mots = motsGrid.getModel();
		for (int i = 0; i < mots.getSize(); i++) {
			Mot mot = mots.getElementAt(i);
			if (mot.sélectionné) {
				logger.debug("{} => {} - {}", new Object[] { "ajouter mots sélectionnés à une liste existante", mot, listeSélectionnée });
				ListeMot lm = new ListeMot(mot, listeSélectionnée);

				try {
					lm = listeMotRepo.save(lm);

					// TODO conserver la liste des mots ajoutés pour présenter
					// un beau rapport final?

				} catch (DataIntegrityViolationException e) {
					// Le mot existe déjà dans cette liste
					logger.info("Le mot {} est déjà dans la liste {}", mot, listeSélectionnée);
					// TODO conserver la liste de ces mots pour présenter un
					// beau rapport final?
				}

				// Mise à jour du modèle
				mot.sélectionné = false;
			}
		}
		// Rafraîchir les données (page courante)
		motsGrid.setModel(mots);
	}

	public void onSelect$actionCombobox() {

		Comboitem selectedItem = actionCombobox.getSelectedItem();
		if (selectedItem != null) {

			String action = selectedItem.getValue();

			// Afficher ou masquer les combobox de sélection de catérogies et
			// listes
			if (action.equals("AJOUTER_À_LA_LISTE")) {
				catégoriesListeCombobox.setVisible(true);
				listesCombobox.setVisible(true);

				if (catégoriesListeCombobox.getItemCount() == 0) {
					logger.debug("Remplir les catégories de listes si pas déjà fait");
					List<CatégorieListe> catégoriesListes = catégorieListeRepo.findAll(new Sort(new Order(Direction.ASC, "ordre"),
							new Order(Direction.ASC, "nom")));
					for (CatégorieListe catégorieListe : catégoriesListes) {
						Comboitem ci = new Comboitem(catégorieListe.getNom());
						ci.setValue(catégorieListe);
						catégoriesListeCombobox.appendChild(ci);
					}
					catégoriesListeCombobox.setSelectedIndex(0);
					onSelect$catégoriesListeCombobox();
				}

			} else {
				catégoriesListeCombobox.setVisible(false);
				listesCombobox.setVisible(false);
			}

		}
	}

	public void onSelect$catégoriesListeCombobox() {

		Comboitem selectedItem = catégoriesListeCombobox.getSelectedItem();
		if (selectedItem != null) {
			logger.debug("rafraîchir les listes pour la catégorie {}", selectedItem.getValue());

			// Vide les valeurs actuelles
			listesCombobox.getChildren().clear();
			listesCombobox.setText("");

			// Récupération des listes de la catégorie de liste sélectionnée
			CatégorieListe catégorieSélectionnée = (CatégorieListe) catégoriesListeCombobox.getSelectedItem().getValue();
			List<Liste> listesCourantes = listeRepo.findByCatégorie(catégorieSélectionnée, new Sort(new Order(Direction.ASC, "ordre"),
					new Order(Direction.ASC, "nom")));

			for (Liste listeCourante : listesCourantes) {
				Comboitem ci = new Comboitem(listeCourante.getNom());
				ci.setValue(listeCourante);
				listesCombobox.appendChild(ci);
			}
			// Se positionner sur la première liste de la catégorie si cette
			// catégorie en contient au moins une!
			if (listesCourantes.size() > 0) {
				listesCombobox.setSelectedIndex(0);
			}
		}
	}

	public void onOK$liste(Event event) {
		chercheEtAffiche(true);
	}

	public void onOK$condition(Event event) {
		chercheEtAffiche(true);
	}

	public void onOK$gp(Event event) {
		chercheEtAffiche(true);
	}

	public void onSelect$gp(Event event) {
		ajustementsGraphiePrononciation();
		condition.setFocus(true);
		condition.open();
	}

	public void onSelect$condition(Event event) {
		cherche.setFocus(true);
		cherche.select();
	}

	private void ajustementsGraphiePrononciation() {
		if (getCible() == Recherche.Cible.PRONONCIATION) {
			api.setVisible(true);
			caractèresSpéciaux.setVisible(false);
			colonnePrononciation.setVisible(true);
			menuitemPrononciation.setChecked(true);
		} else {
			api.setVisible(false);
			caractèresSpéciaux.setVisible(true);
			colonnePrononciation.setVisible(false);
			menuitemPrononciation.setChecked(false);
		}
	}

	/**
	 * Permet d'associer dynamiquement le contenu d'un composant via variable
	 * resolver et EL expression (read-only seulement)
	 */
	@Override
	public Object resolveVariable(String variableName) throws XelException {
		// if (variableName.equals("listes")) {
		// List<Liste> listes = new ArrayList<Liste>(1);
		// listes.add(new Liste("Toutes les listes", "Toutes les listes",
		// null));
		// listes.addAll(listeRepo.findByCorpusId(CORPUS_ID_PAR_DÉFAUT));
		// return listes;
		// }
		return null;
	}

	private String getInfoRésultat(Recherche recherche, int nbTrouvés) {

		StringBuilder sb = new StringBuilder();

		int nbConditions = recherche.getNombreConditions();

		String filtresActifs = "";
		if (nbConditions > 1) {
			filtresActifs = ", filtrés par les " + nbConditions + " filtres actifs";
		} else if (nbConditions == 1) {
			filtresActifs = ", filtrés par le filtre actif";
		}

		if (nbTrouvés == 0) {
			sb.append("Aucun mot trouvé (");
		} else if (nbTrouvés == 1) {
			sb.append("Un seul mot trouvé (");
		} else {
			sb.append(nbTrouvés).append(" mots trouvés (");
		}

		sb.append(recherche.getDescriptionChaîne()).append(filtresActifs).append(").");

		return sb.toString();
	}

	// private List<Mot> getMotsRecherchés() {
	//
	// List<Mot> mots = new ArrayList<Mot>();
	//
	// System.out.println(getDescriptionRecherche());
	//
	// String conditionActive = getPrécisionChaîne();
	//
	// // TODO historique des recherches
	// // créer une méthode getRecherche() et ensuite appeler
	// exécuterRecherche();
	// // Créer l'objet recherche / cette méthode devrait prendre l'objet
	// Recherche en paramètre et s'appeler exécuterRecherche()
	//
	//
	// FiltreRecherche filtres = getFiltres();
	//
	// if (getCible() == Recherche.Cible.GRAPHIE) {
	// mots = motRepository.findByGraphie(getMotCherché(),
	// MotManager.Condition.valueOf(conditionActive), filtres);
	// } else {
	// mots = motRepository.findByPrononciation(getMotCherché(),
	// MotManager.Condition.valueOf(conditionActive), filtres);
	// }
	//
	// return mots;
	// }

	public List<Mot> exécuterRecherche(Recherche recherche, boolean ajouterHistorique) {

		List<Mot> mots = new ArrayList<Mot>();

		logger.info(recherche.getDescriptionChaîne());
		Condition conditionChaîne = MotRepositoryCustom.Condition.valueOf(recherche.précisionChaîne);
		logger.debug("MotRepositoryCustom.Condition.valueOf(recherche.précisionChaîne) = " + conditionChaîne);
		logger.debug("filtres: " + recherche.filtres);

		String chaîne = recherche.getChaîne();
		switch (recherche.cible) {
		case GRAPHIE:
			mots = motRepository.findByGraphie(chaîne, conditionChaîne, recherche.filtres, rôleAdmin);
			break;
		case PRONONCIATION:
			mots = motRepository.findByPrononciation(chaîne, conditionChaîne, recherche.filtres, rôleAdmin);

			// Est-ce que la chaîne recherchée contient au moins un des caractères suivant non suivi du « combining
			// tilde » ou du « : » ?
			Pattern pattern = Pattern.compile("ɛ(?![\u0303:])|ɔ(?!\u0303)|ɑ(?!\u0303)");
			Matcher matcher = pattern.matcher(chaîne);

			// Filtrer les résultats si un des caractères problématiques non suivi d'un caractère combiné est détecté
			// dans la chaîne
			if (matcher.find()) {
				mots = filtrePrononciations(chaîne, conditionChaîne, mots);
			}

			break;
		default:
			logger.error("Cible invalide pour une recherche de mots: " + recherche.cible);
			break;
		}

		if (ajouterHistorique) {
			ajouterRechercheHistorique(recherche, mots.size());
		}

		return mots;
	}

	private List<Mot> filtrePrononciations(String chaîne, Condition conditionChaîne, List<Mot> mots) {

		List<Mot> motsFiltrés = new ArrayList<Mot>(mots.size());

		// COMBINING TILDE (̃) http://www.fileformat.info/info/unicode/char/303/index.htm

		// La chaîne devient une expression régulière
		StringBuffer regexp = new StringBuffer(chaîne.replaceAll("(ɛ(?![\u0303:]))", "(ɛ(?![\u0303:]))")
				.replaceAll("ɔ(?!\u0303)", "ɔ(?!\u0303)").replaceAll("ɑ(?!\u0303)", "ɑ(?!\u0303)"));

		// Convertir la chaîne en un pattern
		switch (conditionChaîne) {
		case COMMENCE_PAR:
			regexp.insert(0, "^\\[").append(".*");
			break;
		case CONTIENT:
			regexp.insert(0, ".*").append(".*");
			break;
		case ENTIER:
			regexp.insert(0, "^\\[").append("\\]$");
			break;
		case FINIT_PAR:
			regexp.insert(0, ".*").append("\\]$");
			break;
		default:
			break;

		}

		Pattern pattern = Pattern.compile(regexp.toString());
		Matcher matcher;

		logger.debug("Ne conserver que les prononciations qui matchent {}", regexp.toString());

		for (Mot mot : mots) {
			// FIXME: KO si prononciations multiples
			matcher = pattern.matcher(mot.getPrononciationsString());
			if (matcher.matches()) {
				motsFiltrés.add(mot);
			}
		}
		return motsFiltrés;
	}

	@Override
	public Recherche getRecherche() {

		Recherche recherche = new RechercheMot();

		// quoi chercher: mot ou prononciation
		recherche.setCible(getCible());

		// chaîne à chercher (entrée par l'utilisateur)
		recherche.setChaîne(getMotCherché());

		// Commence, termine par, etc.
		recherche.setPrécisionChaîne(getPrécisionChaîne());

		// Filtres
		recherche.setFiltres(getFiltres());

		return recherche;
	}

	private Recherche.Cible getCible() {
		return Recherche.Cible.valueOf((String) gp.getItemAtIndex(gp.getSelectedIndex()).getValue());
	}

	private String getPrécisionChaîne() {
		return (String) condition.getItemAtIndex(condition.getSelectedIndex()).getValue();
	}

	/**
	 * Retourne une phrase qui décrit les critères de recherche courant
	 * 
	 * @return
	 */
	public String getDescriptionRecherche() {
		StringBuilder desc = new StringBuilder();

		desc.append("Rechercher tous les ").append(gp.getValue());

		String aChercher = getMotCherché();

		if (!aChercher.isEmpty()) {
			desc.append(" qui ").append(condition.getValue()).append(" « ").append(aChercher).append(" »");
		}

		return desc.toString();
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {

		super.doAfterCompose(comp);

		initialiseChamps();

		initialiseMotsGrid();

		initialiseClavierPhonétique();

	}

	private void initialiseClavierPhonétique() {

		String[][] apiLettres = { { "i", "ép[i], [î]le, l[y]s, out[i]l" }, { "i:", "j[ea]n, tw[ee]d" },
				{ "y", "b[u]lle, déb[u]t, h[u]tte" }, { "u", "[ou]rs, p[ou]ls, t[ou]j[ou]rs" }, { "u:", "slow f[oo]d, p[oo]l" },
				{ "e", "ch[ez], [é]rable, hock[ey], p[é]ch[er]" }, { "ø", "bl[eu]et, h[eu]r[eux], j[eu]" },
				{ "o", "[au]t[o], b[eau], c[ô]té, sir[o]p" }, { "ɛ", "acc[è]s, [ai]mer, épin[e]tte" },
				{ "ɛ:", "bl[ê]me, c[ai]sse, m[è]tre, pr[e]sse" }, { "œ", "bonh[eu]r, jok[e]r, [oeu]f" },
				{ "ɔ", "h[o]mme, [o]béir, p[o]rt" }, { "ə", "méd[e]cin, ach[e]ter" }, { "ə̠", "caf[e]tière, f[e]nouil, just[e]ment" },
				{ "a", "[à], cl[a]v[a]rd[a]ge, p[a]tte" }, { "ɑ", "là-b[a]s, p[â]te, pyjam[a]" },
				{ "ɛ̃", "cert[ain], fr[ein], [im]pair, [in]di[en]" }, { "œ̃", "br[un], l[un]di, parf[um], [un]" },
				{ "ɔ̃", "m[on]tagnais, [om]bre, p[on]t" }, { "ɑ̃", "[an], [en], j[am]bon, s[an]g, t[em]ps" },

				{ "p", "cége[p], [p]aix, sa[p]in" }, { "t", "fourche[tt]e, pa[t]in, [th]é, [t]oit" },
				{ "k", "be[c], [ch]rome, [c]o[q], dis[qu]e, [k]aya[k]" }, { "b", "[b]ain, sno[b], ta[b]le" },
				{ "d", "bala[d]e, che[dd]ar, [d]anse" }, { "g", "al[gu]e, [g]a[g], [gu]ide" },
				{ "f", "al[ph]abet, boeu[f], e[ff]ort, [f]leuve" }, { "s", "[c]inq, for[c]e, gla[ç]on, moca[ss]in, [s]our[c]il" },
				{ "ʃ", "brun[ch], [ch]alet, é[ch]elle, [sch]éma" }, { "v", "ca[v]ité, gra[v]e, [v]ille" },
				{ "z", "bri[s]e, di[x]ième, mai[s]on, [z]énith" }, { "ʒ", "[g]enou, [j]eudi, nei[g]e" },
				{ "l", "a[l]coo[l], [l]aine, pe[ll]e" }, { "ʀ", "cou[rr]iel, fini[r], [r]ang" },
				{ "m", "alu[m]iniu[m], fe[mm]e, [m]itaine" }, { "n", "ante[nn]e, caba[n]e, [n]ord" }, { "ɲ", "bei[gn]e, campa[gn]e" },
				{ "ŋ", "bi[n]go, campi[ng], pi[ng] po[ng]" }, { "'", "sans élision ni liaison : les haches, les huit, le ouaouaron" },

				{ "j", "écureu[il], fi[ll]e, pa[y]er, r[i]en, [y]ogourt" }, { "ɥ", "ann[u]el, h[u]ile, r[u]elle" },
				{ "w", "b[oi]s, [ou]ate, [w]att" }, };

		int idCpt = 1;
		for (String[] apiLettreInfo : apiLettres) {

			Label label = new Label(apiLettreInfo[0]);
			// label.setTooltiptext(apiLettreInfo[1]);

			// Créer Popup et associer à la page

			Popup popup = new Popup();
			// popup.setWidth("300px");
			popup.setHeight("25px");
			popup.setId("api_" + idCpt);
			idCpt++;
			popup.setParent(this.webCorpusWindow);
			Html html = new Html("<div align=\"center\">" + getHtml(apiLettreInfo[1]) + "</div>");
			html.setParent(popup);

			label.setTooltip(popup.getId() + ", position=after_start, delay=50");
			label.setParent(api);
			label.setSclass("apiLettre");
			label.addEventListener(Events.ON_CLICK, new EventListener() {

				@Override
				public void onEvent(Event arg0) throws Exception {
					Label l = (Label) arg0.getTarget();
					cherche.setText(getMotCherché() + l.getValue());
					cherche.setFocus(true);
				}
			});

		}

	}

	private String getHtml(String string) {

		// FIXME faire en une opération!
		return string.replaceAll("\\[([a-zâàëèéêïîôûüùç]*)\\]", "<span style=\"color:red\">$1</span>").replaceAll("\\[", "")
				.replaceAll("\\]", "");
	}

	private void initialiseChamps() {
		Page webCorpusPage = desktop.getPage("webCorpusPage");
		webCorpusWindow = (Window) webCorpusPage.getFellow("webCorpusWindow");

		if (actionCombobox != null) {
			actionCombobox.setText("Choisir une action...");
			// actionCombobox.setSelectedIndex(0);
			// Events.postEvent("onSelect",actionCombobox, null);

		}

	}

	private void initialiseMotsGrid() {

		Recherche recherche = getRecherche();

		ListModelList modelList = new ListModelList(exécuterRecherche(recherche, false));

		motsGrid.setModel(modelList);

		infoRésultats.setValue(getInfoRésultat(recherche, modelList.size()));

		motsGrid.setRowRenderer(new ListeMotRowRenderer(this));

		// Enregistrement événement pour lien vers contextes
		motColumn = (Column) motsGrid.getColumns().getFellow("mot");

	}

	public void afficheContexte(String lemme) {

		logger.debug("Afficher les contextes de « {} »", lemme);

		Include contexteInclude = (Include) webCorpusWindow.getFellow("contexteInclude");
		Window contexteWindow = (Window) contexteInclude.getFellow("contexteWindow");

		contexteWindow.setAttribute("lemme", lemme);
		Events.sendEvent("onAfficheContexte", contexteWindow, lemme);

	}

	@SuppressWarnings("unused")
	private void displayChildren(Component comp, String sep) {
		@SuppressWarnings("unchecked")
		List<Component> children = comp.getChildren();

		System.out.println("Children of " + comp.getUuid() + "(" + comp.getId() + ")");
		for (Component component : children) {
			System.out.println(component.getUuid() + "(" + component.getId() + ")");
			displayChildren(component, sep + "\t");
		}

	}

	@Override
	public void chercheEtAffiche(boolean ajouterHistorique) {

		Recherche recherche = getRecherche();
		ListModelList<Object> modelList = new ListModelList<Object>(exécuterRecherche(recherche, ajouterHistorique));

		motsGrid.setModel(modelList);
		motsGrid.getPaginal().setActivePage(0);
		// les mots sont toujours retournés par ordre alphabétique =>
		// refléter dans la colonne (réinitialisation du marqueur de tri)
		motColumn.setSortDirection("ascending");

		infoRésultats.setValue(getInfoRésultat(recherche, modelList.size()));
	}

	@Override
	protected void initialiseRecherche() {

		gp.setSelectedIndex(0);
		condition.setSelectedIndex(0);
		cherche.setText("");
		api.setVisible(false);

		initialiseMotsGrid();

		// infoRésultats.setValue("Tous les mots (" +
		// motsGrid.getModel().getSize() + ")");

	}

	@Override
	public void initFiltreManager() {
		this.filtreManager = ServiceLocator.getListeFiltreManager();
	}

	@Override
	protected void exporterRésultatsCsv() {

		StringBuilder csv = new StringBuilder();

		String séparateur = ";";

		csv.append(getEntêteCsv(motsGrid, séparateur));

		// Récupération des données
		@SuppressWarnings("unchecked")
		List<Mot> mots = (List<Mot>) motsGrid.getModel();
		for (Mot mot : mots) {
			csv.append(ajouteGuillemetsCsv(mot.getMot())).append(séparateur);
			csv.append(ajouteGuillemetsCsv(mot.getPrononciationsString())).append(séparateur);
			csv.append("\"").append(mot.isRo() ? "*" : "").append("\"").append(séparateur);
			csv.append(ajouteGuillemetsCsv(mot.getCatgram())).append(séparateur);
			csv.append(ajouteGuillemetsCsv(mot.getGenre())).append(séparateur);
			csv.append(ajouteGuillemetsCsv(mot.getNombre())).append(séparateur);
			csv.append(ajouteGuillemetsCsv(mot.getCatgramPrécision())).append("\n");
			// FIXME
			// csv.append(ajouteGuillemetsCsv(mot.getListe().getNom())).append("\n");

		}
		Filedownload.save(csv.toString().getBytes(), "text/csv, charset=UTF-8; encoding=UTF-8", getNomFichier() + ".csv");
	}

	// TODO générer un nom de fichier qui représente la recherche
	private String getNomFichier() {
		return sdf.format(new Date()) + "-mots";
	}

	@Override
	protected void exporterRésultatsXls() {
		Workbook wb = new HSSFWorkbook();
		CreationHelper createHelper = wb.getCreationHelper();

		// Création des styles, font, etc. pour les cellules de la feuille de
		// calcul
		Font entêteFont = wb.createFont();
		entêteFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		CellStyle entêteCellStyle = wb.createCellStyle();
		entêteCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		entêteCellStyle.setFont(entêteFont);

		Font apiCellsFont = wb.createFont();
		apiCellsFont.setFontName("Arial Unicode MS");
		CellStyle apiCellsStyle = wb.createCellStyle();
		apiCellsStyle.setFont(apiCellsFont);

		// Récupération de la description de la dernière recherche
		RechercheExécution rechercheExécution = null;
		if (this.historiqueRecherche.size() > 0) {
			rechercheExécution = this.historiqueRecherche.get(0);
		} else {
			rechercheExécution = new RechercheExécution();
		}

		// Création d'une nouvelle feuille de calcul
		Sheet motsSheet = wb.createSheet("mots");

		// Récupération des données / lignes
		@SuppressWarnings("unchecked")
		List<Mot> mots = (List<Mot>) motsGrid.getModel();
		boolean exportationPartielle = false;
		// Si un mot au moins sélectionné => sélection partielle
		for (Mot mot : mots) {
			if (mot.sélectionné) {
				exportationPartielle = true;
				break;
			}
		}

		insérerTitreRecherche(wb, createHelper, rechercheExécution, motsSheet, exportationPartielle);

		// insérerFeuilleDescription(wb, createHelper, rechercheExécution);

		// Création de la ligne d'entête
		int colCpt = 0;
		int rowCpt = 2;

		org.apache.poi.ss.usermodel.Row row = motsSheet.createRow(rowCpt++);
		for (Object column : motsGrid.getColumns().getChildren()) {
			String label = ((Column) column).getLabel();
			if (label != null && !label.isEmpty()) {
				org.apache.poi.ss.usermodel.Cell cell = row.createCell(colCpt++);
				cell.setCellStyle(entêteCellStyle);
				cell.setCellValue(createHelper.createRichTextString(label));
			}
		}

		for (Mot mot : mots) {
			if (!exportationPartielle || mot.sélectionné) {
				row = motsSheet.createRow(rowCpt++);

				org.apache.poi.ss.usermodel.Cell cell = row.createCell(0);
				cell.setCellValue(createHelper.createRichTextString(mot.getMot()));

				cell = row.createCell(1);
				cell.setCellStyle(apiCellsStyle);
				cell.setCellValue(createHelper.createRichTextString(mot.getPrononciationsString() != null ? mot.getPrononciationsString()
						: ""));

				cell = row.createCell(2);
				cell.setCellValue(createHelper.createRichTextString(mot.isRo() ? "*" : ""));

				cell = row.createCell(3);
				cell.setCellValue(createHelper.createRichTextString(mot.getCatgram()));

				cell = row.createCell(4);
				cell.setCellValue(createHelper.createRichTextString(mot.getGenre() != null ? mot.getGenre() : ""));

				cell = row.createCell(5);
				cell.setCellValue(createHelper.createRichTextString(mot.getNombre() != null ? mot.getNombre() : ""));

				cell = row.createCell(6);
				cell.setCellValue(createHelper.createRichTextString(mot.getCatgramPrécision() != null ? mot.getCatgramPrécision() : ""));

				cell = row.createCell(7);
				Liste liste = mot.getListePartitionPrimaire();
				String nomListePartitionPrimaire = "";
				if (liste != null) {
					// TODO nettoyer HTML du nom de la liste (première étape:
					// supprimer toutes les balises)
					// http://apache-poi.1045710.n5.nabble.com/SHow-HTML-text-in-one-of-the-excel-cell-td2312138.html
					// http://jericho.htmlparser.net/docs/index.html
					nomListePartitionPrimaire = liste.getNom().replaceAll("<(.|\n)*?>", "");
				}
				cell.setCellValue(createHelper.createRichTextString(nomListePartitionPrimaire != null ? nomListePartitionPrimaire : ""));
			}
		}

		for (int i = 0; i <= colCpt; i++) {
			motsSheet.autoSizeColumn(i);
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			wb.write(baos);
			Filedownload.save(baos.toByteArray(), "application/vnd.ms-excel", getNomFichier() + ".xls");
			baos.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void insérerTitreRecherche(Workbook wb, CreationHelper createHelper, RechercheExécution rechercheExécution, Sheet motsSheet,
			boolean exportationPartielle) {

		// Titre de la recherche
		Font titreFont = wb.createFont();
		titreFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		CellStyle titreCellStyle = wb.createCellStyle();
		// titreCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		titreCellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		titreCellStyle.setFont(titreFont);
		titreCellStyle.setWrapText(true);

		Row titreRow = motsSheet.createRow(0);
		Cell titreCell = titreRow.createCell(0);

		titreRow.setHeightInPoints(motsSheet.getDefaultRowHeightInPoints() * 15);

		titreCell.setCellStyle(titreCellStyle);

		String descriptionTextuelle = "";

		String exportationPartielleTexte = "";
		if (exportationPartielle) {
			exportationPartielleTexte = "Une sélection de mots parmi ";
		}
		
		if (rechercheExécution.recherche == null) {
				descriptionTextuelle = exportationPartielleTexte + "tous les mots";			
		} else {
				descriptionTextuelle = exportationPartielleTexte + rechercheExécution.recherche.getDescriptionTextuelle(!exportationPartielle);
		}

		titreCell.setCellValue(createHelper.createRichTextString(descriptionTextuelle));
		motsSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
	}

	private void insérerFeuilleDescription(Workbook wb, CreationHelper createHelper, RechercheExécution rechercheExécution) {

		// Création d'une nouvelle feuille de calcul pour contenir des informations meta sur la recherche

		Sheet descriptionSheet = wb.createSheet("description de la recherche");

		org.apache.poi.ss.usermodel.Row descriptionRow = descriptionSheet.createRow(0);
		descriptionRow.createCell(0).setCellValue(createHelper.createRichTextString("description"));
		Recherche recherche = rechercheExécution.recherche;
		descriptionRow.createCell(1).setCellValue(createHelper.createRichTextString(recherche.getDescriptionTextuelle()));

		org.apache.poi.ss.usermodel.Row nbRésultatsRow = descriptionSheet.createRow(1);
		nbRésultatsRow.createCell(0).setCellValue(createHelper.createRichTextString("nombre de mots trouvés"));
		nbRésultatsRow.createCell(1).setCellValue(createHelper.createRichTextString("" + rechercheExécution.nbRésultats));

		org.apache.poi.ss.usermodel.Row dateExécutionRow = descriptionSheet.createRow(2);
		dateExécutionRow.createCell(0).setCellValue(createHelper.createRichTextString("date de l'exécution"));
		dateExécutionRow.createCell(1).setCellValue(createHelper.createRichTextString(sdf.format(rechercheExécution.dateExécution)));

		for (int i = 0; i <= 2; i++) {
			descriptionSheet.autoSizeColumn(i);
		}
	}

	@Override
	protected Grid getHistoriqueRecherchesGrid() {
		return (Grid) Path
				.getComponent("//webCorpusPage/webCorpusWindow/listeInclude/listeWindow/historiqueRechercheInclude/historiqueRecherchesGrid");
	}

	@Override
	public void chargerRecherche(Recherche recherche) {

		RechercheMot r = (RechercheMot) recherche;

		// Cible
		switch (r.cible) {
		case GRAPHIE:
			gp.setSelectedIndex(0);
			break;
		case PRONONCIATION:
			gp.setSelectedIndex(1);
			break;
		}

		ajustementsGraphiePrononciation();

		// Précision chaîne

		Condition précision = MotRepositoryCustom.Condition.valueOf(r.précisionChaîne);

		switch (précision) {
		case COMMENCE_PAR:
			condition.setSelectedIndex(0);
			break;
		case FINIT_PAR:
			condition.setSelectedIndex(1);
			break;
		case CONTIENT:
			condition.setSelectedIndex(2);
			break;
		case ENTIER:
			condition.setSelectedIndex(3);
			break;
		}

		// Chaîne
		cherche.setText(r.chaîne);

		// Filtres
		remplacerFiltres(r.getFiltres());

	}

}
