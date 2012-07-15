package com.manuelmaly.hnreader.parser;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;

import org.w3c.dom.NodeList;

public abstract class BaseHTMLIntListParser extends BaseHTMLParser<List<Integer>> {

    @Override
    public List<Integer> parseNodeList(NodeList nodeList, XPath xpath) throws Exception {
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < nodeList.getLength(); i++)
            result.add(processValue(nodeList.item(i).getNodeValue()));
        return result;
    }
    
    public abstract Integer processValue(String value);
    
}
