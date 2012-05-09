package com.servicelibre.corpus.manager;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.entity.Liste;
import com.servicelibre.corpus.entity.ListeMot;
import com.servicelibre.corpus.entity.Mot;

@Transactional
@Deprecated
public interface ListeMotManager // extends JpaRepository<Liste, Long>
{

	ListeMot findOne(long listeMotId);
	
	List<ListeMot> findByListe(Liste liste);
	
	List<ListeMot> findByMot(Mot mot);
	
	ListeMot save(ListeMot listeMot);

}
