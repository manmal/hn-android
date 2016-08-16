package com.manuelmaly.hn.parser;

import com.manuelmaly.hn.App;
import com.manuelmaly.hn.Settings;
import com.manuelmaly.hn.model.HNFeed;
import com.manuelmaly.hn.model.HNPost;
import com.manuelmaly.hn.util.HNHelper;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class HNFeedParser extends BaseHTMLParser<HNFeed> {

    @Override
    public HNFeed parseDocument(Element doc) throws Exception {
        if (doc == null)
            return new HNFeed();
        
        String currentUser = Settings.getUserName(App.getInstance());

        ArrayList<HNPost> posts = new ArrayList<HNPost>();

        // clumsy, but hopefully stable query - first element retrieved is the
        // top table, we have to skip that:
        Elements tableRows = doc.select("table tr table tr");
        tableRows.remove(0);

        Elements nextPageURLElements = tableRows.select("a:matches(^More$)");

        // In case there are multiple "More" elements, select only the one which is a relative link:
        if (nextPageURLElements.size() > 1) {
            nextPageURLElements = nextPageURLElements.select("a[href^=/]");
        }

        String nextPageURL = null;
        if (nextPageURLElements.size() > 0)
            nextPageURL = HNHelper.resolveRelativeHNURL(nextPageURLElements.attr("href"));

        String url = null;
        String title = null;
        String author = null;
        int commentsCount = 0;
        int points = 0;
        String urlDomain = null;
        String postID = null;
        String upvoteURL = null;

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
                    url = HNHelper.resolveRelativeHNURL(e1.attr("href"));
                    urlDomain = getDomainName(url);
                    
                    Element e4 = rowElement.select("tr > td:eq(1) a").first();
                    if (e4 != null) {
                        upvoteURL = e4.attr("href");
                        if (!upvoteURL.contains("auth=")) // HN changed authentication
                            upvoteURL = null;
                        else
                            upvoteURL = HNHelper.resolveRelativeHNURL(upvoteURL);
                    }
                    break;
                case 1:
                    points = getIntValueFollowedBySuffix(rowElement.select("tr > td:eq(1) > span").text(), " p");
                    author = rowElement.select("tr > td:eq(1) > a[href*=user]").text();
                    Element e2 = rowElement.select("tr > td:eq(1) > a[href*=item]").last(); // assuming the the last link is the comments link
                    if (e2 != null) {
                        commentsCount = getIntValueFollowedBySuffix(e2.text(), " c");
                        if (commentsCount == BaseHTMLParser.UNDEFINED && e2.text().contains("discuss"))
                            commentsCount = 0;
                        postID = getStringValuePrefixedByPrefix(e2.attr("href"), "id=");
                    }
                    else
                        commentsCount = BaseHTMLParser.UNDEFINED;

                    posts.add(new HNPost(url, title, urlDomain, author, postID, commentsCount, points, upvoteURL));
                    break;
                default:
                    break;
            }
            
            if (endParsing)
                break;
        }

        return new HNFeed(posts, nextPageURL, Settings
                .getUserName(App.getInstance()));
    }
   

}
