package com.servicelibre.corpus.analyzis;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class MotComparator<T extends MotInfo> implements Comparator<MotInfo>
{

    private Collator collator = Collator.getInstance(Locale.CANADA_FRENCH);
    
    
    @Override
    public int compare(MotInfo o1, MotInfo o2)
    {
        return collator.compare(o1.mot, o2.mot);
    }

}
