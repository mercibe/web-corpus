package com.servicelibre.corpus.service;

public class Contexte
{
    
    
    // TODO ajouter metadonnées du document source (pour affichage/traitement ultérieur éventuel)
	// Toutes les métas ou seulement une seulement une liste finie passée en argument lors de la construction? (Map<String, String>)
    
    public String texteAvant;
    public String mot;
    public String texteAprès;
    
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
