package edu.upenn.cis.cis455.crawler.mapreduce.Models;

public class Test {
    private TextType textType;
    String text;

    public Test(TextType textType, String text) {
        this.textType = textType;
        this.text = text;
    }

    public TextType getTextType() {
        return textType;
    }

    public void setTextType(TextType textType) {
        this.textType = textType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static enum TextType
    {
        EXACT,
        CONTAINS;
    }

}
