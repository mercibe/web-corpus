package com.servicelibre.zk.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Column;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Html;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Window;

import com.servicelibre.controller.ServiceLocator;
import com.servicelibre.corpus.entity.Mot;
import com.servicelibre.corpus.manager.FiltreMot;
import com.servicelibre.corpus.manager.ListeManager;
import com.servicelibre.corpus.manager.MotManager;
import com.servicelibre.corpus.manager.PrononciationManager;

/**
 * Démontre MVC: Autowire UI objects to data members
 * 
 * @author benoitm
 * 
 */
public class ListeCtrl extends CorpusCtrl {

	private static Logger logger = LoggerFactory.getLogger(ListeCtrl.class);

	Combobox gp; // autowire car même type/ID que le composant dans la page ZUL
	Combobox condition; // autowire car même type/ID que le composant dans la
	// page ZUL

	Grid motsGrid; // autowire car même type/ID que le composant dans la page
	// ZUL
	Column mot;

	Div api;

	Column colonnePrononciation;
	Menuitem menuitemPrononciation;

	ListeManager listeManager = ServiceLocator.getListeManager();

	MotManager motManager = ServiceLocator.getMotManager();
	PrononciationManager prononciationManager = ServiceLocator.getPrononciationManager();

	private static final long serialVersionUID = 779679285074159073L;

	private Column motColumn;

	public void onOK$liste(Event event) {
		chercheEtAffiche();
	}

	public void onOK$condition(Event event) {
		chercheEtAffiche();
	}

	public void onOK$gp(Event event) {
		chercheEtAffiche();
	}

	public void onSelect$gp(Event event) {
		//
		if (getGpActif().equals("p")) {
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
		// listes.addAll(listeManager.findByCorpusId(CORPUS_ID_PAR_DÉFAUT));
		// return listes;
		// }
		return null;
	}

	private String getInfoRésultat(ListModelList modelList) {

		StringBuilder sb = new StringBuilder();

		String conditionActive = condition.getItemAtIndex(condition.getSelectedIndex()).getLabel();

		String prononc = getGpActif().equals("p") ? " la prononciation" : "";

		String motCherché = getMotCherché();

		if (motCherché != null && motCherché.isEmpty()) {
			String terminaison = modelList.size() > 1 ? "s" : "";
			sb.append(modelList.size()).append(" mot").append(terminaison).append(" trouvé").append(terminaison).append(".");
		} else {
			if (modelList.size() == 0) {
				sb.append(" Aucun mot ne ").append(conditionActive.replace("ent ", "e ")).append(prononc).append(" « ").append(motCherché).append(" ».");
			} else if (modelList.size() == 1) {
				sb.append(" Un seul mot ").append(conditionActive.replace("nnent ", "nt ").replace("dent ", "d ").replace("ent ", "e ")).append(prononc)
						.append(" « ").append(motCherché).append(" ».");
			} else {
				sb.append(modelList.size()).append(" mots").append(" ").append(conditionActive).append(prononc).append(" « ").append(motCherché).append(" ».");
			}
		}

		return sb.toString();
	}

	private List<Mot> getMotsRecherchés() {
		
		List<Mot> mots = new ArrayList<Mot>();

		System.out.println(getDescriptionRecherche());

		String conditionActive = getConditionActive();

		// TODO historique des recherches

		String gpActif = getGpActif();

		FiltreMot filtres = getFiltres();

		if (gpActif.equals("g")) {
			mots = motManager.findByGraphie(getMotCherché(), MotManager.Condition.valueOf(conditionActive), filtres);
		} else {
			mots = motManager.findByPrononciation(getMotCherché(), MotManager.Condition.valueOf(conditionActive), filtres);

			// try
			// {
			// Messagebox.show("La recherche par phonème n'est pas encore implémentée.",
			// "Corpus", Messagebox.OK,
			// Messagebox.INFORMATION);
			// }
			// catch (InterruptedException e)
			// {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}

		return mots;
	}

	private String getGpActif() {
		return (String) gp.getItemAtIndex(gp.getSelectedIndex()).getValue();
	}

	private String getConditionActive() {
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

		String[][] apiLettres = { { "i", "ép[i], [î]le, l[y]s, out[il]" }, { "i:", "j[ea]n, tw[ee]d" }, { "y", "[hu]tte, b[u]lle, f[ût]" },
				{ "u", "[ou]rs, t[ou]ndra, p[ouls]" }, { "u:", "slow f[oo]d, p[oo]l" }, { "e", "(e fermé) [é]rable, p[é]ch[er], ch[ez], hock[ey]" },
				{ "ø", "(eu fermé)j[eu], heu]r[eux], bl[eu]et" }, { "o", "(o fermé) [au]to, c[ô]té, b[eau], sir[op]" },
				{ "ɛ", "(e ouvert) [ai]mer, épin[e]tte, acc[ès]" }, { "ɛ:", "bl[ê]me, c[ai]sse, g[è]ne, m[è]tre, par[aî]tre, pr[e]sse" },
				{ "œ", "(eu ouvert) n[eu]f, [oeu]f, bonh[eu]r, gold[e]n, jok[e]r" }, { "ɔ", "(o ouvert) [o]béir, [au]t[o]chtone, p[o]rt" },
				{ "ə", "(e caduc, ou muet)m[e]ner, crén[e]lage" }, { "ə̠", "f[e]nouil, caf[e]tière, just[e]ment" },
				{ "a", "(a antérieur) [à], cl[a]v[a]rd[a]ge, p[a]tte" }, { "ɑ", "(a postérieur) là-b[as], p[â]te, cip[ai]lle, pyjam[a]" },
				{ "ɛ̃", "br[in], [im]pair, [in]d[ie]n, cert[ain], fr[ein]" }, { "œ̃", "[un], l[un]di, br[un], parf[um]" },
				{ "ɔ̃", "m[on]tagnais, [om]ble, p[ont]" }, { "ɑ̃", "[an], [en], j[am]bon, s[ang], t[emps]" },

				{ "p", "[p]aix, sa[p]in, cége[p]" }, { "t", "[t]oit, [th]é, pa[t]in, a[tt]aché, fourche[tt]e" },
				{ "k", "[c]oq, [ch]rome, be[c], dis[qu]e, [k]aya[k]" }, { "b", "[b]oréal, ta[b]lée, sno[b]" }, { "d", "[d]anse, che[dd]ar, bala[d]e, ble[d]" },
				{ "g", "[g]a[g], al[gu]e, [gu]ide" }, { "f", "[f]leuve, al[ph]abet, e[ff]ort, boeu[f]" },
				{ "s", "[s]our[c]il, [c]inq, for[c]e, moca[ss]in, gla[ç]on" }, { "ʃ", "[ch]alet, [sch]éma, é[ch]elle, brun[ch]" },
				{ "v", "[v]ille, ca[v]ité, dra[v]e" }, { "z", "mai[s]on, [z]énith, di[x]ième, bri[s]e" }, { "ʒ", "[j]eudi, [g]iboulée, nei[g]e" },
				{ "l", "[l]aine, a[l]coo[l], pe[ll]e" }, { "ʀ", "[r]ang, cou[rr]iel, fini[r]" }, { "m", "[m]itaine, fe[mm]e, alu[m]iniu[m]" },
				{ "n", "[n]ordet, ante[nn]e, caba[n]e" }, { "ɲ", "bei[gn]e, campa[gn]e" },
				{ "ŋ", "big ba[ng], camp[ing], flame[n]co, bi[n]go, pi[ng] p[ong]" },
				{ "'", "les [h]aches, un [h]uit, la [ou]ananiche, les [u]nes (sans élision ni liaison)" },

				{ "j", "r[i]en, pa[y]er, écureu[il], fi[ll]e, [y]ogourt" }, { "ɥ", "l[u]i, [hu]issier, t[u]ile" }, { "w", "l[ou]er, [ou]ate, [w]att, b[o]is" }, };

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
		return string.replaceAll("\\[([a-zâàëèéêïîôûü]*)\\]", "<span style=\"color:red\">$1</span>").replaceAll("\\[", "").replaceAll("\\]", "");
	}

	private void initialiseChamps() {
		Page webCorpusPage = desktop.getPage("webCorpusPage");
		webCorpusWindow = (Window) webCorpusPage.getFellow("webCorpusWindow");

	}

	private void initialiseMotsGrid() {
		motsGrid.setModel(new ListModelList(getMotsRecherchés()));

		motsGrid.setRowRenderer(new RowRenderer() {

			@Override
			public void render(Row row, Object model) throws Exception {
				Mot mot = (Mot) model;

				Label motLabel = new Label(mot.getMot());

				motLabel.setSclass("mot");

				motLabel.addEventListener(Events.ON_CLICK, new EventListener() {

					@Override
					public void onEvent(Event event) throws Exception {
						Label label = (Label) event.getTarget();
						afficheContexte(label.getValue());
					}
				});

				Label prononcLabel = new Label(mot.getPrononciationsString());
				prononcLabel.setSclass("apiCrochet");

				row.appendChild(motLabel);
				row.appendChild(prononcLabel);

				Label or = new Label("");
				if (mot.isRo()) {
					or.setValue("*");
					or.setStyle("text-align:center");
					or.setTooltiptext("orthographe rectifiée");
				}

				row.appendChild(or);

				row.appendChild(new Label(mot.getCatgram()));
				row.appendChild(new Label(mot.getGenre()));
				row.appendChild(new Label(mot.getNombre()));
				row.appendChild(new Label(mot.getCatgramPrésicion()));
				row.appendChild(new Label(mot.getListe().getNom()));

			}
		});

		// Enregistrement événement pour lien vers contextes
		motColumn = (Column) motsGrid.getColumns().getFellow("mot");

	}

	private void afficheContexte(String lemme) {

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
	public void chercheEtAffiche() {
		
		ListModelList modelList = new ListModelList(getMotsRecherchés());

		motsGrid.setModel(modelList);
		motsGrid.getPaginal().setActivePage(0);
		// les mots sont toujours retournés par ordre alphabétique =>
		// refléter dans la colonne (réinitialisation du marqueur de tri)
		motColumn.setSortDirection("ascending");

		infoRésultats.setValue(getInfoRésultat(modelList));
	}

	@Override
	protected void initialiseRecherche() {
		gp.setSelectedIndex(0);
		condition.setSelectedIndex(0);
		cherche.setText("");
		api.setVisible(false);

		infoRésultats.setValue("");
		initialiseMotsGrid();

	}

	@Override
	public void initFiltreManager() {
		this.filtreManager = ServiceLocator.getListeFiltreManager();

	}

}
