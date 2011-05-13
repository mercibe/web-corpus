package com.servicelibre.corpus.liste;

import com.servicelibre.corpus.analyzis.Catgram;
import com.servicelibre.corpus.analyzis.CatgramsPivot;
import com.servicelibre.corpus.analyzis.MotInfo;
import com.servicelibre.corpus.analyzis.MotInfo.FreqPrecision;

//TODO : lemme <> mots d'une liste (= lemme sans fréquence)
public class Lemme
{

    static CatgramsPivot catgrams = new CatgramsPivot();

    private Integer id;

    String lemme;
    Catgram catgram;
    String genre;
    double fréquence;
    MotInfo.FreqPrecision précisionFréquence;

    public Lemme(String lemme, Catgram catgram)
    {
        this(lemme, catgram, "", 0, FreqPrecision.EXACTE);
    }

    public Lemme(String lemme, String catgramId)
    {
        this(lemme, catgrams.getCatgramFromId(catgramId));
    }

    public Lemme(String lemme, Catgram catgram, String genre, double fréquence, FreqPrecision précisionFréquence)
    {
        super();
        this.lemme = lemme;
        this.catgram = catgram;
        this.genre = genre;
        this.fréquence = fréquence;
        this.précisionFréquence = précisionFréquence;
    }

    public String getLemme()
    {
        return lemme;
    }

    public void setLemme(String lemme)
    {
        this.lemme = lemme;
    }

    public Catgram getCatgram()
    {
        return catgram;
    }

    public void setCatgram(Catgram catgram)
    {
        this.catgram = catgram;
    }

    public String getGenre()
    {
        return genre;
    }

    public void setGenre(String genre)
    {
        this.genre = genre;
    }

    public double getFréquence()
    {
        return fréquence;
    }

    public void setFréquence(double fréquence)
    {
        this.fréquence = fréquence;
    }

    public MotInfo.FreqPrecision getPrécisionFréquence()
    {
        return précisionFréquence;
    }

    public void setPrécisionFréquence(MotInfo.FreqPrecision précisionFréquence)
    {
        this.précisionFréquence = précisionFréquence;
    }
    
    

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((lemme == null) ? 0 : lemme.hashCode());
        result = prime * result + ((catgram == null) ? 0 : catgram.hashCode());
        result = prime * result + ((genre == null) ? 0 : genre.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Lemme other = (Lemme) obj;

        if (lemme == null)
        {
            if (other.lemme != null) return false;
            if (catgram == null)
            {
                if (other.catgram != null) return false;
            }
            else if (!catgram.equals(other.catgram)) return false;
            if (genre == null)
            {
                if (other.genre != null) return false;
            }
            else if (!genre.equals(other.genre)) return false;
        }
        else if (!lemme.equals(other.lemme)) return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "Lemme [lemme=" + lemme + ", catgram=" + catgram + ", genre=" + genre + ", fréquence=" + fréquence
                + ", précisionFréquence=" + précisionFréquence + "]";
    }

}
