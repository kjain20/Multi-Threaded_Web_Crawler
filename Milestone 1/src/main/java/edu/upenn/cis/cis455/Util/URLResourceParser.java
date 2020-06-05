package edu.upenn.cis.cis455.Util;

import edu.upenn.cis.cis455.crawler.info.URLInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

import java.util.ArrayList;
import java.util.List;


//Parses data into "urlInfo" classes
public class URLResourceParser {

    public List<URLInfo> getUrls(String baseUrl, String data) {

        if(data == null) {
            return new ArrayList<>();
        }

        Document parsedDocument = Jsoup.parse(data, baseUrl);
        NodeVisitorImplementation visitor = new NodeVisitorImplementation();
        NodeTraversor traversor = new NodeTraversor(visitor);
        traversor.traverse(parsedDocument.body());
        return visitor.getUrlInfos();
    }





    public static class NodeVisitorImplementation implements NodeVisitor {

        private List<URLInfo> urlInfos;
        public NodeVisitorImplementation() {
            this.urlInfos = new ArrayList<>();
        }

        public List<URLInfo> getUrlInfos() {
            return urlInfos;
        }

        @Override
        public void head(Node node, int i) {
            if(node instanceof Element) {
                Element element = (Element) node;

                // parse anchor tag:
                if("a".equals(element.nodeName())) {
                    String urlInfo = element.absUrl("href");
                    if(urlInfo != null) {
                        //System.out.println(urlInfo);
                        urlInfos.add(new URLInfo(urlInfo));
                    }

                }
                ////System.out.println(node.nodeName());
            }
        }

        @Override
        public void tail(Node node, int i) {
        }

        public static void main(String[] args) {
            String data = "<!DOCTYPE html>"+
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
            URLResourceParser urlResourceParser = new URLResourceParser();
            //System.out.println(urlResourceParser.getUrls("https://www.domain.com",data));

        }
    }
}
