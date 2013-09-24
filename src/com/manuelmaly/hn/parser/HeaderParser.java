package com.manuelmaly.hn.parser;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HeaderParser extends BaseHTMLParser<String> {

    @Override
    public String parseDocument(Element doc) throws Exception {
        Elements headerRows = doc.select("tr");

        // Six rows means that this is just a Ask HN post or a poll with
        // no options.  In either case, the content we want is in the fourth row
        if (headerRows.size() == 6) {
            return headerRows.get(3).select("td").get(1).html();
        }

        return null;
    }

    
}
