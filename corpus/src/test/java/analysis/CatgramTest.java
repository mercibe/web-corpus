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

package analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import com.servicelibre.corpus.analyzis.Catgram;
import com.servicelibre.corpus.analyzis.CatgramsLexique3;
import com.servicelibre.corpus.analyzis.CatgramsPivot;

public class CatgramTest
{

    @Test
    public void testCatgramPivot()
    {
        CatgramsPivot catgramsPivot = new CatgramsPivot();

        Map<String, Catgram> catgrams = catgramsPivot.getCatgrams();
        assertEquals("Nombre de catégories grammaticales pivot doit etre 53.", 53, catgrams.size());

        Catgram fromId = catgramsPivot.getCatgramFromId("DÉTERMINANT");
        assertNotNull(fromId);

        fromId = catgramsPivot.getCatgramFromId("déterminant");
        assertNotNull(fromId);

        // Pivot à pivot
        fromId = catgramsPivot.convertFromPivot("DÉTERMINANT");
        assertNotNull(fromId);
        assertEquals("DÉTERMINANT", fromId.id);

        fromId = catgramsPivot.convertToPivot("déterminant");
        assertNotNull(fromId);
        assertEquals("DÉTERMINANT", fromId.id);

        Catgram fromÉtiquette = catgramsPivot.getCatgramFromÉtiquette("article_indéfini");
        assertNotNull(fromÉtiquette);

        fromÉtiquette = catgramsPivot.getCatgramFromÉtiquette("article_indéfini");
        assertNotNull(fromÉtiquette);

        
        fromÉtiquette = catgramsPivot.getCatgramFromÉtiquette("functionword");
        assertNotNull(fromÉtiquette);
        assertEquals("FUNCTIONWORD", fromÉtiquette.id);

    }

    @Test
    public void testLexique3Catgram()
    {

        CatgramsLexique3 catgramsLexique3 = new CatgramsLexique3();

        Map<String, Catgram> catgrams = catgramsLexique3.getCatgrams();

        assertEquals("Nombre de catégories grammaticales pivot doit etre 23.", 23, catgrams.size());

        Catgram fromÉtiquette = catgramsLexique3.getCatgramFromÉtiquette("PRO:int");
        assertNotNull(fromÉtiquette);

        fromÉtiquette = catgramsLexique3.getCatgramFromÉtiquette("pro:Int");
        assertNotNull(fromÉtiquette);

        // Conversions Lexique3 => pivot
        Catgram fromId = catgramsLexique3.convertToPivot("ART_IND");

        assertNotNull(fromId);
        assertTrue(fromId.motGrammatical);
        assertEquals("La catégorie grammaticale pivot qui correspond à ART_IND doit être ARTICLE_INDÉFINI",
                "ARTICLE_INDÉFINI", fromId.id);

        fromId = catgramsLexique3.convertToPivot("Adj");
        assertNotNull(fromId);
        assertFalse(fromId.motGrammatical);
        assertEquals("ADJECTIF", fromId.id);

        // Conversions pivot => Lexique3
        Catgram catgramFromPivot = catgramsLexique3.convertFromPivot("VERBES");
        assertNull(catgramFromPivot);

        catgramFromPivot = catgramsLexique3.convertFromPivot("VERBE");
        assertNotNull(catgramFromPivot);
        assertEquals("VER", catgramFromPivot.id);

    }

    @Test
    public void testAllLexique3ToPivot()
    {
        CatgramsLexique3 catgramsLexique3 = new CatgramsLexique3();

        for (String lex3CatgramId : catgramsLexique3.getCatgrams().keySet())
        {
            Catgram pivot = catgramsLexique3.convertToPivot(lex3CatgramId);
            assertNotNull(pivot);
        }
    }


}
