package com.servicelibre.corpus.manager;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.entity.Corpus;
import com.servicelibre.corpus.entity.CorpusMetadata;

@Repository
// Essentiellement pour traduction des exceptions « vendor-neutral »
@Transactional
public class JpaCorpusMetadataManager implements CorpusMetadataManager {
	private static Logger logger = LoggerFactory.getLogger(JpaCorpusMetadataManager.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public CorpusMetadata findOne(long corpusMetadataId) {
		return (CorpusMetadata) entityManager.createQuery("select l from CorpusMetadata l where l.id = ?").setParameter(1, corpusMetadataId).getSingleResult();
	}

	@Override
	public CorpusMetadata save(CorpusMetadata corpusMetadata) {
		entityManager.persist(corpusMetadata);
		return corpusMetadata;
	}

	@Override
	public CorpusMetadata findByNom(String nom) {
		CorpusMetadata corpusMetadata = null;

		try {
			corpusMetadata = (CorpusMetadata) entityManager.createQuery("select l from CorpusMetadata l where l.nom = ?").setParameter(1, nom)
					.getSingleResult();
		} catch (NoResultException e) {
			logger.warn("La métadonnée de corpus du nom [{}] est introuvable dans la table des métadonnées de corpus. ({})", nom, e.getMessage());
		}
		return corpusMetadata;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CorpusMetadata> findByCorpusId(long corpusId) {
		List<CorpusMetadata> corpusMetadatas = new ArrayList<CorpusMetadata>();

		try {
			Corpus c = new Corpus();
			c.setId((int) corpusId);
			corpusMetadatas = (List<CorpusMetadata>) entityManager.createQuery("select l from CorpusMetadata l where l.corpus = ?").setParameter(1, c)
					.getResultList();
		} catch (NoResultException e) {
			logger.warn("Aucune métadonnée pour le corpus [{}]. ({})", corpusId, e.getMessage());
		}

		return corpusMetadatas;
	}

}
