package com.manuelmaly.hnreader.parser;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;

import org.w3c.dom.NodeList;

public abstract class BaseHTMLStringListParser extends BaseHTMLParser<List<String>> {

    @Override
    public List<String> parseNodeList(NodeList nodeList, XPath xpath) throws Exception {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < nodeList.getLength(); i++)
            result.add(processValue(nodeList.item(i).getNodeValue()));
        return result;
    }
    
    public String processValue(String value) {
        return value;
    }
    
}
