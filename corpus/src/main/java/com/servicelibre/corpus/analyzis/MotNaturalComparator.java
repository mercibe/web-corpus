package com.servicelibre.corpus.analyzis;

import java.util.Comparator;

public class MotNaturalComparator<T extends MotInfo> implements Comparator<MotInfo>
{

    @Override
    public int compare(MotInfo o1, MotInfo o2)
    {
        return o1.mot.compareTo(o2.mot);
    }

}
