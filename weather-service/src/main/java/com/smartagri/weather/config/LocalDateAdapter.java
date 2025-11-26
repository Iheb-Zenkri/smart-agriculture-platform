package com.smartagri.weather.config;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE;

    @Override
    public LocalDate unmarshal(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateString, FORMATTER);
    }

    @Override
    public String marshal(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.format(FORMATTER);
    }
}
