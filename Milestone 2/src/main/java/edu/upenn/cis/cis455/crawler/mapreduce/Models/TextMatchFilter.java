package edu.upenn.cis.cis455.crawler.mapreduce.Models;

public class TextMatchFilter {
    private Test.TextType textType;
    private String textString;

    public TextMatchFilter(Test.TextType filterType, String textString) {
        this.textType = filterType;
        this.textString = textString;
    }

    public Test.TextType getFilterType() {
        return textType;
    }

    public void setFilterType(Test.TextType filterType) {
        this.textType = filterType;
    }

    public String getTextString() {
        return textString;
    }

    public void setTextString(String textString) {
        this.textString = textString;
    }


}
