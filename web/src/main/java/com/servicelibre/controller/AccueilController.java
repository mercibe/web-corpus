package com.servicelibre.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.servicelibre.corpus.liste.Liste;
import com.servicelibre.corpus.manager.ListeManager;

@Controller
public class AccueilController
{

    @Autowired
    ListeManager listeManager;

    @Transactional
    @RequestMapping("/")
    public String accueil(Model model)
    {

        //TODO corpus par défaut => configuration, via nom au lieu de ID
        List<Liste> listes = listeManager.findByCorpusId(1);

        model.addAttribute("listes", listes);

        return "accueilVue";
    }

}
