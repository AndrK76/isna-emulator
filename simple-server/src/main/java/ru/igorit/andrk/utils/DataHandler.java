package ru.igorit.andrk.utils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.GregorianCalendar;

public class DataHandler {
    public static XMLGregorianCalendar toXmlDate(LocalDateTime date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(Timestamp.valueOf(date));
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static XMLGregorianCalendar toXmlDate(OffsetDateTime date) {
        return toXmlDate(date.toLocalDateTime());
    }

    public static OffsetDateTime toTimeWithTZ(XMLGregorianCalendar xmlDate) {
        var gcDate = xmlDate.toGregorianCalendar();
        var zdt = gcDate.toZonedDateTime();
        var instant = zdt.toInstant();
        return instant.atOffset(zdt.getOffset());
    }
}
