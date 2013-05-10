package com.servicelibre.repositories.corpus;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.servicelibre.entities.corpus.Mot;
import com.servicelibre.entities.corpus.MotPrononciation;
import com.servicelibre.entities.corpus.Prononciation;

public interface MotPrononciationRepository extends CrudRepository<MotPrononciation, Long> {

	List<MotPrononciation> findByMot(Mot mot);

	List<MotPrononciation> findByPrononciation(Prononciation prononciation);

	MotPrononciation findByMotAndPrononciation(Mot mot, Prononciation prononciation);

}
