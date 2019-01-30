package com.key.jorigin.spring.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CityRepository extends ElasticsearchRepository<City, Long> {
}
