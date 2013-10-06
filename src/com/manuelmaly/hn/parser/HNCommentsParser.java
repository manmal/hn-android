package com.manuelmaly.hn.parser;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.manuelmaly.hn.App;
import com.manuelmaly.hn.Settings;
import com.manuelmaly.hn.model.HNComment;
import com.manuelmaly.hn.model.HNPostComments;
import com.manuelmaly.hn.util.HNHelper;

public class HNCommentsParser extends BaseHTMLParser<HNPostComments> {

    @Override
    public HNPostComments parseDocument(Document doc) throws Exception {
        if (doc == null)
            return new HNPostComments();

        ArrayList<HNComment> comments = new ArrayList<HNComment>();

        Elements tableRows = doc.select("table tr table tr:has(table)");

        String currentUser = Settings.getUserName(App.getInstance());
        
        String text = null;
        String author = null;
        int level = 0;
        String timeAgo = null;
        String url = null;
        Boolean isDownvoted = false;
        String upvoteUrl = null;
        String downvoteUrl = null;

        boolean endParsing = false;
        for (int row = 0; row < tableRows.size(); row++) {
            Element mainRowElement = tableRows.get(row).select("td:eq(2)").first();
            Element rowLevelElement = tableRows.get(row).select("td:eq(0)").first();
            if (mainRowElement == null)
                break;

            // The not portion of this query is meant to remove the reply link
            // from the text.  As far as I can tell that is the only place
            // where size=1 is used.  If that turns out to not be the case then
            // searching for u tags is also a pretty decent option - @jmaltz
            text = mainRowElement.select("span.comment > *:not(:has(font[size=1]))").html();

            Element comHeadElement = mainRowElement.select("span.comhead").first();
            author = comHeadElement.select("a[href*=user]").text();
            String timeAgoRaw = getFirstTextValueInElementChildren(comHeadElement);
            if (timeAgoRaw.length() > 0)
                timeAgo = timeAgoRaw.substring(0, timeAgoRaw.indexOf("|"));
            Element urlElement = comHeadElement.select("a[href*=item]").first();
            if (urlElement != null)
                url = urlElement.attr("href");

            String levelSpacerWidth = rowLevelElement.select("img").first().attr("width");
            if (levelSpacerWidth != null)
                level = Integer.parseInt(levelSpacerWidth) / 40;

            Elements voteElements = tableRows.get(row).select("td:eq(1) a");
            upvoteUrl = getVoteUrl(voteElements.first(), currentUser);

            // We want to test for size because unlike first() calling .get(1)
            // Will throw an error if there are not two elements
            if (voteElements.size() > 1)
               downvoteUrl = getVoteUrl(voteElements.get(1), currentUser);

            comments.add(new HNComment(timeAgo, author, url, text, level, isDownvoted, upvoteUrl, downvoteUrl));

            if (endParsing)
                break;
        }

        return new HNPostComments(comments);
    }

    /**
     * Parses out the url for voting from a given element
     * @param voteElement The element from which to parse out the voting url
     * @param currentUser The currently logged in user
     * @return The relative url to vote in the given direction for that comment
     */
    private String getVoteUrl(Element voteElement, String currentUser) {
        if (voteElement != null) {
            return voteElement.attr("href").contains(currentUser) ?
                HNHelper.resolveRelativeHNURL(voteElement.attr("href")) : null;
        }

        return null;
    }

}
