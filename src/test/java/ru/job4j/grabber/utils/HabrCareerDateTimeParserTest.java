package ru.job4j.grabber.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class HabrCareerDateTimeParserTest {

    @Test
    void whenLineTransformObject() {
        String date = "2024-03-29T12:02:53";
        LocalDateTime expected = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);
        Assertions.assertEquals(expected, LocalDateTime.parse(date));
    }
}
