package com.manuelmaly.hn.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.w3c.dom.Node;

import java.net.URI;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

public abstract class BaseHTMLParser<T> {
    
    public static final int UNDEFINED = -1;

    public T parse(String input) throws Exception {
      return parseDocument(Jsoup.parse(input));
    }

    public abstract T parseDocument(Element doc) throws Exception;

    public static String getDomainName(String url) {
        URI uri;
        try {
            uri = new URI(url);
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (Exception e) {
            return url;
        }
    }

    public static String getFirstTextValueInElementChildren(Element element) {
        if (element == null)
            return "";
        
        for (org.jsoup.nodes.Node node : element.childNodes())
            if (node instanceof TextNode)
                return ((TextNode) node).text();
        return "";
    }
    
    public static String getStringValue(String query, Node source, XPath xpath) {
        try {
            return ((Node)xpath.evaluate(query, source, XPathConstants.NODE)).getNodeValue();
        } catch (Exception e) {
            //TODO insert Google Analytics tracking here?
        }
        return "";
    }
    
    public static Integer getIntValueFollowedBySuffix(String value, String suffix) {
        if (value == null || suffix == null)
            return 0;

        int suffixWordIdx = value.indexOf(suffix);
        if (suffixWordIdx >= 0) {
            String extractedValue = value.substring(0, suffixWordIdx);
            try {
                return Integer.parseInt(extractedValue);
            } catch (NumberFormatException e) {
                return UNDEFINED;
            }
        }
        return UNDEFINED;
    }

    public static String getStringValuePrefixedByPrefix(String value, String prefix) {
        int prefixWordIdx = value.indexOf(prefix);
        if (prefixWordIdx >= 0) {
            return value.substring(prefixWordIdx + prefix.length());
        }
        return null;
    }

}
