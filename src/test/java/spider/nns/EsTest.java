package spider.nns;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.test.context.junit4.SpringRunner;
import spider.nns.dao.EsRepository;
import spider.nns.entity.Good;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author lgc
 * @date 2021/4/12
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class EsTest {

    @Autowired
    EsRepository esRepository;

    @Qualifier("elasticsearchClient")
    @Autowired
    RestHighLevelClient restHighLevelClient;


    /**
     * 查询所有
     *
     * @throws IOException
     */
    @Test
    public void searchAll() throws IOException {
        SearchRequest searchRequest = new SearchRequest("enterprise");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        searchResponse.getHits().forEach(res -> {
            log.info(res.toString());
        });

    }

    /**
     * 精确查询termQuery
     *
     * @throws IOException
     */
    @Test
    public void termQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest("enterprise");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("title", "手机"));
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        searchResponse.getHits().forEach(res -> {
            log.info(res.toString());
        });

    }

    /**
     * 模糊查询matchQuery
     *
     * @throws IOException
     */
    @Test
    public void matchQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest("enterprise");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("title", "这是");
        sourceBuilder.query(matchQueryBuilder);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        searchResponse.getHits().forEach(res -> {
            log.info(res.toString());
        });
    }


    /**
     * 高亮查询
     *
     * @throws IOException
     */
    @Test
    public void highlight() throws IOException {

        SearchRequest searchRequest = new SearchRequest("enterprise");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.highlighter(highlightBuilder());
        searchSourceBuilder.query(new MatchQueryBuilder("title", "这是"));

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        printHits(searchResponse);
    }

    @Test
    public void queryByFirstname() {
        List<SearchHit<Good>> result = esRepository.findByTitle("一");
        result.forEach(res -> {
            Map<String, List<String>> highlightFields = res.getHighlightFields();
            List<String> highlight = highlightFields.get("title");
            highlight.forEach(System.out::println);
        });
    }

    private HighlightBuilder highlightBuilder() {
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field highlightTitle =
                new HighlightBuilder.Field("title");
        highlightBuilder.field(highlightTitle);
        highlightBuilder.preTags("<font size=\"3\" color=\"red\">");
        highlightBuilder.postTags("</font>");
        return highlightBuilder;
    }

    private void printHits(SearchResponse searchResponse) {
        SearchHits hits = searchResponse.getHits();
        for (org.elasticsearch.search.SearchHit hit : hits.getHits()) {
            Map<String,HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField highlight = highlightFields.get("title");
            Text[] fragments = highlight.fragments();
            String fragmentString = fragments[0].string();
            log.info(fragmentString);
        }
    }
}
