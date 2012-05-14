package com.servicelibre.corpus.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.servicelibre.corpus.entity.Mot;
import com.servicelibre.corpus.entity.MotPrononciation;
import com.servicelibre.corpus.entity.Prononciation;

public interface MotPrononciationRepository extends CrudRepository<MotPrononciation, Long> {

	List<MotPrononciation> findByMot(Mot mot);

	List<MotPrononciation> findByPrononciation(Prononciation prononciation);

}
