package org.chadsquare.telegram_bot;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

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

    public Elements selectSimpleElement(Document doc, String cssSelector) {
        return doc.select(cssSelector);
    }
}
