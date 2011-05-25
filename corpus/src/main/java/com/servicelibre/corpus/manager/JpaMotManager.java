package com.servicelibre.corpus.manager;

import java.util.ArrayList;
import java.util.List;

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

	@Transactional(readOnly = true)
	@Override
	public Mot findOne(long motId) {
		return (Mot) entityManager.createQuery("select m from Mot m where m.id = ?").setParameter(1, motId).getSingleResult();
	}

	@Transactional(readOnly = true)
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
	public int deleteFromListe(Liste liste) {
		logger.info("Suppression de tous les mots de la liste [{}].", liste);
		return entityManager.createQuery("delete from Mot m where m.liste = ?").setParameter(1, liste).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public List<Mot> findAll() {
		List<Mot> mots = new ArrayList<Mot>();
		
		mots = entityManager.createQuery("from Mot order by mot").getResultList();
		
		//FIXME bug Hibernate et sérialisation Jackson => passer à OpenJPA ou EclipseLink
		List<Mot> mots2 = new ArrayList<Mot>(mots.size());
		for (Mot mot : mots) {
			mots2.add(new Mot(mot.getMot(), mot.lemme, mot.isLemme, mot.getCatgram()));
		}
		
		return mots2;
		//return mots;
	}

}
