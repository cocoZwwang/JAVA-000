package pers.cocoadel.learning.http;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HttpUrlInfoTest {


    @Test
    public void httpUrlInfoParseTest(){
        String url = "http://127.0.0.1:8080/hello";
        HttpUrlInfo httpUrlInfo = HttpUrlInfo.parse(url);
        assertEquals("http",httpUrlInfo.getScheme());
        assertEquals("127.0.0.1",httpUrlInfo.getHost());
        assertEquals(8080,httpUrlInfo.getPort());
        assertEquals("/hello",httpUrlInfo.getPath());

        url = "http://localhost/hello";
        httpUrlInfo = HttpUrlInfo.parse(url);
        assertEquals("http",httpUrlInfo.getScheme());
        assertEquals("localhost",httpUrlInfo.getHost());
        assertEquals(80,httpUrlInfo.getPort());
        assertEquals("/hello",httpUrlInfo.getPath());

        url = "http://localhost";
        httpUrlInfo = HttpUrlInfo.parse(url);
        assertEquals("http",httpUrlInfo.getScheme());
        assertEquals("localhost",httpUrlInfo.getHost());
        assertEquals(80,httpUrlInfo.getPort());
        assertEquals("/",httpUrlInfo.getPath());

        url = "https://localhost/hello";
        httpUrlInfo = HttpUrlInfo.parse(url);
        assertEquals("https",httpUrlInfo.getScheme());
        assertEquals("localhost",httpUrlInfo.getHost());
        assertEquals(443,httpUrlInfo.getPort());
        assertEquals("/hello",httpUrlInfo.getPath());
    }
}