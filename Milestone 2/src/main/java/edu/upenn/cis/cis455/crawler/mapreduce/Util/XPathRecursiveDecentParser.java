package edu.upenn.cis.cis455.crawler.mapreduce.Util;

import edu.upenn.cis.cis455.crawler.mapreduce.Models.Step;
import edu.upenn.cis.cis455.crawler.mapreduce.Models.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XPathRecursiveDecentParser {
    String queryString;
    int currentPoint = 0;
    String mutateString;


    public XPathRecursiveDecentParser(String queryString) {
        this.queryString = queryString.trim();
        this.mutateString = queryString.trim();
    }

    public List<Step> getSteps() throws ParseException
    {
        List<Step> stepList = new ArrayList<>();
        do {
            removeEmptyChar();
            seeSlash();
            Step step = getStep();
            stepList.add(step);
        }while (this.mutateString.length() != 0);
        return stepList;
    }

    public void removeEmptyChar()
    {
        while (this.mutateString.length() >0 && Character.isWhitespace(this.mutateString.charAt(0)))
        {
            consume(1);
        }
    }

    public void seeSlash() throws ParseException
    {
        if(mutateString.charAt(0) == '/') {
            consume(1);
        }
        else {
            throw new ParseException("Step not found", currentPoint);
        }

    }

    public Step getStep() throws ParseException
    {
        removeEmptyChar();
        String nameOfTheNode = getNodeName();
        removeEmptyChar();
        List<Test> testList = getTestList();
        Step step = new Step(nameOfTheNode,testList);
        return step;

    }

    public String getNodeName() throws ParseException
    {
        Pattern nodeNamePattern = Pattern.compile("^[a-zA-Z_][a-z0-9A-Z_\\.\\:-]*");
        Matcher matcher = nodeNamePattern.matcher(mutateString);
        if(matcher.find())
        {
            int leftPoint = matcher.start();
            int rightPoint = matcher.end();
            String nodeName = mutateString.substring(leftPoint, rightPoint);
            consume(nodeName.length());
            return nodeName;
        }
        else
        {
            throw  new ParseException("Node Name is invalid", currentPoint);
        }

    }

    public List<Test> getTestList() throws ParseException
    {
        List<Test> testList = new ArrayList<>();
        int starIndex = this.currentPoint;
        while(true) {
            if(this.mutateString.equals("") || mutateString.length() == 0)
            {
                return testList;
            }
            if (mutateString.charAt(0) == '/') {
                return testList;
            }
            if(mutateString.charAt(0) != '[')
            {
                throw new ParseException("Text Node not formed",starIndex);
            }
            consume(1);
            Test test = getTest();
            testList.add(test);
            removeEmptyChar();
            if((this.mutateString.length() == 0) || this.mutateString.charAt(0) != ']') {
                reset(starIndex);
                throw new ParseException( "bad ending", starIndex);
            }
            consume(1);
        }

    }

    public Test getTest() throws ParseException
    {
        Test test = null;
        try {
             test = getTestType1();

        }
        catch(ParseException e)
        {
            try {
                 test = getTestType2();
            }
            catch (ParseException e2)
            {
                throw new ParseException("No Test Nodes found",0);
            }

        }

        return test;

    }

    public Test getTestType1() throws ParseException
    {
        int startIndex = this.currentPoint;
        Pattern patternForTest1 = Pattern.compile("text *?\\( *?\\) *?= *?");
        Matcher matcher = patternForTest1.matcher(this.mutateString);
        if(matcher.find())
        {
            int leftPoint = matcher.start();
            int rightPoint = matcher.end();
            String textString = mutateString.substring(leftPoint,rightPoint);
            consume(textString.length());
            removeEmptyChar();
            String quotesString = getTextFromQuotes();
            Test test = new Test(Test.TextType.EXACT,quotesString);
            return test;
        }
        else {
            reset(startIndex);
            throw  new ParseException("Not of Test type 1",startIndex);
        }
    }

    public Test getTestType2() throws ParseException
    {
        Pattern pattern = Pattern.compile("contains *?\\( *?text *?\\( *?\\) *?, *?");
        Matcher matcher = pattern.matcher(mutateString);
        int startIndex = currentPoint;
        if (matcher.find()) {
            int leftPoint = matcher.start();
            int rightPoint = matcher.end();
            if (leftPoint != 0) {
                reset(startIndex);
                throw new ParseException("No Text found", startIndex);
            }
            String textString = mutateString.substring(leftPoint,rightPoint);
            consume(textString.length());
            removeEmptyChar();
            String stringInQuotes = getTextFromQuotes();
            removeEmptyChar();
            if (mutateString.length() == 0) {
                reset(startIndex);
                throw new ParseException("Type 2 not found", startIndex);
            }
            consume(1);
            return new Test(Test.TextType.CONTAINS, stringInQuotes);

        } else {
            reset(startIndex);
            throw new ParseException("Type 2 not found", startIndex);
        }
    }


    public String getTextFromQuotes() throws ParseException
    {
        int startIndex = this.currentPoint;
        Pattern pattern = Pattern.compile("([\"'])(?:(?=(\\\\?))\\2.)*?\\1");
        Matcher matcher = pattern.matcher(mutateString);
        if (matcher.find()) {
            int leftPoint = matcher.start();
            int rightPoint = matcher.end();

            if (leftPoint != 0) {
                throw new ParseException("String inside quotes not present", startIndex);
            }
            // Parse and return the quoted string
            String stringInQuotes = mutateString.substring(leftPoint + 1, rightPoint - 1);
            consume(stringInQuotes.length() + 2); //2 ? 3
            return stringInQuotes;

        } else {
            throw new ParseException("No String found", startIndex);
        }
    }


    public void consume(int howmany) {
        this.mutateString = this.mutateString.substring(howmany);
        currentPoint += howmany;
    }

    public void reset(int index) {
        this.mutateString = this.queryString.substring(index);
    }

    public static void main(String[] args) throws ParseException {

//        String queryString = "/a/b[text() = \"hello\"][contains(text(), \"llo\")]/c";
//        XPathRecursiveDecentParser xPathRecursiveDecentParser = new XPathRecursiveDecentParser(queryString);
//        List<Step> stepsList = xPathRecursiveDecentParser.getSteps();
//        System.out.println(stepsList);

    }





}
