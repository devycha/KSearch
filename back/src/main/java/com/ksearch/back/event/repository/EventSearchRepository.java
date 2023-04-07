package com.ksearch.back.event.repository;

import com.ksearch.back.event.entity.EventDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventSearchRepository extends ElasticsearchRepository<EventDocument, Long> {
    List<EventDocument> findAllByTitleIsContainingOrDescriptionIsContaining(String value1, String value2);
    List<EventDocument> findAllByTitleIsContaining(String value);

}
