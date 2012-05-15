package com.servicelibre.repositories.corpus;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import com.servicelibre.entities.corpus.Corpus;
import com.servicelibre.entities.corpus.DocMetadata;

public interface DocMetadataRepository extends CrudRepository<DocMetadata, Long> {

	List<DocMetadata> findByCorpus(Corpus corpus);
	List<DocMetadata> findByCorpus(Corpus corpus, Sort sort);

}
