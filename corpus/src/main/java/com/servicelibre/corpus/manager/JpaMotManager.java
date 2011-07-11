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
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.entity.Mot;

@Repository
@Transactional
public class JpaMotManager implements MotManager {

	@SuppressWarnings("unused")
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

	/**
	 * "tout";4 "même";3 "derrière";3 "bas";3 "devant";3 "plein";3 "fort";3
	 * "avant";3 "mal";3 "attendre";2 => FIXME (contrainte DB) "bref";2
	 * "sourire";2 "jeune";2 "rose";2 "intérieur";2
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public List<Mot> findByMot(String mot) {
		return (List<Mot>) entityManager.createQuery("select m from Mot m where m.mot = ?").setParameter(1, mot).getResultList();
	}

	@Override
	public Mot save(Mot mot) {
		entityManager.persist(mot);
		return mot;
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

		// Tous les mots sont en minuscules
		graphie = graphie.toLowerCase();

		// criteria.where(builder.like(arg0, arg1))
		// Pas d'utilisation de metamodel => pas typesafe pour l'instant
		Predicate p;
		if (condition == Condition.ENTIER) {
			p = cb.equal(motRacine.get("lemme"), graphie);
		} else {
			String likeCondition = "";
			switch (condition) {
			case COMMENCE_PAR:
				likeCondition = graphie + "%";
				break;
			case CONTIENT:
				likeCondition = "%" + graphie + "%";
				break;
			case FINIT_PAR:
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

			if (filtre.nom.equals("liste")) {

				// Il faut ajouter un critère sur l'objet « listes », variable
				// d'instance de la classe Mot (collection des listes auxquelles
				// appartiennent le mot)
				Join<Object, Object> join = motRacine.join("listes");

				In<Object> in = cb.in(join);

				// Construction de la clause « IN » avec les Ids des listes
				// présents dans le filtre
				for (DefaultKeyValue kv : filtre.keyValues) {
					in.value(Long.parseLong(kv.getKey().toString()));
				}

				p = cb.and(p, in);

			} else {

				// Fonctionne à condition que le nom du filtre corresponde
				// exactement au nom de la colonne dans la DB / modèle
				// Sinon, NPE!

				Path<Object> motPath = motRacine.get(filtre.nom);

				if (motPath != null) {
					In<Object> in = cb.in(motPath);
					for (DefaultKeyValue kv : filtre.keyValues) {
						in.value(kv.getKey());
					}
					p = cb.and(p, in);
				}

			}
		}
		return p;
	}

}
