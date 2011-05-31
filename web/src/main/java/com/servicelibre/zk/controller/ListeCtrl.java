package com.servicelibre.zk.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.GroupsModel;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.SimpleListModel;

import com.servicelibre.controller.ServiceLocator;
import com.servicelibre.corpus.liste.Liste;
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

    Combobox liste; //autowire car même type/ID que le composant dans la page ZUL

    Combobox gp; //autowire car même type/ID que le composant dans la page ZUL
    Combobox condition; //autowire car même type/ID que le composant dans la page ZUL

    Bandbox cherche; //autowire car même type/ID que le composant dans la page ZUL
    Grid motsGrid; //autowire car même type/ID que le composant dans la page ZUL

    Listbox nomFiltre; //autowire car même type/ID que le composant dans la page ZUL
    Listbox valeurFiltre;//autowire car même type/ID que le composant dans la page ZUL

    Button boutonAjoutFiltre;//autowire car même type/ID que le composant dans la page ZUL
    Grid gridFiltreActif;//autowire car même type/ID que le composant dans la page ZUL

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

    public void onClick$boutonAjoutFiltre(Event event)
    {
        System.err.println("Ajouter le filtre courant: "
                + nomFiltre.getItemAtIndex(nomFiltre.getSelectedIndex()).getValue() + " = "
                + valeurFiltre.getItemAtIndex(valeurFiltre.getSelectedIndex()).getValue());
     
        // FIXME jouer sur le modèle et utiliser setModel / Renderer
        // Le modèle pourrait être directement un héritage de FiltreMot qui implémenterait/hériterait de ListModel
        
        Row r = new Row();
        r.appendChild(new Label(nomFiltre.getItemAtIndex(nomFiltre.getSelectedIndex()).getValue().toString()));
        r.appendChild(new Label(valeurFiltre.getItemAtIndex(valeurFiltre.getSelectedIndex()).getValue().toString()));
        final Div d = new Div(); 
        Button button = new Button("Supprimer");
        button.setParent(d);
        button.addEventListener(Events.ON_CLICK, new EventListener()
        {
            
            @Override
            public void onEvent(Event event) throws Exception
            {
                System.err.println(event);
            }
        });
        r.appendChild(d);
        
        gridFiltreActif.getRows().appendChild(r);
//        GroupsModel groupsModel = gridFiltreActif.getGroupsModel();
//        int groupCount = groupsModel.getGroupCount();
//        for(int i=0;i < groupCount; i++) {
//            Object group = groupsModel.getGroup(i);
//            System.err.println("group " + i + " du type " + group.getClass().getName());
//        }
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
            List<DefaultKeyValue> filtreValeurs = filtresManager.getFiltreValeurs(currentItem.getValue().toString());
            valeurFiltre.setModel(new SimpleListModel(filtreValeurs));
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
        if (variableName.equals("listes"))
        {
            List<Liste> listes = new ArrayList<Liste>(1);
            listes.add(new Liste("Toutes les listes", "Toutes les listes", null));
            listes.addAll(listeManager.findByCorpusId(CORPUS_ID_PAR_DÉFAUT));
            return listes;
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
        // TODO historique des recherches

        String gpActif = (String) gp.getItemAtIndex(gp.getSelectedIndex()).getValue();
        if (gpActif.equals("g"))
        {
            FiltreMot filtres = getFiltres();
            mots = motManager.findByGraphie(cherche.getValue(), MotManager.Condition.valueOf(conditionActive), filtres);
        }
        else
        {
            try
            {
                Messagebox.show("La recherche par phonème n'est pas encore implémentée.", "Corpus", Messagebox.OK,
                        Messagebox.INFORMATION);
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
        int selectedIndex = liste.getSelectedIndex();
        if (selectedIndex > 0)
        {
            Long listeActive = (Long) liste.getItemAtIndex(selectedIndex).getValue();
            filtres.addFiltre(FiltreMot.CléFiltre.liste, new Long[] { listeActive });
        }
        
        // Récupération des autres filtres actifs
        for (Iterator<Row> it = gridFiltreActif.getRows().getChildren().iterator(); it.hasNext();)
        {
            Row row = it.next();
            System.err.println("TODO: récupérer attribut et créer filtre");
            //row.getAttribute(name);
            //filtres.addFiltre(FiltreMot.CléFiltre.valueOf(), valeurs)
            
            
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

        desc.append("Rechercher tous les ").append(gp.getValue()).append(" de la liste « ").append(liste.getValue())
                .append(" »");

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

        liste.setSelectedIndex(0);
        gp.setSelectedIndex(0);

        condition.setSelectedIndex(0);

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
                row.appendChild(new Label(mot.getListe().getNom()));

            }
        });

        // Initialisation des filtres (noms)
        nomFiltre.setModel(new SimpleListModel(filtresManager.getFiltreNoms()));

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

    }

}
