package com.manuelmaly.hnreader.parser;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

import com.manuelmaly.hnreader.model.HNComment;
import com.manuelmaly.hnreader.model.HNPostComments;
import com.manuelmaly.hnreader.model.HNFeed;
import com.manuelmaly.hnreader.model.HNPost;

public class HNCommentsParser extends BaseHTMLParser<HNPostComments> {

    
    @Override
    public HNPostComments parseDocument(Document doc) throws Exception {
        if (doc == null)
            return new HNPostComments();

        ArrayList<HNComment> comments = new ArrayList<HNComment>();

        Elements tableRows = doc.select("table tr table tr:has(table)");

        String text = null;
        String author = null;
        int level = 0;
        String timeAgo = null;
        String url = null;
        Boolean isDownvoted = false;

        boolean endParsing = false;
        for (int row = 0; row < tableRows.size(); row++) {
            Element rowElement = tableRows.get(row).select("td:eq(2)").first();
            Element rowLevelElement = tableRows.get(row).select("td:eq(0)").first();
            if (rowElement == null)
                break;

            text = rowElement.select("span.comment > *:not(*:contains(reply))").html();
            
            String levelSpacerWidth = rowLevelElement.select("img").first().attr("width");
            if (levelSpacerWidth != null)
                level = Integer.parseInt(levelSpacerWidth) / 40;
            
            comments.add(new HNComment(timeAgo, author, url, text, level, isDownvoted));

            if (endParsing)
                break;
        }

        return new HNPostComments(comments);
    }
    
}
