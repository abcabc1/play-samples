package utils;

import models.es.Customer;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ElasticSearchUtil {

    String baseIndex = "mytest_user";
    String baseType = "_doc";
    RestHighLevelClient client;
    private final int connectTimeoutMillis = 1000;
    private final int socketTimeoutMillis = 30000;
    private final int connectionRequestTimeoutMillis = 500;
    private final int maxConnectPerRoute = 10;
    private final int maxConnectTotal = 30;

    public static void main(String[] arg) throws Exception {
        ElasticSearchUtil elasticSearchUtil = new ElasticSearchUtil();
        elasticSearchUtil.test();
        elasticSearchUtil.search();
    }

    public void test() {
        String[] ips = {"localhost:9200"};
        HttpHost[] httpHosts = new HttpHost[ips.length];
        for (int i = 0; i < ips.length; i++) {
            httpHosts[i] = HttpHost.create(ips[i]);
        }
        RestClientBuilder builder = RestClient.builder(httpHosts);
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(connectTimeoutMillis);
            requestConfigBuilder.setSocketTimeout(socketTimeoutMillis);
            requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeoutMillis);
            return requestConfigBuilder;
        });

        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setMaxConnTotal(maxConnectTotal);
            httpClientBuilder.setMaxConnPerRoute(maxConnectPerRoute);
            return httpClientBuilder;
        });
        client = new RestHighLevelClient(builder);
    }
    @SuppressWarnings("deprecation")
    public void search() throws Exception {
        SearchRequest request = new SearchRequest();
        request.indices(baseIndex);
        request.types(baseType);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("name", "c"));
        sourceBuilder.from(0);
        sourceBuilder.size(5);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        HighlightBuilder highlightBuilder = new HighlightBuilder().field("name");
        sourceBuilder.highlighter(highlightBuilder);
        request.source(sourceBuilder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits searchHits = response.getHits();
        long total = searchHits.getTotalHits();
        SearchHit[] searchHitArray = searchHits.getHits();
        List<Customer> data = new ArrayList<>();
        for(SearchHit hit : searchHitArray){
            Map<String, Object> source = hit.getSourceAsMap();
            Customer customer = new Customer();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            customer.cTime = simpleDateFormat.parse(source.get("c_time").toString());
            customer.name = source.get("name").toString();
            customer.roleId = Long.valueOf(source.get("role_id").toString());
            customer.roleName = source.get("role_name").toString();
            customer.id = Long.valueOf(hit.getId());
            Map<String, HighlightField> map = hit.getHighlightFields();
            System.out.println(map.get("name").getFragments()[0].toString());
//            qo.setHighlightFields(t,hit);
            data.add(customer);
        }
        client.close();
//        return new PageResult(data,Integer.parseInt(total+""),qo.getCurrentPage(),qo.getPageSize());
    }

    /*public void insertOrUpdate(Object o) throws Exception {
        Map map = BeanUtil.bean2Map(o);
        IndexRequest request = new IndexRequest(baseIndex, baseType, map.get("id")+"");
        request.source(map);
        client.index(request);
    }

    public void delete(Long id) throws Exception {
        DeleteRequest request = new DeleteRequest(baseIndex, baseType, id + "");
        client.delete(request);
    }

    public T get(Long id) throws Exception {
        GetRequest request = new GetRequest(baseIndex, baseType, id+"");
        GetResponse response = client.get(request);
        Map<String, Object> source = response.getSource();
        T t = BeanUtil.map2Bean(source, clazz);
        return t;
    }*/
}
