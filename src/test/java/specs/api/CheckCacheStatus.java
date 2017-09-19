package specs.api;


import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;


public class CheckCacheStatus {
    private final String sUrl = "http://investors.level3.com/";

    @BeforeTest
    public void setUp() {

    }

    @Test(threadPoolSize = 1, invocationCount = 10, timeOut = 2000)
    public void checkCacheStatus() {
        Long id = Thread.currentThread().getId();
        System.out.println("Test method executing on thread with id: " + id);

        HttpGet httpGet = new HttpGet(sUrl);

        httpGet.setHeader(HttpHeaders.HOST, "investors.level3.com");
        httpGet.setHeader(HttpHeaders.CONTENT_TYPE, "text/html; charset=utf-8");
        httpGet.setHeader(HttpHeaders.CONNECTION, "keep-alive");
        httpGet.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:45.0) Gecko/20100101 Firefox/45.0");
        httpGet.setHeader(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        httpGet.setHeader(HttpHeaders.CACHE_CONTROL, "max-age=0");

        BasicCookieStore cookieStore = new BasicCookieStore();
        BasicClientCookie cookieSessionId = new BasicClientCookie("ASP.NET_SessionId", "0puw3swnsinu3jojxdo55b25"+id);
        cookieSessionId.setDomain("investors.level3.com");
        cookieSessionId.setPath("/");
        cookieSessionId.setAttribute("HTTP", "true");
        cookieStore.addCookie(cookieSessionId);

        //client = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
        HttpClient httpClient = HttpClientBuilder.create().build();

        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

        try {
            long start = System.currentTimeMillis();
            HttpResponse response = httpClient.execute(httpGet);
            long end = System.currentTimeMillis();
            long result = end-start;
            System.out.println("Round trip response time = " + result + " millis");

            int statusCode = response.getStatusLine().getStatusCode();

            System.out.println(id+": "+statusCode);

            Header[] headers = response.getAllHeaders();
            for (Header header : headers) {
                System.out.println("Key: " + header.getName()
                        + " ,Value: " + header.getValue()+"\n");
            }
            System.out.println("\n Done");

        } catch(IOException e)
        {
            e.printStackTrace();
        }


    }

    @AfterTest
    public void tearDown() {

    }

}
