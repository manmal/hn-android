package com.manuelmaly.hn.parser;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Returns the hidden FNID form parameter returned by the HN login page.
 * @author manuelmaly
 *
 */
public class HNNewsLoginParser extends BaseHTMLParser<String> {

    @Override
    public String parseDocument(Element doc) throws Exception {
        if (doc == null)
            return null;

        Elements hiddenInput = doc.select("input[type=hidden]");
        if (hiddenInput.size() == 0)
            return null;
        
        return hiddenInput.get(0).attr("value");
    }

}
