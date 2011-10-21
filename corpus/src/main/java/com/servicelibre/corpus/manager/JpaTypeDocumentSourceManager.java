package com.servicelibre.corpus.manager;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.entity.TypeDocumentSource;

@Repository
// Essentiellement pour traduction des exceptions « vendor-neutral »
@Transactional
public class JpaTypeDocumentSourceManager implements TypeDocumentSourceManager {
    private static Logger logger = LoggerFactory.getLogger(TypeDocumentSourceManager.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public TypeDocumentSource findOne(long typeDocumentSourceId) {
	return (TypeDocumentSource) entityManager.createQuery("SELECT tds FROM TypeDocumentSource tds WHERE tds.id = ? ORDER BY tds.nom")
		.setParameter(1, typeDocumentSourceId).getSingleResult();
    }

    @Override
    public TypeDocumentSource save(TypeDocumentSource typeDocumentSource) {
	entityManager.persist(typeDocumentSource);
	return typeDocumentSource;
    }

    @Override
    public TypeDocumentSource findByNom(String nom) {
	TypeDocumentSource typeDocumentSource = null;

	try {
	    typeDocumentSource = (TypeDocumentSource) entityManager.createQuery("SELECT tds FROM TypeDocumentSource WHERE tds.nom = ? ORDER BY tds.nom")
		    .setParameter(1, nom).getSingleResult();
	} catch (NoResultException e) {
	    logger.warn("Le type de document source du nom [{}] est introuvable dans la table des types de documents source. ({})", nom, e.getMessage());
	}
	return typeDocumentSource;
    }

    @Override
    public void flush() {
	entityManager.flush();

    }
}
