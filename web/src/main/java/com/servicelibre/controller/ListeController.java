package com.servicelibre.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.servicelibre.corpus.entity.Liste;
import com.servicelibre.corpus.manager.ListeManager;
import com.servicelibre.corpus.repository.ListeRepository;

@Controller
public class ListeController
{

    @Autowired
    ListeRepository listeRepo;

    @RequestMapping(value = "/liste/{id}", method = RequestMethod.GET)
    public String affiche(@PathVariable("id") long listeId, Model model)
    {

        Liste liste = listeRepo.findOne(listeId);

        System.err.println("Trouvé liste " + liste);

        model.addAttribute(liste);

        return "listeVue";
    }

}
