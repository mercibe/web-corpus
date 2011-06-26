package com.servicelibre.zk.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Group;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.SimpleGroupsModel;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.servicelibre.controller.ServiceLocator;
import com.servicelibre.corpus.manager.Filtre;
import com.servicelibre.corpus.manager.FiltreMot;
import com.servicelibre.corpus.service.Contexte;
import com.servicelibre.corpus.service.CorpusService;

/**
 * 
 * 
 * @author benoitm
 * 
 */
public class ContexteCtrl extends GenericForwardComposer implements VariableResolver
{

    //private static final int CORPUS_ID_PAR_DÉFAUT = 1;

    Combobox condition; //autowire car même type/ID que le composant dans la page ZUL
    Textbox cherche; //autowire car même type/ID que le composant dans la page ZUL
    Button boutonRecherche;
    Combobox voisinage;

    Grid contextesGrid; //autowire car même type/ID que le composant dans la page ZUL

    Listbox nomFiltre; //autowire car même type/ID que le composant dans la page ZUL
    Listbox valeurFiltre;//autowire car même type/ID que le composant dans la page ZUL

    Button boutonAjoutFiltre;//autowire car même type/ID que le composant dans la page ZUL
    Grid gridFiltreActif;//autowire car même type/ID que le composant dans la page ZUL

    Window contexteWindow;

    /**
     * Permet de remplir les choix de filtres/valeurs possibles
     */
    FiltreManager filtreManager = ServiceLocator.getContexteFiltreManager();

    /**
     * Filtre qui sera passé au MotManager pour filtrer les mots à retourner. Ce
     * filtre sert également comme Model (GroupModel) pour le Grid qui affiche
     * le filtre en construction par l'utilisateur.
     */
    FiltreMot filtreActifModel = new FiltreMot();

    CorpusService corpusService = ServiceLocator.getCorpusService();

    private static final long serialVersionUID = 779679285074159073L;

    private Window webCorpusWindow;

    // Enregistrement des événements onOK (la touche ENTER) sur tous les composants de la recherche
    public void onOK$cherche(Event event)
    {
        chercheEtAfficheContextes();
    }

    public void onClick$boutonRecherche(Event event)
    {

        chercheEtAfficheContextes();

    }

    public void onOK$liste(Event event)
    {
        chercheEtAfficheContextes();
    }

    public void onOK$condition(Event event)
    {
        chercheEtAfficheContextes();
    }

    public void onOK$gp(Event event)
    {
        chercheEtAfficheContextes();
    }

    public void onAfficheContexte$contexteWindow(Event event)
    {

        //System.err.println(event.getName() + ": affiche contexte de " + event.getData());
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
        chercheEtAfficheContextes();

    }

    //    public void onSelect$liste(Event event) {
    //    	chercheEtAfficheMot();
    //    }
    //    public void onSelect$condition(Event event) {
    //    	chercheEtAfficheMot();
    //    }

    public void onClick$boutonAjoutFiltre(Event event)
    {
        Listitem filtreNomActuel = nomFiltre.getItemAtIndex(nomFiltre.getSelectedIndex());
        String nom = filtreNomActuel.getValue().toString();
        String description = filtreNomActuel.getLabel();

        Listitem filtreValeurActuel = valeurFiltre.getItemAtIndex(valeurFiltre.getSelectedIndex());
        Set<DefaultKeyValue> valeurs = new HashSet<DefaultKeyValue>(1);
        valeurs.add(new DefaultKeyValue(filtreValeurActuel.getValue().toString(), filtreValeurActuel.getLabel()));

        filtreActifModel.addFiltre(new Filtre(nom, description, valeurs));
        gridFiltreActif.setModel(new SimpleGroupsModel(filtreActifModel.getFiltreValeurs(), filtreActifModel
                .getFiltreGroupes()));

        chercheEtAfficheContextes();

    }

    public void onSelect$nomFiltre(Event event)
    {
        rafraichiValeurFiltreCourant();

    }

    public void onAfterRender$nomFiltre()
    {
        rafraichiValeurFiltreCourant();
    }

    private void rafraichiValeurFiltreCourant()
    {
        Listitem currentItem = nomFiltre.getItemAtIndex(nomFiltre.getSelectedIndex());

        if (currentItem.getValue() != null)
        {
            Set<DefaultKeyValue> filtreValeurs = filtreManager.getFiltreValeurs(currentItem.getValue().toString());
            valeurFiltre.setModel(new SimpleListModel(filtreValeurs.toArray()));
            valeurFiltre.setSelectedIndex(0);
        }
    }

    /**
     * Permet d'associer dynamiquement le contenu d'un composant via variable
     * resolver et EL expression (read-only seulement)
     */
    @Override
    public Object resolveVariable(String variableName) throws XelException
    {
        //        if (variableName.equals("listes"))
        //        {
        //            List<Liste> listes = new ArrayList<Liste>(1);
        //            listes.add(new Liste("Toutes les listes", "Toutes les listes", null));
        //            listes.addAll(listeManager.findByCorpusId(CORPUS_ID_PAR_DÉFAUT));
        //            return listes;
        //        }
        return null;
    }

    public void chercheEtAfficheContextes()
    {
        contextesGrid.setModel(new ListModelList(getContextes()));

        // les contextes sont toujours retournés par ordre de documents  => refléter dans la colonne (réinitialisation du marqueur de tri)
        // tri ZK
        // TODO conserver le tri de l'utilisateur avant de lancer la recherche et le réappliquer après
        //Column motColumn = (Column) contextesGrid.getColumns().getFellow("mot");
        //motColumn.sort(true);

    }

    private List<Contexte> getContextes()
    {

        String aChercher = cherche.getText();

        if (aChercher == null || aChercher.trim().isEmpty())
        {
            return new ArrayList<Contexte>();
        }

        System.out.println(getDescriptionRecherche());

        corpusService.setTailleVoisinnage(Integer.parseInt(voisinage.getSelectedItem().getValue().toString()));

        // TODO chercher toutes les formes en fonction de condition.getValue()

        System.err.println("condition.getValue()=" + condition.getValue());
        System.err.println("condition.getItemAtIndex(condition.getSelectedIndex()).getValue()="
                + condition.getItemAtIndex(condition.getSelectedIndex()).getValue());

        if (condition.getItemAtIndex(condition.getSelectedIndex()).getValue().equals("TOUTES_LES_FORMES_DU_MOT"))
        {
            //FIXME quid si le mot n'est pas un lemme?  Rechercher son lemme et lancer la recherche?
            System.err.println("recherche toutes les formes.....");
            return corpusService.contextesLemme(aChercher);
        }
        else
        {
            System.err.println("recherche le mot exact.....");
            return corpusService.contextesMot(aChercher);
        }
    }

    /**
     * Retourne le filtre à appliquer pour la recherche des mots
     * 
     * @return
     */
    private FiltreMot getFiltres()
    {
        FiltreMot filtres = new FiltreMot();

        // Ajout des autres filtres
        for (Filtre filtre : filtreActifModel.getFiltres())
        {
            System.err.println("Ajout du filtre utilisateur " + filtre);
            filtres.addFiltre(filtre);
        }

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

        String aChercher = cherche.getValue().trim();

        if (!aChercher.isEmpty())
        {
            desc.append("Rechercher les contextes pour ").append(condition.getValue().toLowerCase());

            desc.append(" « ").append(aChercher).append(" »");
        }

        return desc.toString();
    }

    @Override
    public void doAfterCompose(Component comp) throws Exception
    {
        super.doAfterCompose(comp);

        initialiseChamps();

        initialiseRecherche();

        initialiseContexteGrid();

        initialiseFiltre();

    }

    @Override
    public void onEvent(Event evt) throws Exception
    {
        super.onEvent(evt);
        System.err.println("ContexteCtrl" + evt.getName());
    }

    private void initialiseChamps()
    {
        Page webCorpusPage = desktop.getPage("webCorpusPage");
        webCorpusWindow = (Window) webCorpusPage.getFellow("webCorpusWindow");

    }

    private void initialiseFiltre()
    {
        // Initialisation des filtres (noms)
        nomFiltre.setModel(new SimpleListModel(filtreManager.getFiltreNoms()));

        nomFiltre.setItemRenderer(new ListitemRenderer()
        {

            @Override
            public void render(Listitem item, Object keyValue) throws Exception
            {
                DefaultKeyValue kv = (DefaultKeyValue) keyValue;
                item.setValue(kv.getKey());
                item.setLabel(kv.getValue().toString());

            }
        });

        valeurFiltre.setItemRenderer(new ListitemRenderer()
        {

            @Override
            public void render(Listitem item, Object keyValue) throws Exception
            {
                DefaultKeyValue kv = (DefaultKeyValue) keyValue;
                item.setValue(kv.getKey());
                item.setLabel(kv.getValue().toString());

            }
        });

        nomFiltre.setSelectedIndex(0);

        //DefaultKeyValue
        Object[][] data = new Object[][] {};
        Object[] heads = new Object[] {};

        gridFiltreActif.setModel(new SimpleGroupsModel(data, heads));

        gridFiltreActif.setRowRenderer(new RowRenderer()
        {

            @Override
            public void render(Row row, Object model) throws Exception
            {

                DefaultKeyValue cv = (DefaultKeyValue) model;

                final Row currentRow = row;

                Label label = new Label(cv.getValue().toString());
                label.setAttribute("key", cv.getKey());
                label.setParent(row);

                // Ajouter bouton « supprimer » si pas un groupe
                if (!(row instanceof Group))
                {

                    final Button supprimeBtn = new Button("Supprimer");
                    supprimeBtn.setMold("os");
                    supprimeBtn.setParent(row);
                    supprimeBtn.addEventListener(Events.ON_CLICK, new EventListener()
                    {

                        @Override
                        public void onEvent(Event arg0) throws Exception
                        {

                            Label labelValeur = (Label) currentRow.getFirstChild();
                            Label labelGroupe = (Label) currentRow.getGroup().getFirstChild();
                            filtreActifModel.removeFiltre(labelGroupe.getAttribute("key").toString(), labelValeur
                                    .getAttribute("key").toString());
                            gridFiltreActif.setModel(new SimpleGroupsModel(filtreActifModel.getFiltreValeurs(),
                                    filtreActifModel.getFiltreGroupes()));

                            chercheEtAfficheContextes();

                        }
                    });
                }

            }
        });

    }

    private void initialiseContexteGrid()
    {
        contextesGrid.setModel(new ListModelList(getContextes()));

        contextesGrid.setRowRenderer(new RowRenderer()
        {

            @Override
            public void render(Row row, Object model) throws Exception
            {
                Contexte contexte = (Contexte) model;

                row.appendChild(new Label(contexte.texteAvant + contexte.mot + contexte.texteAprès));

            }
        });

    }

    private void initialiseRecherche()
    {
        condition.setSelectedIndex(0);
        voisinage.setSelectedIndex(5);

    }

}
