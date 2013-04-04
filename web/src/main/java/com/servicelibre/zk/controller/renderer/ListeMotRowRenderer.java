package com.servicelibre.zk.controller.renderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.spring.security.SecurityUtil;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Window;

import com.servicelibre.entities.corpus.Liste;
import com.servicelibre.entities.corpus.Mot;
import com.servicelibre.zk.controller.IndexCtrl;
import com.servicelibre.zk.controller.IndexCtrl.Mode;
import com.servicelibre.zk.controller.ListeCtrl;

public class ListeMotRowRenderer implements RowRenderer {

    private static Logger logger = LoggerFactory.getLogger(ListeMotRowRenderer.class);

    protected ListeCtrl listeCtrl;

    private boolean rôleAdmin;

    public ListeMotRowRenderer(ListeCtrl listeCtrl) {
	super();
	this.listeCtrl = listeCtrl;
	this.rôleAdmin = SecurityUtil.isAnyGranted("ROLE_ADMINISTRATEUR");
	logger.debug("this.checkbox = " + this.rôleAdmin);
    }

    @Override
    public void render(Row row, Object model, int index) throws Exception {

	final Mot mot = (Mot) model;

	// Doit-on afficher une checkbox? (mot sélectionnable)
	if (rôleAdmin) {
	    final Checkbox cb = new Checkbox();
	    cb.setValue(mot.getId());
	    cb.setChecked(mot.sélectionné);

	    cb.addEventListener(Events.ON_CHECK, new EventListener<Event>() {

		@Override
		public void onEvent(Event arg0) throws Exception {
		    mot.sélectionné = cb.isChecked();
		}
	    });

	    row.appendChild(cb);
	}

	StringBuilder motPlus = new StringBuilder(mot.getMot());

	Label or = new Label("");
	if (mot.isRo()) {
	    or.setValue("*");
	    or.setStyle("text-align:center");
	    or.setTooltiptext("orthographe rectifiée");
	    motPlus.append(" (OR) ⇔ ").append(mot.getAutreGraphie());
	} else if (mot.getAutreGraphie() != null && !mot.getAutreGraphie().isEmpty()) {
	    motPlus.append(" ⇔ ").append(mot.getAutreGraphie()).append(" (OR)");
	}

	Label motLabel = new Label(motPlus.toString());
	motLabel.setAttribute("mot", mot.getMot());

	motLabel.setSclass("mot");

	motLabel.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

	    @Override
	    public void onEvent(Event event) throws Exception {
		Label label = (Label) event.getTarget();
		listeCtrl.afficheContexte(label.getAttribute("mot").toString());
	    }
	});

	Label prononcLabel = new Label(mot.getPrononciationsString());
	prononcLabel.setSclass("apiCrochet");

	row.appendChild(motLabel);
	row.appendChild(prononcLabel);

	row.appendChild(or);

	row.appendChild(new Label(mot.getCatgram()));
	row.appendChild(new Label(mot.getGenre()));
	row.appendChild(new Label(mot.getNombre()));
	row.appendChild(new Label(mot.getCatgramPrécision()));

	Liste liste = mot.getListePartitionPrimaire();
	Html html = new Html(liste != null ? liste.getNom() : "");
	html.setClass("z-label");
	row.appendChild(html);

	if (rôleAdmin) {
	    Label ficheMotLabel = new Label("...");
	    ficheMotLabel.setSclass("mot");
	    ficheMotLabel.setTooltiptext("Ouvrir la fiche du mot « " + mot.getMot() + " »");
	    ficheMotLabel.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

		@Override
		public void onEvent(Event arg0) throws Exception {
		    // ouvrir la fiche du mot
		    Window webCorpusWindow = (Window) Path.getComponent("//webCorpusPage/webCorpusWindow");
		    IndexCtrl indexCtrl = (IndexCtrl) webCorpusWindow.getAttribute("$composer");
		    indexCtrl.ouvreOngletMot(Mode.AFFICHAGE, mot);

		}
	    });
	    row.appendChild(ficheMotLabel);
	}
    }

}
