package com.servicelibre.zk.controller;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;

import com.servicelibre.controller.ServiceLocator;
import com.servicelibre.corpus.liste.Liste;
import com.servicelibre.corpus.manager.ListeManager;

/**
 * Démontre MVC: Autowire UI objects to data members
 * @author benoitm
 *
 */
public class ListeCtrl extends GenericForwardComposer
{

    Combobox liste; //autowire car même type/ID que le composant dans la page ZUL

    ListeManager listeManager = ServiceLocator.getListeManager();
    /**
     * 
     */
    private static final long serialVersionUID = 779679285074159073L;

    public void rempliListes()
    {

        List<Liste> listes = listeManager.findByCorpusId(1);

        for (Liste listeObject : listes)
        {
            liste.appendChild(new Comboitem(listeObject.getNom()));

        }
    }

    public void onClick$cherche(Event event)
    {
        System.err.println("Cherche!");
        rempliListes();
    }

}
