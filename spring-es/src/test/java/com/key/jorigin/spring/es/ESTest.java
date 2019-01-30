package com.key.jorigin.spring.es;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.*;

public class ESTest {

    private ElasticsearchTemplate elasticsearchTemplate;

    public void test(Long documentId) {


        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .withFilter(boolQuery().must(termQuery("id", documentId)))
                .build();

        Page<User> sampleEntities =
                elasticsearchTemplate.queryForPage(searchQuery, User.class);
    }

    //Elasticsearch在处理大结果集时可以使用scan和scroll。在Spring Data Elasticsearch中，可以向下面那样使用ElasticsearchTemplate来使用scan和scroll处理大结果集
    public void test2() {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .withIndices("test-index")
                .withTypes("test-type")
                .withPageable(new PageRequest(0, 1))
                .build();

        String scrollId = elasticsearchTemplate.scan(searchQuery, 1000, false);
        List<User> sampleEntities = new ArrayList<User>();
        boolean hasRecords = true;
        while (hasRecords) {
            //<T> AggregatedPage<T> mapResults(SearchResponse var1, Class<T> var2, Pageable var3);
            Page<User> page = elasticsearchTemplate.scroll(scrollId, 5000L, new SearchResultMapper() {

                @Override
                public AggregatedPage<User> mapResults(SearchResponse response, Class clazz, Pageable pageable) {

                    List<User> chunk = new ArrayList<User>();
                    for (SearchHit searchHit : response.getHits()) {
                        if (response.getHits().getHits().length <= 0) {
                            return null;
                        }
                        User user = new User();
                        user.setId(searchHit.getId());
                        user.setMessage((String) searchHit.getSource().get("message"));
                        chunk.add(user);
                    }
//                    return new AggregatedPage<User>(chunk);
                    return null;
                }
            });

            if (page != null) {
                sampleEntities.addAll(page.getContent());
                hasRecords = page.hasNext();//.hasNextPage();
            } else {
                hasRecords = false;
            }
        }
    }

}
