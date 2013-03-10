package com.servicelibre.repositories.corpus;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.servicelibre.entities.corpus.Mot;

public interface MotRepository extends CrudRepository<Mot, Long>, MotRepositoryCustom {

	Mot findByLemmeAndMotAndCatgramAndGenre(String lemme, String mot, String catgram, String genre);

	/**
	 * "tout";4 "même";3 "derrière";3 "bas";3 "devant";3 "plein";3 "fort";3
	 * "avant";3 "mal";3 "attendre";2 => FIXME (contrainte DB) "bref";2
	 * "sourire";2 "jeune";2 "rose";2 "intérieur";2
	 */
	List<Mot> findByMot(String mot);
	
	List<Mot> findAll();

	// @Query("select l from Liste l left join l.listeMots lm  where lm.mot = :mot and l.partitionPrimaire is true")
	// Liste findListePrimaire(@Param("mot") Mot mot);
	
}
