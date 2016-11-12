package ru.javaops.masterjava.xml.util;

import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import static org.junit.Assert.assertEquals;

/**
 * gkislin
 * 23.09.2016
 */
public class StaxStreamProcessorTest {
    @Test
    public void readCities() throws Exception {
        try (StaxStreamProcessor processor =
                     new StaxStreamProcessor(Resources.getResource("payload.xml").openStream())) {
            XMLStreamReader reader = processor.getReader();
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLEvent.START_ELEMENT) {
                    if ("City".equals(reader.getLocalName())) {
                        System.out.println(reader.getElementText());
                    }
                }
            }
        }
    }

    @Test
    public void readCities2() throws Exception {
        try (StaxStreamProcessor processor =
                     new StaxStreamProcessor(Resources.getResource("payload.xml").openStream())) {
            String city;
            while ((city = processor.getElementValue("City")) != null) {
                System.out.println(city);
            }
        }
    }

    @Test
    public void shouldReadCitiesAndUsers() throws Exception {
        int countCities = 0;
        int countUsers = 0;
        try (final StaxStreamProcessor processor =
                     new StaxStreamProcessor(Resources.getResource("payload.xml").openStream())) {
            while (processor.doUntilAndStopIfAnotherElement(XMLEvent.START_ELEMENT, "City")) {
                countCities++;
            }
            while (processor.doUntilAndStopIfAnotherElement(XMLEvent.START_ELEMENT, "User")) {
                countUsers++;
            }
        }
        assertEquals(countCities, 4);
        assertEquals(countUsers, 6);
    }
}