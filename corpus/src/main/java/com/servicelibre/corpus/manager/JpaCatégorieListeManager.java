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

import com.servicelibre.corpus.entity.CatégorieListe;
import com.servicelibre.corpus.entity.Liste;

@Repository
// Essentiellement pour traduction des exceptions « vendor-neutral »
@Transactional
public class JpaCatégorieListeManager implements CatégorieListeManager {
    private static Logger logger = LoggerFactory.getLogger(JpaCatégorieListeManager.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public CatégorieListe findOne(long catégorieListeId) {
	return (CatégorieListe) entityManager.createQuery("SELECT c FROM CatégorieListe c JOIN FETCH c.listes l WHERE c.id = ? ORDER BY c.ordre, l.ordre")
		.setParameter(1, catégorieListeId).getSingleResult();
    }

    @Override
    public CatégorieListe save(CatégorieListe catégorieListe) {
	entityManager.persist(catégorieListe);
	return catégorieListe;
    }

    @Override
    public CatégorieListe findByNom(String nom) {
	CatégorieListe catégorieListe = null;

	try {
	    catégorieListe = (CatégorieListe) entityManager
		    .createQuery("SELECT c FROM CatégorieListe c JOIN FETCH c.listes l WHERE c.nom = ? ORDER BY c.ordre,l.ordre").setParameter(1, nom)
		    .getSingleResult();
	} catch (NoResultException e) {
	    logger.warn("La catégorie de liste du nom [{}] est introuvable dans la table des catégories de liste. ({})", nom, e.getMessage());
	}
	return catégorieListe;
    }

    @Override
    public void flush() {
	entityManager.flush();

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CatégorieListe> findAllByCorpusId(long corpusId) {
	List<CatégorieListe> catégories = new ArrayList<CatégorieListe>();

	try {
	    catégories = (List<CatégorieListe>) entityManager.createQuery("select c from CatégorieListe c where c.corpus.id = ? order by c.ordre, c.nom")
		    .setParameter(1, corpusId).getResultList();
	} catch (NoResultException e) {
	    logger.warn("Aucune catégorie de liste pour le corpus [{}]. ({})", corpusId, e.getMessage());
	}

	return catégories;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Liste> getListes(CatégorieListe catégorie) {
	
	List<Liste> listes = new ArrayList<Liste>();
	
	try {
	    listes = (List<Liste>) entityManager.createQuery("select l from Liste l where l.catégorie.id = ? order by l.ordre, l.nom")
		    .setParameter(1, catégorie.getId()).getResultList();
	} catch (NoResultException e) {
	    logger.warn("Aucune liste dans cette catégorie pour le corpus [{}]. ({})", catégorie, e.getMessage());
	}
	return listes;
    }
}
