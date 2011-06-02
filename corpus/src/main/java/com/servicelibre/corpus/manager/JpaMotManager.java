package com.servicelibre.corpus.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;
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

	private CriteriaBuilder builder;

	public JpaMotManager() {
		super();
	}

	@Transactional(readOnly = true)
	@Override
	public Mot findOne(long motId) {
		return (Mot) entityManager.createQuery("select m from Mot m where m.id = ?").setParameter(1, motId).getSingleResult();
	}

	@Transactional(readOnly = true)
	@Override
	public Mot findByMot(String mot) {
		return (Mot) entityManager.createQuery("select m from Mot m where m.mot = ?").setParameter(1, mot).getSingleResult();
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

		mots = entityManager.createQuery("from Mot").getResultList();

		// FIXME bug Hibernate et sérialisation Jackson => passer à OpenJPA ou
		// EclipseLink
		// List<Mot> mots2 = new ArrayList<Mot>(mots.size());
		// for (Mot mot : mots) {
		// mots2.add(new Mot(mot.getMot(), mot.lemme, mot.isLemme,
		// mot.getCatgram()));
		// }
		//
		// return mots2;
		return mots;
	}

	public CriteriaBuilder getBuilder() {
		if (builder == null) {
			builder = entityManager.getCriteriaBuilder();
		}
		return builder;
	}

	@Transactional(readOnly = true)
	@Override
	public List<Mot> findByGraphie(String graphie, Condition condition) {
		return findByGraphie(graphie, condition, null);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Mot> findByGraphie(String graphie, Condition condition, FiltreMot filtres) {
		CriteriaBuilder cb = getBuilder();
		CriteriaQuery<Mot> criteria = cb.createQuery(Mot.class);

		Root<Mot> motRacine = criteria.from(Mot.class);
		criteria.select(motRacine);

		// criteria.where(builder.like(arg0, arg1))
		// Pas d'utilisation de metamodel => pas typesafe pour l'instant
		Predicate p;
		if (condition == Condition.MOT_ENTIER) {
			p = cb.equal(motRacine.get("lemme"), graphie);
		} else {
			String likeCondition = "";
			switch (condition) {
			case MOT_COMMENCE_PAR:
				likeCondition = graphie + "%";
				break;
			case MOT_CONTIENT:
				likeCondition = "%" + graphie + "%";
				break;
			case MOT_FINIT_PAR:
				likeCondition = "%" + graphie;
				break;

			}
			p = cb.like(motRacine.get("lemme").as(String.class), likeCondition);

		}

		if (filtres != null) {
			p = addFiltresToPrédicat(cb, motRacine, p, filtres);
		}

		criteria.where(p);
		
		// Order by
		// criteria.orderBy(cb.asc(motRacine.get("lemme")));

		TypedQuery<Mot> q = entityManager.createQuery(criteria);

		return q.getResultList();
	}

	private Predicate addFiltresToPrédicat(CriteriaBuilder cb, Root<Mot> motRacine, Predicate p, FiltreMot filtres) {

		LinkedHashSet<Filtre> f = filtres.getFiltres();

		for (Iterator<Filtre> it = f.iterator(); it.hasNext();) {

			Filtre filtre = it.next();

			// Fonctionne à condition que le nom du filtre corresponde
			// exactement au nom de la colonne dans la DB / modèle
			System.err.println("filtre avant NPE = " + filtre);
			Path<Object> path = motRacine.get(filtre.nom);
			if (path != null) {
				In<Object> in = cb.in(path);

				for (DefaultKeyValue kv : filtre.keyValues) {
					in.value(kv.getKey());
				}
				p = cb.and(p, in);
			}
			else
			{
				logger.error("{} ne correspond à aucune colonne dans la DB/modèle.",filtre.nom);
			}
		}

		return p;
	}

}
