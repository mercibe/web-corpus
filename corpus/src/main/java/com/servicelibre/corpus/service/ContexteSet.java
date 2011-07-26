package com.servicelibre.corpus.service;

import java.util.ArrayList;
import java.util.List;

import com.servicelibre.corpus.manager.FiltreMot;

public class ContexteSet {

    List<Contexte> contextes = new ArrayList<Contexte>();
    
    int documentCount;
    
    String motCherché = "";
    
    boolean formesDuLemme;
    
    FiltreMot filtre;

    public List<Contexte> getContextes() {
        return contextes;
    }

    public void setContextes(List<Contexte> contextes) {
        this.contextes = contextes;
    }

    public int size() {
        return contextes.size();
    }

    public int getDocumentCount() {
        return documentCount;
    }

    public void setDocumentCount(int documentCount) {
        this.documentCount = documentCount;
    }

    public String getMotCherché() {
        return motCherché;
    }

    public void setMotCherché(String motCherché) {
        this.motCherché = motCherché;
    }

    public FiltreMot getFiltre() {
        return filtre;
    }

    public void setFiltre(FiltreMot filtre) {
        this.filtre = filtre;
    }

    public boolean isFormesDuLemme() {
        return formesDuLemme;
    }

    public void setFormesDuLemme(boolean formesDuLemme) {
        this.formesDuLemme = formesDuLemme;
    }
    
    
    
    
}
