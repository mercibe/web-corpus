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

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;


public class InformationTermeTextComparator<T extends InformationTerme> implements Comparator<InformationTerme>
{

    private Collator collator = Collator.getInstance(Locale.CANADA_FRENCH);
    
    public int compare(InformationTerme o1, InformationTerme o2)
    {
        
        return collator.compare(o1.term.text(), o2.term.text());
    }

}
