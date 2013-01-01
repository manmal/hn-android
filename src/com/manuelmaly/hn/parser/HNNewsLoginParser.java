package com.manuelmaly.hn.parser;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

import com.manuelmaly.hn.model.HNComment;
import com.manuelmaly.hn.model.HNFeed;
import com.manuelmaly.hn.model.HNPost;
import com.manuelmaly.hn.model.HNPostComments;

/**
 * Returns the hidden FNID form parameter returned by the HN login page.
 * @author manuelmaly
 *
 */
public class HNNewsLoginParser extends BaseHTMLParser<String> {

    @Override
    public String parseDocument(Document doc) throws Exception {
        if (doc == null)
            return null;

        Elements hiddenInput = doc.select("input[type=hidden]");
        if (hiddenInput.size() == 0)
            return null;
        
        return hiddenInput.get(0).attr("value");
    }

}
