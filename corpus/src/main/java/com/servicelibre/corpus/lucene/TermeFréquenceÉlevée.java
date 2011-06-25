/**
 * Librairie de code pour la création d'index textuels
 *
 * Copyright (C) 2009 Benoit Mercier <benoit.mercier@servicelibre.com> — Tous droits réservés.
 *
 * Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le
 * modifier suivant les termes de la “GNU General Public License” telle que
 * publiée par la Free Software Foundation : soit la version 3 de cette
 * licence, soit (à votre gré) toute version ultérieure.
 *
 * Ce programme est distribué dans l’espoir qu’il vous sera utile, mais SANS
 * AUCUNE GARANTIE : sans même la garantie implicite de COMMERCIALISABILITÉ
 * ni d’ADÉQUATION À UN OBJECTIF PARTICULIER. Consultez la Licence Générale
 * Publique GNU pour plus de détails.
 *
 * Vous devriez avoir reçu une copie de la Licence Générale Publique GNU avec
 * ce programme ; si ce n’est pas le cas, consultez :
 * <http://www.gnu.org/licenses/>.
 */

package com.servicelibre.corpus.lucene;

import java.io.File;
import java.util.Hashtable;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.PriorityQueue;

/**
 * <code>HighFreqTerms</code> class extracts terms and their frequencies out of
 * an existing Lucene index.
 * 
 * @version $Id: HighFreqTerms.java,v 1.2 2003/11/08 10:55:40 Administrator Exp
 *          $
 */
public class TermeFréquenceÉlevée
{
    public static int defaultNumTerms = 100;

    public static void main(String[] args) throws Exception
    {
        Directory dir = FSDirectory.open(new File(args[0]));
        
        System.err.println(args[0]);
        InformationTerme[] terms = getTermesFréquenceÉlevée(IndexReader.open(dir, true), null, new String[] { "source", "auteur", "série", "type"  });
        for (int i = 0; i < terms.length; i++)
        {
            System.out.println(i + ".\t" + terms[i].term + "\t" + terms[i].docFreq);
        }
    }

    public static InformationTerme[] getTermesFréquenceÉlevée(IndexReader ir, Hashtable<String, ?> junkWords, String[] fields)
            throws Exception
    {
        return getTermesFréquenceÉlevée(ir, junkWords, defaultNumTerms, fields);
    }

    public static InformationTerme[] getTermesFréquenceÉlevée(IndexReader reader, Hashtable<String, ?> junkWords, int numTerms,
            String[] fields) throws Exception
    {
        if (reader == null || fields == null) return null;
        TermInfoQueue tiq = new TermInfoQueue(numTerms);
        TermEnum terms = reader.terms();

        int minFreq = 0;
        while (terms.next())
        {
            String field = terms.term().field();
            if (fields != null && fields.length > 0)
            {
                boolean skip = true;
                for (int i = 0; i < fields.length; i++)
                {
                    if (field.equals(fields[i]))
                    {
                        skip = false;
                        break;
                    }
                }
                if (skip) continue;
            }
            if (junkWords != null && junkWords.get(terms.term().text()) != null) continue;
            if (terms.docFreq() > minFreq)
            {
                tiq.add(new InformationTerme(terms.term(), terms.docFreq()));
                if (tiq.size() >= numTerms) // if tiq overfull
                {
                    tiq.pop(); // remove lowest in tiq
                    minFreq = ((InformationTerme) tiq.top()).docFreq; // reset minFreq
                }
            }
        }
        InformationTerme[] res = new InformationTerme[tiq.size()];
        for (int i = 0; i < res.length; i++)
        {
            res[res.length - i - 1] = (InformationTerme) tiq.pop();
        }
        return res;
    }
}

final class TermInfoQueue extends PriorityQueue<Object>
{
    TermInfoQueue(int size)
    {
        initialize(size);
    }

    protected final boolean lessThan(Object a, Object b)
    {
        InformationTerme termInfoA = (InformationTerme) a;
        InformationTerme termInfoB = (InformationTerme) b;
        return termInfoA.docFreq < termInfoB.docFreq;
    }
}
