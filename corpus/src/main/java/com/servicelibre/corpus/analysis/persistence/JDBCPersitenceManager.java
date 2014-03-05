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

package com.servicelibre.corpus.analysis.persistence;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.servicelibre.corpus.analysis.Catgram;
import com.servicelibre.corpus.analysis.CorpusInfo;
import com.servicelibre.corpus.analysis.DocumentInfo;
import com.servicelibre.corpus.analysis.MotInfo;
import com.servicelibre.corpus.analysis.MotInfo.FreqPrecision;
import com.servicelibre.corpus.metadata.IntMetadata;
import com.servicelibre.corpus.metadata.Metadata;
import com.servicelibre.corpus.metadata.StringMetadata;

public abstract class JDBCPersitenceManager {

	static Logger logger = LoggerFactory.getLogger(JDBCPersitenceManager.class);

	static final String CORPUS_TABLENAME = "corpus";
	static final String DOCUMENT_TABLENAME = "document";
	static final String MOT_TABLENAME = "mot";

	protected JdbcTemplate jdbcTemplate;
	private CorpusInfo corpusInfo;
	private String tablePrefix;

	// Ordre des tables important (pour CREATE / DROP et contrainte d'intégrité)
	private String[] tablenames = { CORPUS_TABLENAME, DOCUMENT_TABLENAME, MOT_TABLENAME };

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public JDBCPersitenceManager() {
		super();
	}

	public JDBCPersitenceManager(CorpusInfo corpusInfo) {
		this.corpusInfo = corpusInfo;
	}

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public CorpusInfo getCorpusInfo() {
		return corpusInfo;
	}

	public void setCorpusInfo(CorpusInfo corpusInfo) {
		this.corpusInfo = corpusInfo;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public String getTablePrefix() {
		return tablePrefix;
	}

	public void setTablePrefix(String tablePrefix) {
		this.tablePrefix = tablePrefix;
	}

	/**
	 * Sauvegarde des informations d'analyse lexicale du corpus dans une base de
	 * données relationnelle
	 * 
	 * @param corpusInfo
	 */
	public void persist(CorpusInfo corpusInfo) {
		setCorpusInfo(corpusInfo);

		setDatabaseOptions();

		// Création des tables si inexistantes ou truncate (tables préfixées par
		// le nom du corpus injecté)
		dropAndCreateTables();

		// Remplissage des tables
		// Les documents...
		// FIXME générique!!!
		int cpt = 1;
		int total = corpusInfo.getDocuments().size();
		
		for (DocumentInfo doc : corpusInfo.getDocuments()) {

			String[] colonnes = { "doc_id", "titre", "categorie", "chapitre", "auteur", "difficulte", "cycles" };
			String[] minColonnes = { "doc_id", "titre", "auteur" };

			Arrays.sort(colonnes);

			Map<String, Object> metaCols = new HashMap<String, Object>(10);

			for (Metadata docMeta : doc.getMetadatas()) {
				// Si meta dans colonnes, ajouter à la Map
				if (Arrays.binarySearch(colonnes, docMeta.getNom()) >= 0) {
					if (docMeta instanceof StringMetadata) {
						metaCols.put(docMeta.getNom(), ((StringMetadata) docMeta).getValue());
					} else if (docMeta instanceof IntMetadata) {
						metaCols.put(docMeta.getNom(), Integer.valueOf(((IntMetadata) docMeta).getValue()));
					}
				}

			}

			SqlParameterSource namedParameters = new MapSqlParameterSource(metaCols);
			String sql = "INSERT INTO " + getTableNameWithPrefix(DOCUMENT_TABLENAME)
					+ " (doc_id, titre, auteur, categorie, chapitre, difficulte, cycles) "
					+ " VALUES(:doc_id, :titre, :auteur, :categorie, :chapitre,  :difficulte, :cycles)";

			// TODO affreux « workaround » pour sauver temporairement
			// généralisation
			if (metaCols.size() != colonnes.length) {
				// sql = "INSERT INTO " + getTableNameWithPrefix(DOCUMENT_TABLENAME) + " (doc_id, titre, auteur) " +
				// " VALUES(:doc_id, :titre, :auteur)";
				sql = "INSERT INTO " + getTableNameWithPrefix(DOCUMENT_TABLENAME) + " (doc_id) " + " VALUES(:doc_id)";
			}

			try {
				this.namedParameterJdbcTemplate.update(sql, namedParameters);
			} catch (DuplicateKeyException e) {
				logger.error("Le document {} existe déjà dans la base de données.", metaCols, e);
			} catch (DataAccessException e) {

				e.printStackTrace();
			}

			// Les mots de ce document...
			metaCols = new HashMap<String, Object>(10);
			for (MotInfo motInfo : doc.getMots()) {
				
				
				
				metaCols.put("doc_id", doc.docId);
				metaCols.put("mot", motInfo.getMot());
				metaCols.put("freqmot", motInfo.getFreqMot());
				metaCols.put("lemme", motInfo.getLemme());
				metaCols.put("freqlemme", motInfo.getFreqLemme());
				
				String catgramId = "";
				String motGrammatical = "";
				
				Catgram catgram = motInfo.getCatgram();
				if (catgram != null) {
					catgramId = catgram.id;
					motGrammatical = Boolean.toString(catgram.motGrammatical);
				}
				metaCols.put("catgram", catgramId);
				metaCols.put("motgram", motGrammatical);

				metaCols.put("note", motInfo.getNote());
				metaCols.put("freqmotprecision", motInfo.getFreqMotPrecision().name());

				// FIXME!!! Est-ce nécessaire? Pourquoi?
				FreqPrecision freqLemmePrecision = motInfo.getFreqLemmePrecision();
				if (freqLemmePrecision != null) {
					metaCols.put("freqlemmeprecision", freqLemmePrecision.name());
				} else {
					logger.error("Pas de freqlemmeprecision pour {} - {}", motInfo, doc.docId);
				}

				namedParameters = new MapSqlParameterSource(metaCols);
				sql = "INSERT INTO "
						+ getTableNameWithPrefix(MOT_TABLENAME)
						+ " (doc_id, mot, freqmot, lemme, freqlemme, catgram, note,  motgram, freqmotprecision, freqlemmeprecision) "
						+ "VALUES(:doc_id, :mot, :freqmot, :lemme, :freqlemme, :catgram, :note, :motgram, :freqmotprecision, :freqlemmeprecision) ";
				this.namedParameterJdbcTemplate.update(sql, namedParameters);
				
				
			}

			logger.debug("{}/{} données d'analyse textuelles sauvegardées SQL", cpt++, total);

		}

		closeDatabase();

	}

	protected void closeDatabase() {
		// surcharger au besoin

	}

	protected void setDatabaseOptions() {
		// surcharger au besoin

	}

	private void dropAndCreateTables() {
		try {
			String realTableName = "";

			// Drop (en ordre inverse de la création)
			for (int i = tablenames.length - 1; i > 0; i--) {
				realTableName = getTableNameWithPrefix(tablenames[i]);
				dropTable(realTableName);
			}

			// Create (création des tables, dans l'ordre)
			for (String tablename : tablenames) {
				realTableName = getTableNameWithPrefix(tablename);
				createTable(realTableName);
			}

		} catch (SQLException e) {
			logger.error("Erreur lors de la création ou du truncate table.", e);
		}

	}

	protected abstract void createTable(String tableName) throws SQLException;

	public String getTableNameWithPrefix(String tablename) {
		return (tablePrefix + "_" + tablename).toUpperCase();
	}

	protected void dropTable(String tableName) {
		logger.info("Drop de la table {}", tableName);

		try {
			this.jdbcTemplate.execute("DROP TABLE " + tableName);
		} catch (BadSqlGrammarException e) {
			logger.warn("Erreur lors la suppression de la table {}", tableName, e);
		}
	}
	
    private String getMotLemmeCatgramKey(MotInfo motInfo)
    {

        return new StringBuilder().append(motInfo.getMot()).append("|").append(motInfo.getLemme()).append("|").append(motInfo.getCatgram()).toString();
    }

}
