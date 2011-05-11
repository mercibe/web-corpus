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

package com.servicelibre.corpus.analyzis.persistence;

import java.sql.SQLException;

public class MysqlPersistenceManager extends JDBCPersitenceManager
{

    @Override
    protected void createTable(String tableName) throws SQLException
    {
        logger.info("Création de la table {}...", tableName);

        if (tableName.equalsIgnoreCase(getTableNameWithPrefix(CORPUS_TABLENAME)))
        {

            // TODO
        }
        else if (tableName.equalsIgnoreCase(getTableNameWithPrefix(DOCUMENT_TABLENAME)))
        {
            String documentTableName = getTableNameWithPrefix(DOCUMENT_TABLENAME);

            //TODO générer dynamiquement la création sur base des métadonnées de corpusInfo

            this.jdbcTemplate.execute("CREATE TABLE " + documentTableName + "( doc_id varchar(255) PRIMARY KEY,"
                    + "titre TEXT NOT NULL," + "categorie varchar(255) DEFAULT NULL," + "chapitre TEXT DEFAULT NULL,"
                    + "auteur varchar(255) NOT NULL," + "difficulte smallint DEFAULT NULL,"
                    + "cycles varchar(64) DEFAULT NULL) DEFAULT CHARSET=utf8 COLLATE utf8_bin");

            // TODO création des index
            this.jdbcTemplate.execute("CREATE INDEX document_cycles_idx ON " + documentTableName + "(cycles)");
            this.jdbcTemplate.execute("CREATE INDEX document_categorie_idx ON " + documentTableName + "(categorie)");
        }
        else if (tableName.equalsIgnoreCase(getTableNameWithPrefix(MOT_TABLENAME)))
        {

            String motTableName = getTableNameWithPrefix(MOT_TABLENAME);

            this.jdbcTemplate.execute("CREATE TABLE " + motTableName + "( doc_id VARCHAR(255),"
                    + "mot VARCHAR(255) DEFAULT NULL," + "freqmot DOUBLE DEFAULT NULL,"
                    + "lemme VARCHAR(255) DEFAULT NULL," + "freqlemme DOUBLE DEFAULT NULL,"
                    + "catgram VARCHAR(255) DEFAULT NULL," + "note VARCHAR(255) DEFAULT NULL,"
                    + "motgram VARCHAR(5) DEFAULT NULL," + "freqmotprecision VARCHAR(255) DEFAULT NULL, " + "freqlemmeprecision VARCHAR(255) DEFAULT NULL, "
                    + " FOREIGN KEY (doc_id) REFERENCES " + getTableNameWithPrefix(DOCUMENT_TABLENAME) + " (doc_id)) DEFAULT CHARSET=utf8 COLLATE utf8_bin");

            // création des index
            this.jdbcTemplate.execute("CREATE INDEX mot_mot_idx ON " + motTableName + "(mot)");
            this.jdbcTemplate.execute("CREATE INDEX mot_freqmot_idx ON " + motTableName + "(freqmot)");
            this.jdbcTemplate.execute("CREATE INDEX mot_lemme_idx ON " + motTableName + "(lemme)");
            this.jdbcTemplate.execute("CREATE INDEX mot_freqlemme_idx ON " + motTableName + "(freqlemme)");
            this.jdbcTemplate.execute("CREATE INDEX mot_catgram_idx ON " + motTableName + "(catgram)");
            this.jdbcTemplate.execute("CREATE INDEX mot_note_idx ON " + motTableName + "(note)");
            this.jdbcTemplate.execute("CREATE INDEX mot_motgram_idx ON " + motTableName + "(motgram)");
            this.jdbcTemplate.execute("CREATE INDEX mot_lemme_catgram_idx ON " + motTableName + "(lemme,catgram)");
            this.jdbcTemplate.execute("CREATE INDEX mot_freqmotprecision_idx ON " + motTableName + "(freqmotprecision)");
            this.jdbcTemplate.execute("CREATE INDEX mot_freqlemmeprecision_idx ON " + motTableName + "(freqlemmeprecision)");
            this.jdbcTemplate.execute("CREATE INDEX mot_mot_catgram_lemme_idx ON " + motTableName
                    + "(mot, catgram, lemme)");
        }
        else
        {
            logger.warn("DDL pour création de la table {} introuvable.", tableName);
        }
        
    }

}
