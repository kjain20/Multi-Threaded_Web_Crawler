package edu.upenn.cis.cis455.URLInfo;

import edu.upenn.cis.cis455.crawler.info.URLInfo;
import org.junit.Assert;
import org.junit.Test;

public class URLInfoTest {
     URLInfo urlInfo = new URLInfo("https://www.google.com:8080/abc");

     @Test
    public void testURLParse()
     {

         Assert.assertEquals("www.google.com",urlInfo.getHostName());
     }

     @Test public void testPort()
     {
         Assert.assertEquals(8080,urlInfo.getPortNo());
     }

     @Test public void testURL()
     {
         Assert.assertEquals("/abc",urlInfo.getFilePath());
     }
}
