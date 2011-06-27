package com.servicelibre.corpus.manager;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.servicelibre.corpus.entity.CorpusMetadata;

@Transactional
public interface CorpusMetadataManager // extends JpaRepository<CorpusMetadata, Long>
{

	CorpusMetadata findOne(long corpusMetadataId);

	CorpusMetadata findByNom(String nom);

	List<CorpusMetadata> findByCorpusId(long corpusId);

	CorpusMetadata save(CorpusMetadata corpusMetadata);

}
