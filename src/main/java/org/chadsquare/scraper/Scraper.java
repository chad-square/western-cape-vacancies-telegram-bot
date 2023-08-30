package org.chadsquare.scraper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Optional;

public class Scraper {

    public Scraper() {
    }

    public Document scrap(String url) {
        try {
            Connection connect = Jsoup.connect(url);
            connect.userAgent("Chrome");
            return connect.get();
        } catch (IOException ioe) {
            System.out.printf("A problem occurred reading webpage with url %s, exception: %s", url, ioe);
            return null;
        }
    }

    public Elements scrapUrlAndExtractElements(String url, String cssSelector) {
        System.out.printf("scraping url: %s\n", url);
        Optional<Document> optionalDocument = Optional.ofNullable(this.scrap(url));

        if (optionalDocument.isPresent()) {
            Document document1 = optionalDocument.get();
            return this.selectSimpleElement(document1, cssSelector);
        } else {
            return null;
        }
    }

    private Elements selectSimpleElement(Document doc, String cssSelector) {
        return doc.select(cssSelector);
    }
}
