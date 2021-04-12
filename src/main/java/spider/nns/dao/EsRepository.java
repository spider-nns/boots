package spider.nns.dao;

import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.annotations.HighlightParameters;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.repository.CrudRepository;
import spider.nns.entity.Good;

import java.util.List;

/**
 * @author lgc
 * @date 2021/4/12
 */
public interface EsRepository extends CrudRepository<Good, String> {

    @Highlight(fields = {
            @HighlightField(name = "descr")
    }, parameters = @HighlightParameters(
            preTags = "<strong>",
            postTags = "</strong>"
    ))
    List<SearchHit<Good>> findByTitle(String title);
}
