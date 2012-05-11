package com.servicelibre.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.servicelibre.corpus.entity.Corpus;
import com.servicelibre.corpus.entity.Liste;
import com.servicelibre.corpus.repository.ListeRepository;

@Controller
public class AccueilController
{

    @Autowired
    ListeRepository listeRepository;

    @Transactional
    @RequestMapping("/")
    public String accueil(Model model)
    {

        //TODO corpus par défaut => configuration, via nom au lieu de ID
        List<Liste> listes = listeRepository.findByCorpusId(1);

        model.addAttribute("listes", listes);

        return "accueilVue";
    }

}
