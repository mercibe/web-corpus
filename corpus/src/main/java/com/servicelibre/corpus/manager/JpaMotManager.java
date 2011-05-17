package com.servicelibre.corpus.manager;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.liste.Liste;
import com.servicelibre.corpus.liste.Mot;

@Repository
@Transactional
public class JpaMotManager implements MotManager {

	private static Logger logger = LoggerFactory.getLogger(JpaMotManager.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Mot findOne(long motId) {
		return (Mot) entityManager.createQuery("select m from Mot m where m.id = ?").setParameter(1, motId).getSingleResult();
	}

	@Override
	public Mot findByMot(String mot) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mot save(Mot mot) {
		entityManager.persist(mot);
		return mot;
	}

	@Override
	public void deleteFromListe(Liste liste) {
		logger.info("Suppression de tous les mots de la liste [{}].", liste);
		entityManager.createQuery("delete from Mot m where m.liste = ?").setParameter(1, liste).executeUpdate();
	}

}
