package com.servicelibre.corpus.repository;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

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
import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.entity.Mot;
import com.servicelibre.corpus.entity.MotPrononciation;
import com.servicelibre.corpus.entity.Prononciation;
import com.servicelibre.corpus.manager.Filtre;
import com.servicelibre.corpus.manager.FiltreRecherche;

public class MotRepositoryImpl implements MotRepositoryCustom {

	private static Logger logger = LoggerFactory.getLogger(MotRepositoryImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	MotRepository motRepository;
	
	@Autowired
	MotPrononciationRepository motPrononciationRepo;

	@Autowired
	PrononciationRepository prononciationRepo;

	private CriteriaBuilder builder;

	public MotRepositoryImpl() {
		super();
	}

	@Override
	@Transactional
	public int ajoutePrononciation(String forme, String phonétique) {

		int liaisonCpt = 0;

		Prononciation prononciation = null;

		prononciation = prononciationRepo.findByPrononciation(phonétique);

		if (prononciation == null) {
			prononciation = prononciationRepo.save(new Prononciation(phonétique));
		}

		// logger.debug("importation de la prononciation {}",
		// prononciation.prononciation);

		// Rechercher le/les éventuels mot/forme associés et lier
		List<Mot> mots = motRepository.findByMot(forme);
		for (Mot mot : mots) {
			MotPrononciation motPrononciation = motPrononciationRepo.save(new MotPrononciation(mot, prononciation));
			logger.debug("liaison de la prononciation {} à la forme {}", motPrononciation.getPrononciation(), forme);
			liaisonCpt++;
		}

		return liaisonCpt;
	}

	@Override
	public List<Mot> findByGraphie(String graphie, Condition condition) {
		return findByGraphie(graphie, condition, null);
	}

	@Override
	public List<Mot> findByGraphie(String graphie, Condition condition, FiltreRecherche filtres) {

		final CriteriaBuilder cb = getBuilder();
		final CriteriaQuery<Mot> criteria = cb.createQuery(Mot.class);

		final Root<Mot> mot = criteria.from(Mot.class);
		criteria.distinct(true);
		criteria.select(mot);

		// chargement EAGER des prononciations => DISTINCT dans le critère
		mot.fetch("motPrononciations", JoinType.LEFT).fetch("prononciation", JoinType.LEFT);

		// chargement EAGER de la liste primaire du mot
		mot.fetch("liste", JoinType.LEFT);

		// Tous les mots sont en minuscules
		graphie = graphie.toLowerCase();

		// criteria.where(builder.like(arg0, arg1))
		// Pas d'utilisation de metamodel => pas typesafe pour l'instant
		Predicate p;
		if (condition == Condition.ENTIER) {
			p = cb.equal(mot.get("mot"), graphie);
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
			p = cb.like(mot.get("mot").as(String.class), likeCondition);

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

	public CriteriaBuilder getBuilder() {
		if (builder == null) {
			builder = entityManager.getCriteriaBuilder();
		}
		return builder;
	}

	private Predicate addFiltresToPrédicat(CriteriaBuilder cb, Root<Mot> motRacine, Predicate p, FiltreRecherche filtres) {

		LinkedHashSet<Filtre> f = filtres.getFiltres();

		Map<String, In<Object>> inClauses = new HashMap<String, CriteriaBuilder.In<Object>>();

		for (Iterator<Filtre> it = f.iterator(); it.hasNext();) {

			Filtre filtre = it.next();

			// Fonctionne à condition que le nom du filtre corresponde
			// exactement au nom de la colonne dans la DB / modèle
			// Sinon, NPE!
			Path<Object> path = motRacine.get(filtre.nom.replaceAll("_.*", ""));

			if (path != null) {

				In<Object> in = null;

				if (filtre.nom.startsWith("liste_")) {
					in = inClauses.get(filtre.nom);
					if (in == null) {
						// Il faut ajouter un critère sur l'objet « liste »,
						// variable d'instance de la classe Mot (collection des listes
						// auxquelles appartiennent le mot = étiquettes associées au mot)

						Join<Object, Object> joinListe = motRacine.join("listeMots").join("liste");
						

						
						// FIXME BUG
						in = cb.in(joinListe);
						inClauses.put(filtre.nom, in);
					}

					// Construction de la clause « IN » avec les Ids des listes
					// présents dans le filtre
					for (DefaultKeyValue kv : filtre.keyValues) {
						in.value(Long.parseLong(kv.getKey().toString()));
					}

				} else {

					in = inClauses.get(filtre.nom);

					if (in == null) {
						in = cb.in(path);
						inClauses.put(filtre.nom, in);
					}

					for (DefaultKeyValue kv : filtre.keyValues) {
						in.value(kv.getKey());
					}
				}

			} else {
				logger.error("{} ne correspond à aucune colonne dans la DB/modèle.", filtre.nom);
			}
		}

		// ajout de toutes les clauses IN
		for (String nomFiltre : inClauses.keySet()) {
			p = cb.and(p, inClauses.get(nomFiltre));
		}

		return p;
	}

	@Transactional(readOnly = true)
	@Override
	public List<Mot> findByPrononciation(String prononciation, Condition condition, FiltreRecherche filtres) {

		CriteriaBuilder cb = getBuilder();
		CriteriaQuery<Mot> criteria = cb.createQuery(Mot.class);

		Root<Mot> mot = criteria.from(Mot.class);
		criteria.distinct(true);
		criteria.select(mot);

		// chargement EAGER des prononciations => DISTINCT dans le critère (cf. ci-dessus)
		mot.fetch("motPrononciations").fetch("prononciation");

		// chargement EAGER de la liste primaire du mot
		mot.fetch("liste", JoinType.LEFT);

		// Pas d'utilisation de metamodel => pas typesafe pour l'instant
		Path<Object> prononciationPath = mot.join("motPrononciations").get("prononciation").get("prononciation");

		Predicate p;
		if (condition == Condition.ENTIER) {
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

}
