package com.servicelibre.corpus.manager;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.entity.Liste;
import com.servicelibre.corpus.entity.ListeMot;
import com.servicelibre.corpus.entity.Mot;

@Repository
// Essentiellement pour traduction des exceptions « vendor-neutral »
@Transactional
public class JpaListeMotManager implements ListeMotManager {

	private static Logger logger = LoggerFactory.getLogger(JpaListeMotManager.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public ListeMot findOne(long listeMotId) {
		 return (ListeMot) entityManager.createQuery("select lm from ListeMot lm where lm.id = ?").setParameter(1, listeMotId)
	                .getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ListeMot> findByListe(Liste liste) {
		 return (List<ListeMot>) entityManager.createQuery("select lm from ListeMot lm where lm.liste = ?").setParameter(1, liste)
	                .getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ListeMot> findByMot(Mot mot) {
		 return (List<ListeMot>) entityManager.createQuery("select lm from ListeMot lm where lm.mot = ?").setParameter(1, mot)
	                .getResultList();
	}

	@Override
	public ListeMot save(ListeMot listeMot) {
		entityManager.persist(listeMot);
		return listeMot;
	}

}
