package com.servicelibre.zk.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.SimpleListModel;

import com.servicelibre.controller.ServiceLocator;
import com.servicelibre.corpus.liste.Mot;
import com.servicelibre.corpus.manager.FiltreMot;
import com.servicelibre.corpus.manager.ListeManager;
import com.servicelibre.corpus.manager.MotManager;

/**
 * Démontre MVC: Autowire UI objects to data members
 * 
 * @author benoitm
 * 
 */
public class ListeCtrl extends GenericForwardComposer implements VariableResolver
{

    private static final int CORPUS_ID_PAR_DÉFAUT = 1;
    
    
    Combobox gp; //autowire car même type/ID que le composant dans la page ZUL
    Combobox condition; //autowire car même type/ID que le composant dans la page ZUL
    
    Bandbox cherche; //autowire car même type/ID que le composant dans la page ZUL
    Grid motsGrid; //autowire car même type/ID que le composant dans la page ZUL
    
    Combobox nomFiltre;
    Combobox valeurFiltre;

    ListeManager listeManager = ServiceLocator.getListeManager();
    MotManager motManager = ServiceLocator.getMotManager();
    ListeMotFiltre filtresManager = ServiceLocator.getListeMotFiltre();

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
    
    public void onChange$nomFiltre(Event event)
    {
        System.err.println("Remplir les valeurs pour " + nomFiltre.getValue());
        
        // Vider la liste des valeurs
        List<DefaultKeyValue> filtreValeurs = filtresManager.getFiltreValeurs(nomFiltre.getValue());
        //FIXME !
        valeurFiltre.setModel(new SimpleListModel(filtreValeurs));
        
    }
    
    

    /**
     * Permet d'associer dynamiquement le contenu d'un composant via variable
     * resolver et EL expression (read-only seulement)
     */
    @Override
    public Object resolveVariable(String variableName) throws XelException
    {
        if (variableName.equals("listes"))
        {
            return listeManager.findByCorpusId(CORPUS_ID_PAR_DÉFAUT);
        }
        return null;
    }

    public void chercheEtAfficheMot()
    {
        motsGrid.setModel(new ListModelList(getMotsRecherchés()));

    }

    private List<Mot> getMotsRecherchés()
    {
        List<Mot> mots = new ArrayList<Mot>();

        // TODO rechercher les mots de la liste courante et remplir la table
        System.out.println(getDescriptionRecherche());

        
        String conditionActive = (String) condition.getItemAtIndex(condition.getSelectedIndex()).getValue();
        
        // TODO ajouter filtre pour liste et corpus
        // TODO sort sur colonnes
        // TODO ajout filtre user pour catgram, genre, nombre, etc.
        // TODO historique des recherches
        
        String gpActif = (String) gp.getItemAtIndex(gp.getSelectedIndex()).getValue();
        if(gpActif.equals("g"))
        {
            FiltreMot filtres = getFiltres() ;
            mots = motManager.findByGraphie(cherche.getValue(), MotManager.Condition.valueOf(conditionActive), filtres);
        }
        else {
            try
            {
                Messagebox.show("La recherche par phonème n'est pas encore implémentée.", "Corpus", Messagebox.OK, Messagebox.INFORMATION);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return mots;
    }

    private FiltreMot getFiltres()
    {
        FiltreMot filtres = new FiltreMot();
        
        // Récupération de la liste active
//        Long listeActive = (Long)liste.getItemAtIndex(liste.getSelectedIndex()).getValue();
//        
//        filtres.addFiltre(FiltreMot.CléFiltre.liste, new Long[]{listeActive});
        
        
        return filtres;
    }

    /**
     * Retourne une phrase qui décrit les critères de recherche courant
     * 
     * @return
     */
    public String getDescriptionRecherche()
    {
        StringBuilder desc = new StringBuilder();

        desc.append("Rechercher tous les ").append(gp.getValue()).append(" de la liste « ").append("TODO:liste des listes").append(" »");

        String aChercher = cherche.getValue().trim();

        if (!aChercher.isEmpty())
        {
            desc.append(" qui ").append(condition.getValue()).append(" « ").append(aChercher).append(" »");
        }

        return desc.toString();
    }

    @Override
    public void doAfterCompose(Component comp) throws Exception
    {
        super.doAfterCompose(comp);
        
        gp.setSelectedIndex(0);
        
        condition.setSelectedIndex(0);
        
        
        // Initialisation des filtres (noms)
        Set<String> filtreNoms = filtresManager.getFiltreNoms();
        for (String filtre : filtreNoms)
        {
            System.err.println("filtre: " + filtre);
            nomFiltre.appendItem(filtre);
        }
        
        
        motsGrid.setModel(new ListModelList(getMotsRecherchés()));
        
        motsGrid.setRowRenderer(new RowRenderer()
        {

            @Override
            public void render(Row row, Object model) throws Exception
            {
                Mot mot = (Mot) model;

                row.appendChild(new Label(mot.getMot()));
                row.appendChild(new Label(mot.getCatgram()));
                row.appendChild(new Label(mot.getGenre()));
                row.appendChild(new Label(mot.getNombre()));
                row.appendChild(new Label(mot.getCatgramPrésicion()));

            }
        });
        
        
        
        //valeurFiltre.
        
    }

}
