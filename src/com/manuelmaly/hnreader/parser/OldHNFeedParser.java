package com.manuelmaly.hnreader.parser;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

import com.manuelmaly.hnreader.model.HNFeed;
import com.manuelmaly.hnreader.model.HNPost;

public class OldHNFeedParser extends BaseHTMLParser<HNFeed> {

    @Override
    public String getXpathExpressionForRawNodesList() {
        return "//table/tr[3]//table";
    }
    
    @Override
    public HNFeed parseNodeList(NodeList nodeList, XPath xpath) throws Exception {
        Node content = nodeList.item(0);
        
        if (content == null)
            return new HNFeed(new ArrayList<HNPost>());
        
        List<HNPost> posts = new ArrayList<HNPost>();
        List<String> postTitles = new PostTitlesParser().parse(content, xpath);
        List<String> postURLs = new PostURLsParser().parse(content, xpath);
        List<Integer> postPoints = new PostPointsParser().parse(content, xpath);
        List<String> postIDs = new PostIDsParser().parse(content, xpath);
        
        for (int i = 0; i < postTitles.size(); i++)
            try {
                posts.add(new HNPost(getSafe(postURLs, i), getSafe(postTitles, i), getDomainName(getSafe(postURLs, i)), "", getSafe(postIDs, i), 0, getSafe(postPoints, i)));
            } catch (Exception e) {
                Log.e("HNFeedParser", "Post could not be added :(", e);
            }
        
        return new HNFeed(posts); 
    }
    
    private class PostTitlesParser extends BaseHTMLStringListParser {
        public String getXpathExpressionForRawNodesList() {
            return "tr/td[3]/a[1]/text()";
        }
    }

    private class PostURLsParser extends BaseHTMLStringListParser {
        public String getXpathExpressionForRawNodesList() {
            return "tr/td[3]/a[1]/@href";
        }
    }
    
    private class PostIDsParser extends BaseHTMLStringListParser {
        public String getXpathExpressionForRawNodesList() {
            return "tr/td[2][@class=\"subtext\"]/a[3]/@href";
        }
        
        public String processValue(String value) {
            return value.substring(8);
        }
    }

    private class PostPointsParser extends BaseHTMLIntListParser {
        public String getXpathExpressionForRawNodesList() {
            return "tr/td[2][@class=\"subtext\"]/span/text()";
        }

        public Integer processValue(String value) {
            int pointsWordIdx = value.indexOf("points");
            if (pointsWordIdx >= 0) {
                String pointsValue = value.substring(0, pointsWordIdx - 1); // - 1 because of space char
                Log.i("HNFeedParser", "Points found:" + pointsValue);
                try {
                    return Integer.parseInt(pointsValue); 
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
            return null;
        }

    }
    
    
}
