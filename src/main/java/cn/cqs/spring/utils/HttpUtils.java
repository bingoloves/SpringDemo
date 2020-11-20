package cn.cqs.spring.utils;

import com.google.common.collect.Multiset;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

public class HttpUtils {
    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url 发送请求的URL
     * @return URL 所代表远程资源的响应结果
     */
    public static String doGet(String url) {
        String result = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("Content-Type","application/json");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
//            for (String key : map.keySet()) {
//                System.out.println(key + "--->" + map.get(key));
//            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 参数类型转换
     *
     * @param parameters
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String paramsConvert(Map<String, Object> parameters) throws UnsupportedEncodingException {
        if (parameters == null) return "";
        StringBuffer sb = new StringBuffer();// 处理请求参数
        // 编码请求参数
        for (String name : parameters.keySet()) {
            Object object = parameters.get(name);
            if(object instanceof Integer || object instanceof Long || object instanceof Float || object instanceof Double) {
                sb.append(name).append("=").append(java.net.URLEncoder.encode(String.valueOf(object), "UTF-8")).append("&");
            } else if(object instanceof Date) {
                Calendar cal = Calendar.getInstance();
                cal.setTime((Date)object);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                sb.append(name).append("=").append(java.net.URLEncoder.encode(sdf.format(cal.getTime()), "UTF-8")).append("&");
            }else{
                if(object.getClass().isArray()) {//判断是否是字符串数组
                    String[] values = (String[])object;
                    for(String va : values) {
                        sb.append(name).append("=").append(java.net.URLEncoder.encode((String) va, "UTF-8")).append("&");
                    }
                }else {
                    sb.append(name).append("=").append(java.net.URLEncoder.encode((String) parameters.get(name),"UTF-8")).append("&");
                }
            }
        }
        return sb.toString().substring(0, sb.toString().length()-1);
    }

    /**
     * 发起GET请求
     * @param url
     * @param parameters
     * @return
     */
    public static String sendGet(String url,Map<String, Object> parameters) {
        String result = null,params = "";
        try {
           params = paramsConvert(parameters);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String urlParam = TextUtils.isEmpty(params)? url : url+"?"+params;
        System.out.println("urlParam = "+urlParam);
        HttpGet httpGet = new HttpGet(urlParam);
        CloseableHttpResponse response = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            response = httpClient.execute(httpGet);
            //int status = response.getStatusLine().getStatusCode();
            HttpEntity httpEntity = response.getEntity();
            result = EntityUtils.toString(httpEntity, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    /**
     * Post请求 包含图片上传
     * @param url
     * @param requestParams
     * @param filePathAndName
     * @return
     * @throws Exception
     */
    public static String sendHttpPost(String url, Map<String, String> requestParams, String filePathAndName)
            throws Exception {
        String result;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        /** HttpPost */
        HttpPost httpPost = new HttpPost(url);
        MultipartEntity reqEntity = new MultipartEntity(); // 建立多文件实例
        FileBody filebody = new FileBody(new File(filePathAndName));
        reqEntity.addPart("pic", filebody);// upload为请求后台的File upload;
        for (String key : requestParams.keySet()) {
            String value = requestParams.get(key);
            reqEntity.addPart(key, new StringBody(value, Charset.forName("utf-8")));
        }
        httpPost.setEntity(reqEntity); // 设置实体
        /** HttpResponse */
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        try {
            HttpEntity httpEntity = httpResponse.getEntity();
            result = EntityUtils.toString(httpEntity, "utf-8");
            EntityUtils.consume(httpEntity);
        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Post请求 多参数
     * @param url
     * @param requestParams
     * @return
     * @throws Exception
     */
    public static String sendHttpPost(String url, Map<String, String> requestParams) throws Exception {
        String result;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        /** HttpPost */
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        Iterator<Map.Entry<String, String>> it = requestParams.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> en = it.next();
            String key = en.getKey();
            String value = en.getValue();
            if (value != null) {
                params.add(new BasicNameValuePair(key, value));
            }
        }
        httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        /** HttpResponse */
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
        try {
            HttpEntity httpEntity = httpResponse.getEntity();
            result = EntityUtils.toString(httpEntity, "utf-8");
            EntityUtils.consume(httpEntity);
        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Post 请求 请求数据body
     * @param url
     * @param JSONBody
     * @return
     * @throws Exception
     */
    public static String sendHttpPost(String url, String JSONBody) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(JSONBody));
        CloseableHttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        String responseContent = EntityUtils.toString(entity, "UTF-8");
        response.close();
        httpClient.close();
        return responseContent;
    }

}
