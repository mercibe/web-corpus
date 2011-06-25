package com.servicelibre.corpus.manager;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.entity.Corpus;


@Repository
// Essentiellement pour traduction des exceptions « vendor-neutral »
@Transactional
public class JpaCorpusManager implements CorpusManager 
{
	private static Logger logger = LoggerFactory.getLogger(JpaCorpusManager.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Corpus findOne(long corpusId) {
		return (Corpus) entityManager.createQuery("select c from Corpus c where c.id = ?").setParameter(1, corpusId).getSingleResult();
	}

	@Override
	public Corpus save(Corpus corpus) {
		entityManager.persist(corpus);
		return corpus;
	}

	@Override
	public Corpus findByNom(String nom) {
		Corpus corpus = null;

		try {
			corpus = (Corpus) entityManager.createQuery("select c from Corpus c where c.nom = ?").setParameter(1, nom).getSingleResult();
		} catch (NoResultException e) {
			logger.warn("Le corpus du nom [{}] est introuvable dans la table des corpus. ({})", nom, e.getMessage());
		}
		return corpus;
	}

	@Override
	public void flush() {
		entityManager.flush();

	}

}
