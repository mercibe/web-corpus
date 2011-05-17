package com.servicelibre.corpus.liste;

import java.util.ArrayList;
import java.util.List;

/**
 * mot\tlemme\tcatgram\tcatgram_precision\tgenre\tnombre\tro\tnote
 * 
 * Tous les mots de la liste sont considérés comme des lemmes (seuls les lemmes sont retenus)
 * 
 * Exemple d'entrées
 * -----------------
 * 
 * malade \t  \t  adj.
 * vite \t  \t  adv.
 * comme \t  \t  conj..
 * deux \t  \t  dét.
 * auto \t  \t n.f.
 * bars \t  \t n.m.
 * 
 * ami, amie   \t  \t     n.m., n.f.
 * il \t elle, ils, elles  \t  pron.
 * 
 * beau \t bel, belle \t adj.
 * animal \t  animaux \t n.m.
 * ne pas \t  ne...pas, n'…pas  \t  adv.
 * du \t  de l', de la, des \t  dét.
 * 
 * RO:
 * 
 * frais \t   fraîche / *fraiche  \t adj.
 * 
 * 
 * 
 * @author benoitm
 * 
 */
public class LigneMtlSplitter implements LigneSplitter
{
    private static final String SÉPARATEUR = "\\t";

    @Override
    public Mot splitLigne(String ligne, Liste liste)
    {

        String[] cols = ligne.split(SÉPARATEUR);

        nettoie(cols);
        
        String graphie = cols[0];
        String lemme = cols[0];
        boolean isLemme = true;
        String classe = cols[2];
        String catgram = "";
        String genre = "";
        String nombre = "";
        String catgramPrécision = "";
        boolean isRo = false;
        String note = "";
        
        
        // Traiter catgram_genre_nombre
        catgram = getCatgram(classe);
        genre = getGenre(classe, catgram.length());
        nombre = getNombre(classe);
        catgramPrécision = classe.replace(catgram, "").replace(genre, "").replace(nombre, "").trim();
        
        // TODO Traiter double graphie (RO ou non) : «goût / *gout» ou «paie / paye» 
        
        Mot mot = new Mot(liste, graphie, lemme, isLemme, catgram, genre, nombre, catgramPrécision, isRo, note);
        
        return mot;
    }

    private String getNombre(String classe)
    {
        String nombre = "";
       
        if(classe.indexOf("pl.") >=0 )
        {
            nombre = "pl.";
        }
        else if (classe.indexOf("inv.") >=0) {
            nombre = "inv.";
        }
       
       return nombre;
    }

    private String getGenre(String classe, int length)
    {
        String genre = "";
        
        int pos = classe.indexOf(".", length);
        
        if(pos >= 0) {
            String candidatGenre = classe.substring(length,pos+1);
            if(candidatGenre.equalsIgnoreCase("m.") || candidatGenre.equalsIgnoreCase("f.") || candidatGenre.equalsIgnoreCase("inv.")) {
                genre = candidatGenre;
            }
        }
        
        return genre;
    }

    /**
     * Tout ce qui vient avant le premier point
     * @param classe
     * @return
     */
    private String getCatgram(String classe)
    {
        return classe.substring(0,classe.indexOf(".")+1);
    }

    @Override
    public List<Mot> splitLigneMulti(String ligne, Liste liste)
    {
        List<Mot> mots = new ArrayList<Mot>(1);

        String[] cols = ligne.split(SÉPARATEUR);

        nettoie(cols);

        mots.add(new Mot(cols[0], cols[1], cols[0] == cols[1], cols[2], "", liste));

        return mots;
    }

    /**
     * Supprime les blancs inutiles
     * Ne conserve que le lemme (1er élément de la 1ère colonne, séparé par vigrule)
     * Ne conserve que la 1ère catgram (séparée par virgule)
     * @param cols
     */
    private void nettoie(String[] cols)
    {
        for (int i = 0; i < cols.length; i++)
        {
            cols[i] = cols[i].trim();
        }
        
        cols[0] = cols[0].split(",")[0].trim();
        cols[1] = "";
        cols[2] = cols[2].split(",")[0].trim();
    }
}
