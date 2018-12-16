package com.inspur.fabric.es;

//import com.squareup.okhttp.*;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2017/10/21
 */
public class HttpUtil {
    private static Log log = LogFactory.getLog(HttpUtil.class);

    private OkHttpClient client;

    private static HttpUtil instance;

    public static HttpUtil getInstance(){
        if (instance==null){
            synchronized (HttpUtil.class){
                if(null==instance){
                    instance = new HttpUtil();
                }
            }
        }
        return instance;
    }

    public HttpUtil(){
        client = new OkHttpClient();
    }

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public String doPut(String url,String json){
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).put(body).build();
        Response response = null;
        String res = "failed";
        try {
            response = client.newCall(request).execute();
            if(response.isSuccessful()){
                res = response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public String doDelete(String url){
        RequestBody body = RequestBody.create(JSON, "");
        Request request = new Request.Builder().url(url).delete(body).build();
        Response response = null;
        String res = "failed";
        try {
            response = client.newCall(request).execute();
            if(response.isSuccessful()){
                res = response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public String doPost(String url,String json){
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();
        Response response = null;
        String res = "failed";
        try {
            response = client.newCall(request).execute();
            if(response.isSuccessful()){
                res = response.body().string();
            }
        } catch (IOException e) {
            log.error("doPost-->",e);
        }
        return res;
    }

    public static void main(String[] args){
        String url = "http://10.10.10.107:9200/qc/art/_search";
        String json = "{\"from\":"+0+",\"size\":"+10+",\"_source\":{\"exclude\":[\"content\"]},\"query\":{\"multi_match\":{\"query\":\""+"电饭煲"+"\",\"fields\":[\"title\",\"content\"]}},\"highlight\":{\"pre_tags\":[\"<span style='color:#ff0000'>\"],\"post_tags\":[\"</span>\"],\"fields\":{\"title\":{}}}}";
//        String json = "{\"from\":0,\"size\":10,\"query\":{\"match\":{\"product_name\":\"电饭煲\"}}}";
//        String json = "{\"from\":0,\"size\":10,\"query\":{\"multi_match\":{\"query\":\"查询\",\"fields\":[\"title\",\"content\"]}},\"highlight\":{\"pre_tags\":[\"<span style='color:#ff0000'>\"],\"post_tags\":[\"</span>\"],\"fields\":{\"title\":{},\"content\":{}}}}";
//        String json = "{\"article_id\":\"1\",\"title\":\"使用filters优化查询\",\"author\":\"作者a\",\"content\":\"首先，正如读者所想，filters来做缓存是一个很不错的选择，ElasticSearch也提供了这种特殊的缓存，filter cache来存储filters得到的结果集。此外，缓存filters不需要太多的内存(它只保留一种信息，即哪些文档与filter相匹配)，同时它可以由其它的查询复用，极大地提升了查询的性能。设想你正运行如下的查询命令\",\"time\":\"20170101\"}";
//        String json = "{\"organize_id\":\"20171222104818990821\",\"organize_code\":\"91442000686409571K\",\"organize_name\":\"格力电器（中山）小家电制造有限公司\"}";
//        String json = "{\"product_id\":\"20171223134406538940546814593634\",\"product_code\":\"6937671717293\",\"product_name\":\"IH智能电饭煲\",\"product_brand\":\"大松（TOSOT）\",\"product_spec\":\"GDCF-4001Cf\",\"product_photo_url\":\"/res/qc/img/2018/01/05/2018010508562375506.jpg,/res/qc/img/2018/01/05/2018010508563159779.jpg,/res/qc/img/2018/01/05/2018010508563761549.jpg\",\"product_features\":\"IH电磁加热,整面盖可拆洗,复合传热内胆,三段智能保温,多种烹饪功能\"}";
//        String json = "{\"from\":0,\"size\":10,\"query\":{\"filtered\": {\"query\":{ \"match\":{ \"product_name\":\"IH\" }},\"filter\":{\"bool\":{\"must\":[]}}}}}";
//        String json = "{\"from\":0,\"size\":10,\"query\":{\"match\":{\"product_name\":\"IH\"}}}";
        String res = HttpUtil.getInstance().doPost(url, json);
        System.out.println("res-->"+res);
    }

}
