package pers.sherry.shorturl;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ShortUrlApplicationTests {

    /**
     * 测试1: 测试自动生成短链接
     */
	@Test
	public void testAutoLong2Short() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost("http://localhost:6060/shortUrl/long2Short");
        post.addHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(Mock.LONG_URL));
        CloseableHttpResponse response = httpClient.execute(post);
        System.out.println(EntityUtils.toString(response.getEntity()));
    }

    /**
     * 测试2: 测试自定义生成短链接
     */
    @Test
    public void testCustomLong2Short() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost("http://localhost:6060/shortUrl/long2Short");
        post.addHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(Mock.LONG_AND_SHORT));
        CloseableHttpResponse response = httpClient.execute(post);
        System.out.println(EntityUtils.toString(response.getEntity()));
    }

    /**
     * 测试3: 在执行了【测试1】并成功后 测试能否重复插入短链接相同的
     */
    @Test
    public void testDuplicate() throws IOException {
        testAutoLong2Short();
    }

    /**
     * 测试4: 测试能否通过短链接跳转到长链接
     */
    @Test
    public void testVisit() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet("http://localhost:6060/s.my/1234");
        CloseableHttpResponse response = httpClient.execute(get);
        System.out.println(EntityUtils.toString(response.getEntity()));
    }

    /**
     * 测试5: 测试不存在的短链接能否跳转
     */
    @Test
    public void testNonexistent() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet("http://localhost:6060/s.my/xxxxxxxx");
        CloseableHttpResponse response = httpClient.execute(get);
        System.out.println(EntityUtils.toString(response.getEntity()));
    }

    /**
     * 测试6: 查询Redis中短链接访问次数
     */
    @Test
    public void testVisitCount() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet("http://localhost:6060/shortUrl/getShortUrlVisitCount/1234");
        CloseableHttpResponse response = httpClient.execute(get);
        System.out.println(EntityUtils.toString(response.getEntity()));
    }

    /**
     * 测试7: 不存在短链接访问次数
     */
    @Test
    public void testNonexistentVisitCount() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet("http://localhost:6060/shortUrl/getShortUrlVisitCount/xxxxxxx");
        CloseableHttpResponse response = httpClient.execute(get);
        System.out.println(EntityUtils.toString(response.getEntity()));
    }

    /**
     * 测试8: 异常测试, 保存长链接和短链接时, 无参数, 测试异常
     */
    @Test
    public void testException() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost("http://localhost:6060/shortUrl/long2Short");
        CloseableHttpResponse response = httpClient.execute(post);
        System.out.println(EntityUtils.toString(response.getEntity()));
    }

    /**
     * 测试9: 测试修改配置短链接长度
     */
    @Test
    public void testConfig() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPut put = new HttpPut("http://localhost:6060/shortUrl/configShortUrlLength/6");
        CloseableHttpResponse response = httpClient.execute(put);
        System.out.println(EntityUtils.toString(response.getEntity()));
    }

    /**
     * 测试10: 测试修改配置短链接长度不合法
     */
    @Test
    public void testConfigError() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPut put = new HttpPut("http://localhost:6060/shortUrl/configShortUrlLength/25");
        CloseableHttpResponse response = httpClient.execute(put);
        System.out.println(EntityUtils.toString(response.getEntity()));
    }


}

