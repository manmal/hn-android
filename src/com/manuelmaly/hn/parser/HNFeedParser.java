package com.manuelmaly.hn.parser;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

import com.manuelmaly.hn.model.HNFeed;
import com.manuelmaly.hn.model.HNPost;

public class HNFeedParser extends BaseHTMLParser<HNFeed> {

    @Override
    public HNFeed parseDocument(Document doc) throws Exception {
        if (doc == null)
            return new HNFeed();

        ArrayList<HNPost> posts = new ArrayList<HNPost>();

        // clumsy, but hopefully stable query - first element retrieved is the
        // top table, we have to skip that:
        Elements tableRows = doc.select("table tr table tr");
        tableRows.remove(0);
        
        Elements nextPageURLElements = tableRows.select("a:matches(More)");
        String nextPageURL = null;
        if (nextPageURLElements.size() > 0)
            nextPageURL = resolveRelativeHNURL(nextPageURLElements.attr("href"));

        String url = null;
        String title = null;
        String author = null;
        int commentsCount = 0;
        int points = 0;
        String urlDomain = null;
        String postID = null;

        boolean endParsing = false;
        for (int row = 0; row < tableRows.size(); row++) {
            int rowInPost = row % 3;
            Element rowElement = tableRows.get(row);

            switch (rowInPost) {
                case 0:
                    Element e1 = rowElement.select("tr > td:eq(2) > a").first();
                    if (e1 == null) {
                        endParsing = true;
                        break;
                    }
                    
                    title = e1.text();
                    url = resolveRelativeHNURL(e1.attr("href"));
                    urlDomain = getDomainName(url);
                    break;
                case 1:
                    points = getIntValueFollowedBySuffix(rowElement.select("tr > td:eq(1) > span").text(), " p");
                    author = rowElement.select("tr > td:eq(1) > a[href*=user]").text();
                    Element e2 = rowElement.select("tr > td:eq(1) > a[href*=item]").first();
                    if (e2 != null) {
                        commentsCount = getIntValueFollowedBySuffix(e2.text(), " c");
                        if (commentsCount == BaseHTMLParser.UNDEFINED && e2.text().contains("discuss"))
                            commentsCount = 0;
                        postID = getStringValuePrefixedByPrefix(e2.attr("href"), "id=");
                    }
                    else
                        commentsCount = BaseHTMLParser.UNDEFINED;

                    posts.add(new HNPost(url, title, urlDomain, author, postID, commentsCount, points));
                    break;
                default:
                    break;
            }
            
            if (endParsing)
                break;
        }

        return new HNFeed(posts, nextPageURL);
    }
    
    public static String resolveRelativeHNURL(String url) {
        if (url == null)
            return null;
        
        String hnurl = "http://news.ycombinator.com/";
        
        if (url.startsWith("http") || url.startsWith("ftp")) {
            return url;
        } else if (url.startsWith("/"))
            return hnurl + url.substring(1);
        else
            return hnurl + url;
    }

}
