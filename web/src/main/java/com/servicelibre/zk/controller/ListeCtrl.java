package com.servicelibre.zk.controller;

import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Combobox;

import com.servicelibre.controller.ServiceLocator;
import com.servicelibre.corpus.manager.ListeManager;

/**
 * Démontre MVC: Autowire UI objects to data members
 * @author benoitm
 *
 */
public class ListeCtrl extends GenericForwardComposer implements VariableResolver
{

    Combobox liste; //autowire car même type/ID que le composant dans la page ZUL
    Combobox gp;
    Combobox condition;
    Bandbox cherche;

    ListeManager listeManager = ServiceLocator.getListeManager();
    /**
     * 
     */
    private static final long serialVersionUID = 779679285074159073L;


    public void onOpen$cherche(Event event)
    {
    	chercheEtAfficheMot();
        
    }

    // Enregistrement des événements onOK (la touche ENTER) sur tous les composants de la recherche
    public void onOK$cherche(Event event)
    {
    	chercheEtAfficheMot();
    }
    public void onOK$liste(Event event)
    {
    	chercheEtAfficheMot();
    }
    public void onOK$condition(Event event)
    {
    	chercheEtAfficheMot();
    }
    public void onOK$gp(Event event)
    {
    	chercheEtAfficheMot();
    }
    
    /**
     * Permet d'associer dynamiquement le contenu d'un composant via variable resolver et EL expression (read-only seulement)
     */
	@Override
	public Object resolveVariable(String variableName) throws XelException {
		if(variableName.equals("listes")) {
			return listeManager.findByCorpusId(1);
		}
		return null;
	}

	
	public void chercheEtAfficheMot() {
		// TODO rechercher les mots de la liste courante et remplir la table
		System.out.println("Rechercher tous les " + gp.getValue() + " de la liste " + liste.getValue() + " qui " + condition.getValue() + " " + cherche.getValue() );
		
	}
	
}
