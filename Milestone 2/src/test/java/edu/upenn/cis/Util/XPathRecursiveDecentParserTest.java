package edu.upenn.cis.Util;

import edu.upenn.cis.cis455.crawler.mapreduce.Models.Step;
import edu.upenn.cis.cis455.crawler.mapreduce.Util.XPathRecursiveDecentParser;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.text.ParseException;
import java.util.List;

public class XPathRecursiveDecentParserTest {



    @Test
    public void testValidXPath() throws ParseException
    {
        String queryString = "/a/b[text() = \"hello\"][contains(text(), \"llo\")]/c";
        XPathRecursiveDecentParser xPathRecursiveDecentParser = new XPathRecursiveDecentParser(queryString);
        List<Step> stepsList = xPathRecursiveDecentParser.getSteps();
        Assert.assertEquals(3,stepsList.size());
    }

    @Test(expected = ParseException.class)
    public void testForParseException() throws ParseException
    {
        String queryString = "/a/btext(), \"llo\")]c";
        XPathRecursiveDecentParser xPathRecursiveDecentParser = new XPathRecursiveDecentParser(queryString);
        List<Step> stepsList = xPathRecursiveDecentParser.getSteps();


    }
}
