package ru.job4j.grabber.parse;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";
    public static final String PREFIX = "/vacancies?page=";
    public static final String SUFFIX = "&q=Java%20developer&type=all";
    private static final int PAGE = 5;
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list(String link) {
        List<Post> posts = new ArrayList<>();
        int pageNumber = 1;
        while (pageNumber <= PAGE) {
            try {
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
                    String postLink = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                    String data = dataElement.attr("datetime");
                    String description = retrieveDescription(postLink);
                    LocalDateTime created = dateTimeParser.parse(data);
                    posts.add(new Post(0, vacancyName, postLink, description, created));
                }
                pageNumber++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return posts;
    }

    private String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Elements rows = document.select(".faded-content__body");
        Element description = rows.select(".vacancy-description__text").first();
        return description.text();
    }
}