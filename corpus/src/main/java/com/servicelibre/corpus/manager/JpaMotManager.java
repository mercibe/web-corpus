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
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.entity.Liste;
import com.servicelibre.corpus.entity.Mot;
import com.servicelibre.corpus.entity.Prononciation;

@Repository
public class JpaMotManager implements MotManager {

	private static Logger logger = LoggerFactory.getLogger(JpaMotManager.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	PrononciationManager prononciationManager;

	private CriteriaBuilder builder;

	public JpaMotManager() {
		super();
	}

	@Override
	public Mot findOne(long motId) {
		return (Mot) entityManager.createQuery("select m from Mot m where m.id = ?").setParameter(1, motId).getSingleResult();
	}

	@Override
	public Mot findByMot(String lemme, String mot, String catgram, String genre) {
		Mot motTrouvé = null;
		
		try {
			motTrouvé =(Mot) entityManager.createQuery("select m from Mot m where m.lemme = ? and m.mot = ? and m.catgram = ? and m.genre = ?")
			.setParameter(1, lemme)
			.setParameter(2, mot)
			.setParameter(3, catgram)
			.setParameter(4, genre)
			.getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			// retourner NULL
		}
		
		return motTrouvé;
	}
	
	/**
	 * "tout";4 "même";3 "derrière";3 "bas";3 "devant";3 "plein";3 "fort";3
	 * "avant";3 "mal";3 "attendre";2 => FIXME (contrainte DB) "bref";2
	 * "sourire";2 "jeune";2 "rose";2 "intérieur";2
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Mot> findByMot(String mot) {
		return (List<Mot>) entityManager.createQuery("select m from Mot m where m.mot = ?").setParameter(1, mot).getResultList();
	}

	@Override
	@Transactional(readOnly = false)
	public Mot save(Mot mot) {
		entityManager.persist(mot);
		return mot;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Mot> findAll() {
		List<Mot> mots = new ArrayList<Mot>();

		mots = entityManager.createQuery("from Mot m LEFT OUTER JOIN FETCH m.prononciations ").getResultList();
		
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

	@Override
	public List<Mot> findByGraphie(String graphie, Condition condition) {
		return findByGraphie(graphie, condition, null);
	}

	@Override
	public List<Mot> findByGraphie(String graphie, Condition condition, FiltreMot filtres) {
		
		final CriteriaBuilder cb = getBuilder();
		final CriteriaQuery<Mot> criteria = cb.createQuery(Mot.class);

		final Root<Mot> mot = criteria.from(Mot.class);
		criteria.distinct(true);
		criteria.select(mot);

		// chargement EAGER des prononciations => DISTINCT dans le critère
		mot.fetch("prononciations", JoinType.LEFT);
		
		// chargement EAGER de la liste primaire du mot
		mot.fetch("liste", JoinType.LEFT);
		
		// Tous les mots sont en minuscules
		graphie = graphie.toLowerCase();

		// criteria.where(builder.like(arg0, arg1))
		// Pas d'utilisation de metamodel => pas typesafe pour l'instant
		Predicate p;
		if (condition == Condition.ENTIER) {
			p = cb.equal(mot.get("lemme"), graphie);
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
			p = cb.like(mot.get("lemme").as(String.class), likeCondition);

		}

		if (filtres != null) {
			p = addFiltresToPrédicat(cb, mot, p, filtres);
		}

		criteria.where(p);

		// Order by
		criteria.orderBy(cb.asc(mot.get("mot")));

		TypedQuery<Mot> q = entityManager.createQuery(criteria);

		return q.getResultList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Mot> findByPrononciation(String prononciation, Condition condition, FiltreMot filtres) {

		CriteriaBuilder cb = getBuilder();
		CriteriaQuery<Mot> criteria = cb.createQuery(Mot.class);

		Root<Mot> mot = criteria.from(Mot.class);
		criteria.distinct(true);
		criteria.select(mot);
		
		// chargement EAGER des prononciations => DISTINCT dans le critère
		mot.fetch("prononciations");
		
		// chargement EAGER de la liste primaire du mot
		mot.fetch("liste", JoinType.LEFT);

		// Pas d'utilisation de metamodel => pas typesafe pour l'instant
		Path<Object> prononciationPath = mot.join("prononciations").get("prononciation");

		Predicate p;
		if (condition == Condition.ENTIER) {
			// return
			// entityManager.createQuery("SELECT m FROM Mot m JOIN m.prononciations p WHERE p.prononciation = ?").setParameter(1,
			// prononciation).getResultList();
			p = cb.equal(prononciationPath, prononciation);
		} else {
			String likeCondition = "";
			switch (condition) {
			case COMMENCE_PAR:
				likeCondition = prononciation + "%";
				break;
			case CONTIENT:
				likeCondition = "%" + prononciation + "%";
				break;
			case FINIT_PAR:
				likeCondition = "%" + prononciation;
				break;

			}
			p = cb.like(prononciationPath.as(String.class), likeCondition);

		}

		if (filtres != null) {
			p = addFiltresToPrédicat(cb, mot, p, filtres);
		}

		criteria.where(p);

		// Order by
		criteria.orderBy(cb.asc(mot.get("mot")));


		TypedQuery<Mot> q = entityManager.createQuery(criteria);

		return q.getResultList();
	}

	private Predicate addFiltresToPrédicat(CriteriaBuilder cb, Root<Mot> motRacine, Predicate p, FiltreMot filtres) {

		LinkedHashSet<Filtre> f = filtres.getFiltres();

		for (Iterator<Filtre> it = f.iterator(); it.hasNext();) {

			Filtre filtre = it.next();

			// Fonctionne à condition que le nom du filtre corresponde
			// exactement au nom de la colonne dans la DB / modèle
			// Sinon, NPE!

			// Tour de passe passe rapide pour liste / particularités...
			String nomFiltre = filtre.nom.replaceAll("_secondaire", "");
			
			Path<Object> path = motRacine.get(nomFiltre);

			if (path != null) {

				In<Object> in = null;

				if (nomFiltre.equals("liste")) {

					// Il faut ajouter un critère sur l'objet « listes », variable
					// d'instance de la classe Mot (collection des listes auxquelles
					// appartiennent le mot = étiquettes associées au mot)
					Join<Object, Object> joinListe = motRacine.join("listes");

					in = cb.in(joinListe);

					// Construction de la clause « IN » avec les Ids des listes
					// présents dans le filtre
					for (DefaultKeyValue kv : filtre.keyValues) {
						in.value(Long.parseLong(kv.getKey().toString()));
					}

				} else {
					in = cb.in(path);
					for (DefaultKeyValue kv : filtre.keyValues) {
						in.value(kv.getKey());
					}
				}

				p = cb.and(p, in);
			} else {
				logger.error("{} ne correspond à aucune colonne dans la DB/modèle.", nomFiltre);
			}
		}

		return p;
	}

	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	@Transactional
	public int ajoutePrononciation(String forme, String phonétique) {

		int liaisonCpt = 0;

		Prononciation prononciation = null;

		prononciation = prononciationManager.findByPrononciation(phonétique);

		if (prononciation == null) {
			prononciation = new Prononciation(phonétique);
			prononciationManager.save(prononciation);
		}

		// logger.debug("importation de la prononciation {}",
		// prononciation.prononciation);

		// Rechercher le/les éventuels mot/forme associés et lier
		List<Mot> mots = findByMot(forme);
		for (Mot mot : mots) {
			mot.ajoutePrononciation(prononciation);
			logger.debug("liaison de la prononciation {} à la forme {}", prononciation.prononciation, forme);
			save(mot);
			liaisonCpt++;
		}

		return liaisonCpt;
	}

}
