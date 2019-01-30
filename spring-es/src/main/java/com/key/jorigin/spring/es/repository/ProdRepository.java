package com.key.jorigin.spring.es.repository;

import com.key.jorigin.spring.es.entity.Prod;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProdRepository extends ElasticsearchRepository<Prod, Long> {

}
