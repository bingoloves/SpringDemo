package cn.cqs.spring.test;

import cn.cqs.spring.bean.User;
import cn.cqs.spring.utils.HttpUtils;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiTest {

    private static final Logger logger = LoggerFactory.getLogger(ApiTest.class);

    @Test
    public void testGetData() {
        String url = "http://localhost:8080/api/getUserList";
        String rs = HttpUtils.sendGet(url,null);
        System.out.println(rs);
    }
    @Test
    public void testPostData() {
        String url = "http://localhost:8080/api/login";
        User user = new User();
        user.setName("徐睿");
        user.setPassword("123456");
        String json = JSON.toJSONString(user);
        System.out.println(json);
        try {
            String rs = HttpUtils.sendHttpPost(url, json);
            System.out.println("rs = "+rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
