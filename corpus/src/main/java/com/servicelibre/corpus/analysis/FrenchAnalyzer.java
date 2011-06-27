/**
 * Code library for textual corpus management
 *
 * Copyright (C) 2011 Benoit Mercier <benoit.mercier@servicelibre.com> — Tous droits réservés.
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

package com.servicelibre.corpus.analysis;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.WordlistLoader;
import org.apache.lucene.analysis.fr.ElisionFilter;
import org.apache.lucene.analysis.fr.FrenchStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

/**
 * {@link Analyzer} for French language.
 * <p>
 * Supports an external list of stopwords (words that will not be indexed at
 * all) and an external list of exclusions (word that will not be stemmed, but
 * indexed). A default set of stopwords is used unless an alternative list is
 * specified, but the exclusion list is empty by default.
 * </p>
 * 
 * <a name="version"/>
 * <p>
 * You must specify the required {@link Version} compatibility when creating
 * FrenchAnalyzer:
 * <ul>
 * <li>As of 2.9, StopFilter preserves position increments
 * </ul>
 * 
 * <p>
 * <b>NOTE</b>: This class uses the same {@link Version} dependent settings as
 * {@link StandardAnalyzer}.
 * </p>
 * 
 * @version $Id$
 */
public final class FrenchAnalyzer extends Analyzer
{

    private static final String[] DET_ART_ELISION = new String[] { "l", "m", "t", "n", "s", "j", "d", "c", "qu",
            "jusqu", "quelqu", "lorsqu", "puisqu", "quoiqu" };

    /**
     * Extended list of typical French stopwords.
     */
    public final static String[] FRENCH_STOP_WORDS = {};

    /**
     * Contains the stopwords used with the {@link StopFilter}.
     */
    private Set<Object> stoptable = new HashSet<Object>();
    private final Version matchVersion;

    /**
     * Builds an analyzer with the default stop words (
     * {@link #FRENCH_STOP_WORDS}).
     * 
     * @deprecated Use {@link #FrenchAnalyzer(Version)} instead.
     */
    public FrenchAnalyzer()
    {
        this(Version.LUCENE_30);
    }

    /**
     * Builds an analyzer with the default stop words (
     * {@link #FRENCH_STOP_WORDS}).
     */
    public FrenchAnalyzer(Version matchVersion)
    {
        stoptable = StopFilter.makeStopSet(FRENCH_STOP_WORDS);
        this.matchVersion = matchVersion;
    }

    /**
     * Builds an analyzer with the given stop words.
     * 
     * @deprecated Use {@link #FrenchAnalyzer(Version, String[])} instead.
     */
    public FrenchAnalyzer(String[] stopwords)
    {
        this(Version.LUCENE_23, stopwords);
    }

    /**
     * Builds an analyzer with the given stop words.
     */
    public FrenchAnalyzer(Version matchVersion, String[] stopwords)
    {
        stoptable = StopFilter.makeStopSet(stopwords);
        this.matchVersion = matchVersion;
    }

    /**
     * Builds an analyzer with the given stop words.
     * 
     * @throws IOException
     * 
     * @deprecated Use {@link #FrenchAnalyzer(Version, File)} instead
     */
    public FrenchAnalyzer(File stopwords) throws IOException
    {
        this(Version.LUCENE_23, stopwords);
    }

    /**
     * Builds an analyzer with the given stop words.
     * 
     * @throws IOException
     */
    public FrenchAnalyzer(Version matchVersion, File stopwords) throws IOException
    {
        stoptable = new HashSet<Object>(WordlistLoader.getWordSet(stopwords));
        this.matchVersion = matchVersion;
    }

    /**
     * Builds an exclusionlist from an array of Strings.
     */
    public void setStemExclusionTable(String[] exclusionlist)
    {
        StopFilter.makeStopSet(exclusionlist);
        setPreviousTokenStream(null); // force a new stemmer to be created
    }

    /**
     * Builds an exclusionlist from a Map.
     */
    public void setStemExclusionTable(Map<?, ?> exclusionlist)
    {
        new HashSet<Object>(exclusionlist.keySet());
        setPreviousTokenStream(null); // force a new stemmer to be created
    }

    /**
     * Builds an exclusionlist from the words contained in the given file.
     * 
     * @throws IOException
     */
    public void setStemExclusionTable(File exclusionlist) throws IOException
    {
        new HashSet<Object>(WordlistLoader.getWordSet(exclusionlist));
        setPreviousTokenStream(null); // force a new stemmer to be created
    }

    /**
     * Creates a {@link TokenStream} which tokenizes all the text in the
     * provided {@link Reader}.
     * 
     * @return A {@link TokenStream} built from a {@link StandardTokenizer}
     *         filtered with {@link StandardFilter}, {@link StopFilter},
     *         {@link FrenchStemFilter} and {@link LowerCaseFilter}
     */
    public final TokenStream tokenStream(String fieldName, Reader reader)
    {

        if (fieldName == null) throw new IllegalArgumentException("fieldName must not be null");
        if (reader == null) throw new IllegalArgumentException("reader must not be null");

        TokenStream result = new StandardTokenizer(matchVersion, reader);
        result = new StandardFilter(result);
        //result = new StopFilter(StopFilter.getEnablePositionIncrementsVersionDefault(matchVersion), result, stoptable);
        result = new ElisionFilter(result, DET_ART_ELISION);
        result = new LowerCaseFilter(result);
        return result;
    }

    private class SavedStreams
    {
        Tokenizer source;
        TokenStream result;
    };

    /**
     * Returns a (possibly reused) {@link TokenStream} which tokenizes all the
     * text in the provided {@link Reader}.
     * 
     * @return A {@link TokenStream} built from a {@link StandardTokenizer}
     *         filtered with {@link StandardFilter}, {@link StopFilter},
     *         {@link FrenchStemFilter} and {@link LowerCaseFilter}
     */
    public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException
    {
        SavedStreams streams = (SavedStreams) getPreviousTokenStream();
        if (streams == null)
        {
            streams = new SavedStreams();
            streams.source = new StandardTokenizer(matchVersion, reader);
            streams.result = new StandardFilter(streams.source);
            streams.result = new ElisionFilter(streams.result, DET_ART_ELISION);
            //streams.result = new StopFilter(StopFilter.getEnablePositionIncrementsVersionDefault(matchVersion), streams.result, stoptable);
            streams.result = new LowerCaseFilter(streams.result);

            setPreviousTokenStream(streams);
        }
        else
        {
            streams.source.reset(reader);
        }
        return streams.result;
    }
}
