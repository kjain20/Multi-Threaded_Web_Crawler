package edu.upenn.cis.cis455.Util;

import edu.upenn.cis.cis455.crawler.info.URLInfo;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class URLResourceParserTest {
    URLResourceParser urlResourceParser = new URLResourceParser();

    @Test
    public void testForURLParsing()
    {
        String webData = "<!DOCTYPE html>"+
            "<html>"+
            "<head>"+
            "    <title>Register account</title>"+
            "</head>"+
            "<body>"+
            "<h1>Create Account for Milestone 2</h1>"+
            "<p>Please register a user name and password</p>"+
            "<A HREF=\"crawltest/nytimes/\">The New York Times</A>"+
                "<A HREF=\"crawltest/bbc/\">BBC News</A>"+
            "<form method=\"POST\" action=\"/register\">"+
            "Name: <input type=\"text\" name=\"username\"/><br/>"+
            "Password: <input type=\"password\" name=\"password\"/><br/>"+
            "First Name: <input type = \"text\" name = \"firstName\"/><br/>"+
            "    Seconde Name: <input type = \"text\" name = \"secondName\"/><br/>"+
            "<input type=\"submit\" value=\"Create account\"/>"+
            "</form>"+
            ""+
            "</body>"+
            "</html>";
        Assert.assertEquals(urlResourceParser.getUrls("http://www.testdomain.com",webData).size(),2);

        //Assert.assertEquals("www.testdomain.com/crawltest/nytimes",urlResourceParser.getUrls("www.testdomain.com",webData).get(0).getURL().toString());
    }

    @Test
    public void testURL()
    {
        String webData = "<!DOCTYPE html>"+
                "<html>"+
                "<head>"+
                "    <title>Register account</title>"+
                "</head>"+
                "<body>"+
                "<h1>Create Account for Milestone 2</h1>"+
                "<p>Please register a user name and password</p>"+
                "<A HREF=\"crawltest/nytimes/\">The New York Times</A>"+
                "<A HREF=\"crawltest/bbc/\">BBC News</A>"+
                "<form method=\"POST\" action=\"/register\">"+
                "Name: <input type=\"text\" name=\"username\"/><br/>"+
                "Password: <input type=\"password\" name=\"password\"/><br/>"+
                "First Name: <input type = \"text\" name = \"firstName\"/><br/>"+
                "    Seconde Name: <input type = \"text\" name = \"secondName\"/><br/>"+
                "<input type=\"submit\" value=\"Create account\"/>"+
                "</form>"+
                ""+
                "</body>"+
                "</html>";

        List<URLInfo> urlInfoList =  urlResourceParser.getUrls("http://www.testdomain.com",webData);
        URLInfo instance = urlInfoList.remove(0);
        Assert.assertEquals(instance.getURL().toString(),"http://www.testdomain.com/crawltest/nytimes/");


    }
}
