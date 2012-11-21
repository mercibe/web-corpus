package com.servicelibre.zk.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Include;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Toolbarbutton;

import com.servicelibre.controller.ServiceLocator;
import com.servicelibre.entities.ui.Onglet;
import com.servicelibre.entities.ui.Rôle;
import com.servicelibre.repositories.ui.OngletRepository;

/**
 * Démontre MVC: Autowire UI objects to data members
 * 
 * @author benoitm
 * 
 */
public class IndexCtrl extends GenericForwardComposer implements VariableResolver {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3190337135508498208L;

	// private static Logger logger = LoggerFactory.getLogger(IndexCtrl.class);
	// Collection<? extends GrantedAuthority> authorities = ssctx.getAuthentication().getAuthorities();
	// for (GrantedAuthority grantedAuthority : authorities) {
	// System.out.println("auth: " + grantedAuthority);
	// }

	Tabs corpusTabs;
	Tabpanels corpusTabpanels;

	Toolbarbutton boutonFermerTousLesOnglets;

	public void onClick$boutonFermerTousLesOnglets(Event event) {
		// Fermer tous les onglets visibles et fermables
		Tab tab = (Tab) corpusTabs.getLastChild();
		while (tab.isClosable()) {
			if (tab.isVisible()) {
				tab.close();
				tab = (Tab) corpusTabs.getLastChild();
			}
		}

		// Sélectionner le premier enfant visible
		@SuppressWarnings("unchecked")
		List<Component> tabs = (List<Component>) corpusTabs.getChildren();
		for (Component tabEnfant : tabs) {
			if (tabEnfant.isVisible()) {
				((Tab)tabEnfant).setSelected(true);
				break;
			}
		}

		boutonFermerTousLesOnglets.setVisible(false);

	}

	@Override
	public Object resolveVariable(String arg0) throws XelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		initOnglets();
	}

	public void initOnglets() {

		OngletRepository ongletRepo = ServiceLocator.getOngletRepo();

		//TODO faire un SpringSecurityHelper (celui de ZK semble ne pas fonctionner correctement)
		SecurityContext ssctx = (SecurityContext) desktop.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
		Collection<? extends GrantedAuthority> authorities =  ssctx.getAuthentication().getAuthorities();

		// Récupération des onglets à afficher
		List<Onglet> onglets = (List<Onglet>) ongletRepo.findAll(new Sort("ordre"));

		// Il faut sélectionner le premier onglet (tab) visible
		boolean premierOngletVisibleSélectionné = false;

		for (Onglet onglet : onglets) {

			// L'onglet est-il visible?
			Boolean visible = onglet.getVisible();

			// L'utilisateur courant fait-il partie du rôle auquel est éventuellement limité cet onglet?
			Rôle rôlePourVoirOnglet = onglet.getRôle();
			boolean autorisé = false;
			if (rôlePourVoirOnglet != null) {
				if (authorities.contains(new SimpleGrantedAuthority(rôlePourVoirOnglet.getNom()))) {
					autorisé = true;
				}
			} else {
				autorisé = true;
			}

			visible = visible && autorisé;

			// Créer le Tab (onglet)
			Tab newTab = new Tab();
			newTab.setVisible(visible);
			newTab.setId(onglet.getIdComposant() + "Tab");
			newTab.setLabel(onglet.getNom());
			newTab.setParent(corpusTabs);

			if (!premierOngletVisibleSélectionné && visible) {
				newTab.setSelected(visible);
				premierOngletVisibleSélectionné = true;
			}

			// Créer le TabPanel (contenu de l'onglet)
			Tabpanel newTabpanel = new Tabpanel();
			newTabpanel.setVisible(visible);
			newTabpanel.setId(onglet.getIdComposant() + "Tabpanel");
			newTabpanel.setHeight("100%");

			// Include ou Iframe?
			if (onglet.isIframe()) {
				Iframe newIframe = new Iframe(onglet.getSrc());
				newIframe.setId(onglet.getIdComposant() + "Iframe");
				newIframe.setHeight("100%");
				newIframe.setWidth("100%");
				newIframe.setVisible(visible);
				newIframe.setParent(newTabpanel);
				System.out.println("Création de l'iframe pour le composant " + onglet.getIdComposant());
			} else {
				Include newInclude = new Include(onglet.getSrc());
				newInclude.setId(onglet.getIdComposant() + "Include");
				newInclude.setHeight("100%");
				newInclude.setParent(newTabpanel);
				newInclude.setVisible(visible);
				System.out.println("Création de l'include pour le composant " + onglet.getIdComposant());
			}

			newTabpanel.setParent(corpusTabpanels);
		}
	}

}
