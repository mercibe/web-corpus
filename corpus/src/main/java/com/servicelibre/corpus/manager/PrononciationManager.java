package com.servicelibre.corpus.manager;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.entity.Mot;
import com.servicelibre.corpus.entity.Prononciation;

@Transactional
public interface PrononciationManager {

    List<Prononciation> findAll();

    Prononciation findOne(long prononciationId);

    List<Prononciation> findByMot(Mot mot);

    Prononciation save(Prononciation prononciation);

    Prononciation findByPrononciation(String prononciation);

}
