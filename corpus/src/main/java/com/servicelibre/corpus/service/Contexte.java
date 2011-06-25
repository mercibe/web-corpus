package com.servicelibre.corpus.service;

public class Contexte
{
    
    
    // TODO ajouter metadonnées du document source
    
    String texteAvant;
    String mot;
    String texteAprès;
    
    public Contexte(String texteAvant, String mot, String texteAprès)
    {
        super();
        this.texteAvant = texteAvant;
        this.mot = mot;
        this.texteAprès = texteAprès;
    }

    @Override
    public String toString()
    {
        return texteAvant + " ===> " + mot + " <=== " + texteAprès;
    }
    
    
    
}
