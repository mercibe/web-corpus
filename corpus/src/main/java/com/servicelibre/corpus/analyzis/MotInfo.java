package com.servicelibre.corpus.analyzis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.servicelibre.corpus.analyzis.Lemmatiseur.CatgramFranqus;

public class MotInfo
{
    public enum FreqPrecision
    {
        EXACTE, CALCULÉE, SANS_OBJET
    };

    public String mot;
    String lemme;
    CatgramFranqus catgram;
    double freqMot;
    double freqLemme;
    boolean isLemme;
    String note;
    FreqPrecision freqMotprecision;
    FreqPrecision freqLemmePrecision;

    // Ajouter un tableau de genre/nombre + mode/temps/personne


    public MotInfo(String mot)
    {
        this(mot, null, null, null);
    }

    public MotInfo(String mot, String lemme, CatgramFranqus catgram, String note)
    {
        this(mot,lemme, catgram, note, 0, 0, false);
    }
    
    public MotInfo(String mot, String lemme, CatgramFranqus catgram, String note, double freqMot, double freqLemme, boolean isLemme)
    {
        this.mot = mot;
        this.lemme = lemme;
        this.catgram = catgram;
        this.note = note;
        this.freqMot = freqMot;
        this.freqLemme = freqLemme;
        this.isLemme = isLemme;
    }
    

    public MotInfo()
    {
        super();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append(mot).append("|").append(lemme)
            .append("|").append(catgram)
            .append("|").append(freqMot)
            .append("|").append(freqLemme)
            .append("|").append(isLemme);

        return sb.toString();
    }

    public String getMot()
    {
        return mot;
    }

    public MotInfo setMot(String mot)
    {
        this.mot = mot;
        return this;
    }

    public String getLemme()
    {
        return lemme;
    }

    public MotInfo setLemme(String lemme)
    {
        this.lemme = lemme;
        return this;
    }

    public CatgramFranqus getCatgram()
    {
        return catgram;
    }

    public MotInfo setCatgram(CatgramFranqus catgram)
    {
        this.catgram = catgram;
        return this;
    }

    public FreqPrecision getFreqMotPrecision()
    {
        return freqMotprecision;
    }

    public MotInfo setFreqMotPrecision(FreqPrecision freqMotPrecision)
    {
        this.freqMotprecision = freqMotPrecision;
        return this;
    }
    
    

    public FreqPrecision getFreqLemmePrecision()
    {
        return freqLemmePrecision;
    }

    public void setFreqLemmePrecision(FreqPrecision freqLemmePrecision)
    {
        this.freqLemmePrecision = freqLemmePrecision;
    }

    public double getFreqMot()
    {
        return freqMot;
    }

    public MotInfo setFreqMot(double freqMot)
    {
        this.freqMot = freqMot;
        return this;
    }

    public double getFreqLemme()
    {
        return freqLemme;
    }

    public MotInfo setFreqLemme(double freqLemme)
    {
        this.freqLemme = freqLemme;
        return this;
    }

    public boolean isLemme()
    {
        return isLemme;
    }

    public MotInfo setLemme(boolean isLemme)
    {
        this.isLemme = isLemme;
        return this;
    }

    public String getNote()
    {
        return note;
    }

    public void setNote(String note)
    {
        this.note = note;
    }

    public static void dumpMotInfos(List<MotInfo> motInfoList, String dumpFilename)
    {
        File dumpFile = new File(dumpFilename);
        try
        {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dumpFile), "UTF-8"));
            for (MotInfo motInfo : motInfoList)
            {
                writer.append(motInfo.toString());
                writer.newLine();
            }
            writer.close();

        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }   
    
    public static void dumpMotInfosList(List<List<MotInfo>> motInfoList, String dumpFilename)
    {
        File dumpFile = new File(dumpFilename);
        try
        {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dumpFile), "UTF-8"));
            for (List<MotInfo> motInfo : motInfoList)
            {
                for (MotInfo info : motInfo)
                {
                    writer.append(info.toString());
                    writer.newLine();
                }
            }
            writer.close();

        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((catgram == null) ? 0 : catgram.hashCode());
        result = prime * result + ((lemme == null) ? 0 : lemme.hashCode());
        result = prime * result + ((mot == null) ? 0 : mot.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        
        MotInfo other = (MotInfo) obj;
        
        if (catgram == null)
        {
            if (other.catgram != null) return false;
        }
        else if (!catgram.equals(other.catgram)) return false;
        
        if (lemme == null)
        {
            if (other.lemme != null) return false;
        }
        else if (!lemme.equals(other.lemme)) return false;
        
        if (mot == null)
        {
            if (other.mot != null) return false;
        }
        else if (!mot.equals(other.mot)) return false;
        
        return true;
    }    
    
    
    

}
