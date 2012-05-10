package com.servicelibre.corpus.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import com.servicelibre.corpus.entity.Corpus;
import com.servicelibre.corpus.entity.DocMetadata;

public interface DocMetadataRepository extends CrudRepository<DocMetadata, Long> {

	List<DocMetadata> findByCorpus(Corpus corpus);
	List<DocMetadata> findByCorpus(Corpus corpus, Sort sort);

}
