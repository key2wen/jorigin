package com.key.jorigin.spring.es.component;

import com.key.jorigin.spring.es.entity.Prod;
import com.key.jorigin.spring.es.repository.ProdRepository;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class EsComponent {

    @Autowired
    ProdRepository prodRepository;

    Logger LOGGER = LoggerFactory.getLogger(EsComponent.class);


    /**
     * @param keyword
     * @param brandId
     * @param productCategoryId
     * @param pageNum
     * @param pageSize
     * @param sort
     * @return
     * @ApiOperation(value = "综合搜索、筛选、排序")
     * @ApiImplicitParam(name = "sort", value = "排序字段:0->按相关度；1->按新品；2->按销量；3->价格从低到高；4->价格从高到低",
     * defaultValue = "0", allowableValues = "0,1,2,3,4", paramType = "query", dataType = "integer")
     */
    public Page<Prod> searchProd(String keyword, Long brandId, Long productCategoryId, Integer pageNum, Integer pageSize, Integer sort) {

        Pageable pageable = new PageRequest(pageNum, pageSize);

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //分页
        nativeSearchQueryBuilder.withPageable(pageable);

        //过滤
        if (brandId != null || productCategoryId != null) {

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            if (brandId != null) {
                boolQueryBuilder.must(QueryBuilders.termQuery("brandId", brandId));
            }
            if (productCategoryId != null) {
                boolQueryBuilder.must(QueryBuilders.termQuery("categoryId", productCategoryId));
            }
            nativeSearchQueryBuilder.withFilter(boolQueryBuilder);
        }

        //搜索
        if (StringUtils.isEmpty(keyword)) {
            nativeSearchQueryBuilder.withQuery(QueryBuilders.matchAllQuery());
        } else {

            FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery()

                    .add(QueryBuilders.matchQuery("searchName", keyword),
                            ScoreFunctionBuilders.weightFactorFunction(10))
                    .add(QueryBuilders.matchQuery("searchBran", keyword),
                            ScoreFunctionBuilders.weightFactorFunction(5))
                    .add(QueryBuilders.matchQuery("keywords", keyword),
                            ScoreFunctionBuilders.weightFactorFunction(2))
                    .scoreMode("sum")
                    .setMinScore(2);

            nativeSearchQueryBuilder.withQuery(functionScoreQueryBuilder);
        }

        //排序
        if (sort == 1) {
            //按新品从新到旧
            nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("id").order(SortOrder.DESC));
        } else if (sort == 2) {
            //按销量从高到低
            nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("sale").order(SortOrder.DESC));
        } else if (sort == 3) {
            //按价格从低到高
            nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.ASC));
        } else if (sort == 4) {
            //按价格从高到低
            nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));
        } else {
            //按相关度
            nativeSearchQueryBuilder.withSort(SortBuilders.scoreSort().order(SortOrder.DESC));
        }
        nativeSearchQueryBuilder.withSort(SortBuilders.scoreSort().order(SortOrder.DESC));

        NativeSearchQuery searchQuery = nativeSearchQueryBuilder.build();

        LOGGER.info("DSL query:{}", searchQuery.getQuery().toString());

        LOGGER.info("DSL filter:{}", searchQuery.getFilter().toString());

        List<SortBuilder> sortList = searchQuery.getElasticsearchSorts();
        sortList.forEach(s -> LOGGER.info("DSL sort:{}", s.toString()));

        return prodRepository.search(searchQuery);
    }

}
