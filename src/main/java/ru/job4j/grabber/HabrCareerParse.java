package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class HabrCareerParse {

    private static final String SOURCE_LINK = "https://career.habr.com";
    public static final String PREFIX = "/vacancies?page=";
    public static final String SUFFIX = "&q=Java%20developer&type=all";
    public static final int PAGE = 5;

    public static void main(String[] args) throws IOException {
        int pageNumber = 1;
        while (pageNumber <= PAGE) {
            String fullLink = "%s%s%d%s".formatted(SOURCE_LINK, PREFIX, pageNumber, SUFFIX);
            Connection connection = Jsoup.connect(fullLink);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            for (Element row : rows) {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                Element dataTime = row.select(".vacancy-card__date").first();
                Element dataElement = dataTime.child(0);
                String vacancyName = titleElement.text();
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                String data = dataElement.attr("datetime");
                HabrCareerParse habrCareerParse = new HabrCareerParse();
                System.out.printf("%s %s %s%n %s%n",
                        data, vacancyName, link, habrCareerParse.retrieveDescription(link));
            }
            pageNumber++;
        }
    }

    private String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Elements rows = document.select(".faded-content__body");
        Element description = rows.select(".vacancy-description__text").first();
        return description.text();
    }
}