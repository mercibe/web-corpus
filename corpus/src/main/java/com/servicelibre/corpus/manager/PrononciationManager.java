package com.servicelibre.corpus.manager;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.entity.Mot;
import com.servicelibre.corpus.entity.Prononciation;

@Transactional
@Deprecated
public interface PrononciationManager {

    List<Prononciation> findAll();

    Prononciation findOne(long prononciationId);

    List<Prononciation> findByMot(Mot mot);

    Prononciation save(Prononciation prononciation);

    Prononciation findByPrononciation(String prononciation);

	EntityManager getEntityManager();

	void setEntityManager(EntityManager entityManager);

}
