package com.manuelmaly.hn.parser;

import com.manuelmaly.hn.model.HNComment;
import com.manuelmaly.hn.model.HNPostComments;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.Serializable;
import java.util.ArrayList;

public class HNCommentsParser extends BaseHTMLParser<HNCommentsParser.Result> {

    @Override
    public Result parseDocument(Document doc) throws Exception {
        if (doc == null)
            return new Result(null, null);

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
            Element mainRowElement = tableRows.get(row).select("td:eq(2)").first();
            Element rowLevelElement = tableRows.get(row).select("td:eq(0)").first();
            if (mainRowElement == null)
                break;

            text = mainRowElement.select("span.comment > *:not(*:contains(reply))").html();

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

            comments.add(new HNComment(timeAgo, author, url, text, level, isDownvoted));

            if (endParsing)
                break;
        }

        String articleUrl = null;
        Element urlElement = doc.select("table td.title a").first();
        if(urlElement != null) articleUrl = urlElement.attr("href");

        return new Result(new HNPostComments(comments), articleUrl);
    }

    public static class Result implements Serializable {
        private static final long serialVersionUID = -6971776670433626807L;

        private HNPostComments comments;
        private String articleUrl;

        public Result(HNPostComments comments, String articleUrl) {
            this.comments = comments;
            this.articleUrl = articleUrl;
        }

        public HNPostComments getComments() {
            return comments;
        }

        public String getArticleUrl() {
            return articleUrl;
        }
    }
}
