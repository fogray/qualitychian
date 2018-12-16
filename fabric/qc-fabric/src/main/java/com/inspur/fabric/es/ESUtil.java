package com.inspur.fabric.es;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.inspur.pub.PropertiesFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author zhang_lan@inspur.com
 * @description
 * @date 2018/2/7
 */
public class ESUtil {
    private static final Log log = LogFactory.getLog(ESUtil.class);
    private static ESUtil instance;
    public static ESUtil getInstance(){
        if (null == instance){
            synchronized (ESUtil.class){
                if (null == instance){
                    instance = new ESUtil();
                }
            }
        }
        return instance;
    }
    
    private static final String host = PropertiesFactory.globalProps.getString("esurl");
    //private static final String host = "http://10.10.10.107:9200";
    //private static final String host = "http://10.10.10.114:9200";

    /**
     * 全文检索
     * @param param 搜索的文本
     * @param from 搜索结果开始位置
     * @param size 搜索结果数
     * @return
     */
    public String searchAll(String param, String from, String size){
        if (log.isDebugEnabled()){
            log.debug("ESUtil--searchAll--begin");
            log.debug("ESUtil--searchAll--param-->"+param);
        }

        String url = host+"/qc/_search";
        String json = "{\"from\":"+from+",\"size\":"+size+",\"query\":{\"multi_match\":{\"query\":\""+param+"\",\"fields\":[\"organize_name\",\"product_name\",\"process_name\",\"process_json\",\"news_title\",\"news_content\",\"news_keywords\"]}}}";
        if(null==param || "".equals(param)){
            url = host+"/qc/organize,product,process,news/_search";
            json = "{\"from\":"+from+",\"size\":"+size+",\"query\":{\"match_all\":{}},\"sort\":[{\"crt_time.keyword\":{\"order\":\"desc\"}}]}}";
        }

        String res = HttpUtil.getInstance().doPost(url,json);

        JSONObject retJson = new JSONObject();
        String ret = "";
        try {
            JSONObject jo1 = JSONObject.fromObject(res);
            JSONArray ja1 = jo1.getJSONObject("hits").getJSONArray("hits");
            int total = jo1.getJSONObject("hits").getInt("total");
            JSONArray retJa = new JSONArray();
            if (ja1.size()>0){
                for(int i=0; i<ja1.size(); i++){
                    JSONObject jo = ja1.getJSONObject(i).getJSONObject("_source");
                    retJa.add(jo);
                }
                retJson.put("total",total);
                retJson.put("data",retJa);
                ret = retJson.toString();
            }
        } catch (Exception e) {
            log.error("ESUtil--err-->", e);
        }

        return ret;
    }

    /**
     * 搜索商品
     * @param param 搜索的文本
     * @param from 搜索结果开始位置
     * @param size 搜索结果数
     * @return
     */
    public String searchProduct(String param, String from, String size){
        String url = host+"/qc/product/_search";
        String json = "{\"from\":"+from+",\"size\":"+size+",\"query\":{\"match\":{\"product_name\":\""+param+"\"}}}";
        if(null==param || "".equals(param)){
            json = "{\"from\":"+from+",\"size\":"+size+",\"query\":{\"match_all\":{}},\"sort\":[{\"crt_time.keyword\":{\"order\":\"desc\"}}]}}";
        }
        if (log.isDebugEnabled()){
            log.debug("ESUtil--searchProduct--url-->"+url);
            log.debug("ESUtil--searchProduct--json-->"+json);
        }
        String res = HttpUtil.getInstance().doPost(url,json);
        String ret = dealResult(res);
        return ret;
    }
    
    public String searchProductByStarLevel(String param, String from, String size){
        String url = host+"/qc/product/_search";
        String json = "{\"from\":"+from+",\"size\":"+size+",\"query\":{\"bool\": {\"must\": { \"multi_match\": { \"query\": \""+param+"\",\"fields\":[\"product_name\"]}},\"filter\": {\"bool\":{\"must\":[{\"terms\":{\"star_level\":[\"2\",\"3\"]}}]}}}}}";
        if(null==param || "".equals(param)){
            json = "{\"from\":"+from+",\"size\":"+size+",\"query\":{\"bool\": {\"must\": {\"match_all\":{}},\"filter\": {\"bool\":{\"must\":[{\"terms\":{\"star_level\":[\"2\",\"3\"]}}]}}}},\"sort\":[{\"is_top.keyword\":{\"order\":\"asc\"}}]}";
        }
        String res = HttpUtil.getInstance().doPost(url,json);
        String ret = dealResult(res);
        return ret;
    }

    /**
     * 搜索企业
     * @param param 搜索内容
     * @param from 搜索结果开始位置
     * @param size 搜索结果数
     * @return
     */
    public String searchOrganize(String param, String from, String size){
        String url = host+"/qc/organize/_search";
        String json = "{\"from\":"+from+",\"size\":"+size+",\"query\":{\"match\":{\"organize_name\":\""+param+"\"}}}";
        if(null==param || "".equals(param)){
            json = "{\"from\":"+from+",\"size\":"+size+",\"query\":{\"match_all\":{}},\"sort\":[{\"is_top.keyword\":{\"order\":\"asc\"}}]}}";
        }
        String res = HttpUtil.getInstance().doPost(url,json);
        String ret = dealResult(res);
        return ret;
    }

    /**
     * 搜索文章
     * @param param 搜索内容
     * @param from 搜索结果开始位置
     * @param size 搜索结果数
     * @return
     */
    public String searchArt(String param, String from, String size){
        String url = host+"/qc/art/_search";
        String json = "{\"from\":"+from+",\"size\":"+size+",\"_source\":{\"exclude\":[\"content\"]},\"query\":{\"multi_match\":{\"query\":\""+param+"\",\"fields\":[\"title\",\"content\"]}},\"highlight\":{\"pre_tags\":[\"<span style='color:#ff0000'>\"],\"post_tags\":[\"</span>\"],\"fields\":{\"title\":{}}}}";
        if(null==param || "".equals(param)){
            json = "{\"from\":"+from+",\"size\":"+size+",\"query\":{\"match_all\":{}}}";
        }
        String res = HttpUtil.getInstance().doPost(url,json);
        String ret = "{}";
        JSONObject retJson = new JSONObject();
        try {
            JSONObject jo1 = JSONObject.fromObject(res);
            JSONArray ja1 = jo1.getJSONObject("hits").getJSONArray("hits");
            int total = jo1.getJSONObject("hits").getInt("total");
            JSONArray retJa = new JSONArray();
            if (ja1.size()>0){
                for(int i=0; i<ja1.size(); i++){
                    JSONObject retJo = new JSONObject();
                    JSONObject jo = ja1.getJSONObject(i).getJSONObject("_source");
                    try{
                        JSONObject highlight = ja1.getJSONObject(i).getJSONObject("highlight");
                        retJo.put("highlight",highlight);
                    }catch (Exception e){
                    }
                    retJo.put("article_id",jo.getString("article_id"));
                    retJo.put("title",jo.getString("title"));
                    retJo.put("author",jo.getString("author"));
                    retJo.put("avatar",jo.getString("avatar"));
//                    retJo.put("content",jo.getString("content"));
                    retJo.put("time",jo.getString("time"));
                    retJa.add(retJo);
                }
            }
            retJson.put("total",total);
            retJson.put("data",retJa);
            ret = TextUtil.unicode2String(retJa.toString());
        } catch (Exception e) {
            log.error("ESUtil--searchArt--error-->",e);
        }
        return ret;
    }

    /**
     * 搜索工序
     * @param param
     * @param from
     * @param size
     * @return
     */
    public String searchProcess(String param, String from, String size){
        String url = host+"/qc/process/_search";
        String json = "{\"from\":"+from+",\"size\":"+size+",\"query\":{\"multi_match\":{\"query\":\""+param+"\",\"fields\":[\"process_name\",\"process_json\"]}}}";
        if(null==param || "".equals(param)){
            json = "{\"from\":"+from+",\"size\":"+size+",\"query\":{\"match_all\":{}},\"sort\":[{\"crt_time.keyword\":{\"order\":\"desc\"}}]}}";
        }
        String res = HttpUtil.getInstance().doPost(url,json);
        JSONObject retJson = new JSONObject();
        String ret = "";
        try {
            JSONObject jo1 = JSONObject.fromObject(res);
            JSONArray ja1 = jo1.getJSONObject("hits").getJSONArray("hits");
            int total = jo1.getJSONObject("hits").getInt("total");
            JSONArray retJa = new JSONArray();
            if (ja1.size()>0){
                for(int i=0; i<ja1.size(); i++){
                    JSONObject jo = ja1.getJSONObject(i).getJSONObject("_source");
                    retJa.add(jo);
                }
                retJson.put("total",total);
                retJson.put("data",retJa);
                ret = retJson.toString();
            }
        } catch (Exception e) {
            log.error("ESUtil--err-->", e);
        }
        
        return ret;
    }

    /**
     * 删除工序
     * @param id 文章id
     * @return
     */
    public String deleteProcess(String id){
        String url = host+"/qc/process/"+id;
        String res = HttpUtil.getInstance().doDelete(url);
        return res;
    }

    /**
     * 删除出文章
     * @param id 文章id
     * @return
     */
    public String deleteArt(String id){
        String url = host+"/qc/art/"+id;
        String res = HttpUtil.getInstance().doDelete(url);
        return res;
    }

    /**
     * 搜索新闻
     * @param param
     * @param from
     * @param size
     * @return
     */
    public String searchNews(String param, String from, String size){
        String url = host+"/qc/news/_search";
        String json = "{\"from\":"+from+",\"size\":"+size+",\"query\":{\"multi_match\":{\"query\":\""+param+"\",\"fields\":[\"news_title\",\"news_content\",\"news_keywords\"]}}}";
        if(null==param || "".equals(param)){
            json = "{\"from\":"+from+",\"size\":"+size+",\"query\":{\"match_all\":{}},\"sort\":[{\"crt_time.keyword\":{\"order\":\"desc\"}}]}";
        }
        String res = HttpUtil.getInstance().doPost(url,json);
        String ret = dealResult(res);
        return ret;
    }

    public String searchNewsByBlock(String param, String blockId, String from, String size){
        String url = host+"/qc/news/_search";
        String json = "{\"from\":"+from+",\"size\":"+size+",\"query\":{\"bool\": {\"must\": { \"multi_match\": { \"query\": \""+param+"\",\"fields\":[\"news_title\",\"news_content\",\"news_keywords\"]}},\"filter\": {\"bool\":{\"must\":[{\"term\":{\"block_id\":\""+blockId+"\"}}]}}}}}";
        if(null==param || "".equals(param)){
            json = "{\"from\":"+from+",\"size\":"+size+",\"query\":{\"bool\": {\"must\": {\"match_all\":{}},\"filter\": {\"bool\":{\"must\":[{\"term\":{\"block_id\":\""+blockId+"\"}}]}}}},\"sort\":[{\"is_top.keyword\":{\"order\":\"desc\"}},{\"crt_time.keyword\":{\"order\":\"desc\"}}]}";

        }
        String res = HttpUtil.getInstance().doPost(url,json);
        String ret = dealResult(res);
        return ret;
    }

    private String dealResult(String res){
        String ret = "{}";
        JSONObject retJson = new JSONObject();
        try {
            JSONObject jo1 = JSONObject.fromObject(res);
            JSONArray ja1 = jo1.getJSONObject("hits").getJSONArray("hits");
            int total = jo1.getJSONObject("hits").getInt("total");
            JSONArray retJa = new JSONArray();
            if (ja1.size()>0){
                for(int i=0; i<ja1.size(); i++){
                    JSONObject jo = ja1.getJSONObject(i).getJSONObject("_source");
                    retJa.add(jo);
                }
                retJson.put("total",total);
                retJson.put("data",retJa);
            }
            ret = TextUtil.unicode2String(retJson.toString());
        } catch (Exception e) {
            log.error("ESUtil--err-->", e);
        }
        return ret;
    }

    /**
     * 删除新闻
     * @param id 文章id
     * @return
     */
    public String deleteNews(String id){
        String url = host+"/qc/news/"+id;
        String res = HttpUtil.getInstance().doDelete(url);
        return res;
    }

    /**
     * 索引数据到es中
     * @param type 索引类型（art：文章，organize：企业，product：商品）
     * @param index 文档索引
     * @param param 文档内容
     * @return
     */
    public String uploadDataToEs(String type, String index, String param){
        if (log.isDebugEnabled()){
            log.debug("ESUtil--uploadDataToEs--begin");
            log.debug("ESUtil--uploadDataToEs--param-->"+param);
        }
        String url = host+"/qc/"+type+"/"+index;
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, param);
        Request request = new Request.Builder().url(url).post(body).build();
        Response response = null;
        String res = "failed";
        try {
            response = client.newCall(request).execute();
            if(response.isSuccessful()){
                res = response.body().string();
            }
        } catch (Exception e) {
            log.error("ESUtil--uploadDataToEs--err-->",e);
        }
        return res;
    }
    
    
    public String searchOrganizeHeight(String param, String from, String size){
        String url = host+"/qc/organize/_search";
        String json = "{\"from\":"+from+",\"size\":"+size+",\"query\":{\"match\":{\"organize_name\":\""+param+"\"}},\"highlight\":{\"pre_tags\":[\"<span style='color:#ff0000'>\"],\"post_tags\":[\"</span>\"],\"fields\":{\"organize_name\":{}}}}";
        if(null==param || "".equals(param)){
            json = "{\"from\":"+from+",\"size\":"+size+",\"query\":{\"match_all\":{}},\"sort\":[{\"crt_time.keyword\":{\"order\":\"desc\"}}]}}";
        }
        String res = HttpUtil.getInstance().doPost(url,json);
        String ret = "{}";
        JSONObject retJson = new JSONObject();
        try {
            JSONObject jo1 = JSONObject.fromObject(res);
            JSONArray ja1 = jo1.getJSONObject("hits").getJSONArray("hits");
            int total = jo1.getJSONObject("hits").getInt("total");
            JSONArray retJa = new JSONArray();
            if (ja1.size()>0){
                for(int i=0; i<ja1.size(); i++){
                    JSONObject retJo = new JSONObject();
                    JSONObject jo = ja1.getJSONObject(i).getJSONObject("_source");
                    try{
                        JSONObject highlight = ja1.getJSONObject(i).getJSONObject("highlight");
                        retJo.put("highlight",highlight);
                    }catch (Exception e){
                    }
                    
                    retJo.put("organize_id", jo.get("organize_id"));
                    retJo.put("organize_code", jo.get("organize_code"));
                    retJo.put("organize_name", jo.get("organize_name"));
                    retJo.put("organize_logo", jo.get("organize_logo"));
                    retJo.put("organize_addr", jo.get("organize_addr"));
                    //retJo.put("orgen_introduction", jo.get("orgen_introduction"));
                   
                    retJa.add(retJo);
                }
            }
            retJson.put("total",total);
            retJson.put("data",retJa);
            ret = TextUtil.unicode2String(retJa.toString());
        } catch (Exception e) {
            log.error("ESUtil--searchArt--error-->",e);
        }
        return ret;
    }
    
    
    /**
     * 搜索新闻通过时间范围和频道
     * @param param
     * @param from
     * @param size
     * @return
     */
    public String searchNewsList(String param, String from, String size,String blockId, String startTime,String endTime){
//    	startTime = "2018-07-01T00:00:00";
//    	endTime = "2018-07-23T23:59:59";
//    	blockId = "78";
        String url = host+"/qc/news/_search";
        String json = "{\"from\":"+from+",\"size\":"+size+",\"query\":{\"bool\": {\"must\": [{ \"multi_match\": { \"query\": \""+param+"\",\"fields\":[\"news_title\",\"news_content\",\"news_keywords\"]}},{\"range\":{\"entry_news_time\":{\"gte\":\""+startTime+"\",\"lte\":\""+endTime+"\"}}}],\"filter\": {\"bool\":{\"must\":[{\"term\":{\"block_id\":\""+blockId+"\"}}]}}}}}";
        if(null==param || "".equals(param)){
            json = "{\"from\":"+from+",\"size\":"+size+",\"query\":{\"bool\": {\"must\": {\"range\":{\"entry_news_time\":{\"gte\":\""+startTime+"\",\"lte\":\""+endTime+"\"}}},\"filter\": {\"bool\":{\"must\":[{\"term\":{\"block_id\":\""+blockId+"\"}}]}}}},\"sort\":[{\"is_top.keyword\":{\"order\":\"desc\"}},{\"crt_time.keyword\":{\"order\":\"desc\"}}]}";
        }
        String res = HttpUtil.getInstance().doPost(url,json);
        String ret = dealResult(res);
        return ret;
    }
}
