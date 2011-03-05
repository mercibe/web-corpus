package com.servicelibre.corpus.analyzis;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class CanadaFrenchStringComparator implements Comparator<String>
{

    private Collator collator = Collator.getInstance(Locale.CANADA_FRENCH);
    
    @Override
    public int compare(String s1, String s2)
    {
        return collator.compare(s1, s2);
    }

}
