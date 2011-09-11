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

import com.servicelibre.corpus.entity.DocMetadata;

@Repository
// Essentiellement pour traduction des exceptions « vendor-neutral »
@Transactional
public class JpaDocMetadataManager implements DocMetadataManager {
	
	private static Logger logger = LoggerFactory.getLogger(JpaDocMetadataManager.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public DocMetadata findOne(long docMetadataId) {
		return (DocMetadata) entityManager.createQuery("select l from DocMetadata l where l.id = ?").setParameter(1, docMetadataId).getSingleResult();
	}

	@Override
	public DocMetadata save(DocMetadata docMetadata) {
		entityManager.persist(docMetadata);
		return docMetadata;
	}

	@Override
	public DocMetadata findByNom(String nom) {
		DocMetadata docMetadata = null;

		try {
			docMetadata = (DocMetadata) entityManager.createQuery("select l from DocMetadata l where l.nom = ?").setParameter(1, nom)
					.getSingleResult();
		} catch (NoResultException e) {
			logger.warn("La métadonnée de document du nom [{}] est introuvable dans la table des métadonnées de document. ({})", nom, e.getMessage());
		}
		return docMetadata;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DocMetadata> findByCorpusId(long corpusId) {
		List<DocMetadata> docMetadatas = new ArrayList<DocMetadata>();

		try {
			docMetadatas = (List<DocMetadata>) entityManager.createQuery("SELECT l FROM DocMetadata l WHERE l.corpus.id = ? ORDER BY l.ordre, l.nom")
			.setParameter(1, corpusId).getResultList();
		} catch (NoResultException e) {
			logger.warn("Aucune métadonnée de document pour le corpus [{}]. ({})", corpusId, e.getMessage());
		}

		return docMetadatas;
	}

}
