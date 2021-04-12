package spider.nns.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;

/**
 * @author lgc
 * @date 2021/4/12
 */
@Data
@Document(indexName = "enterprise")
public class Good implements Serializable {
    @Id
    private Integer id;
    private Integer productId;
    private String title;
    private String descr;
    private String type;
}
