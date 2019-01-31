package com.key.jorigin.spring.es.repository;

import com.key.jorigin.spring.es.entity.Prod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProdRepository extends ElasticsearchRepository<Prod, Long> {

    Page<Prod> findByNameOrCountry(String name, String country, Pageable page);

    Page<Prod> findByNameAndCountry(String name, String country, Pageable page);
}
