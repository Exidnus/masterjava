package ru.javaops.masterjava.xml.util;

import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * gkislin
 * 23.09.2016
 */
public class StaxStreamProcessorTest {
    @Test
    public void readCities() throws Exception {
        try (StaxStreamProcessor processor = getStaxStreamProcessor()) {
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
        try (StaxStreamProcessor processor = getStaxStreamProcessor()) {
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
        try (final StaxStreamProcessor processor = getStaxStreamProcessor()) {
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

    @Test
    public void shouldReadProjectsAndGroups() throws Exception {
        int countProjects = 0;
        int countGroups = 0;
        try (final StaxStreamProcessor processor = getStaxStreamProcessor()) {
            processor.doUntil(XMLEvent.START_ELEMENT, "Project");
            boolean isNextElementProject;
            do {
                final String description = processor.getElementValue("description");
                System.out.println(description);
                boolean isNextElementGroup = true;
                while (isNextElementGroup) {
                    final String nextElementName = processor.getNextElementName();
                    if (nextElementName.equals("Group")) {
                        System.out.println(processor.getAttribute("name"));
                    } else {
                        isNextElementGroup = false;
                    }
                    processor.getReader().next();
                }
                processor.getReader().next();
                final String nextElementName = processor.getReader().getLocalName();
                isNextElementProject = nextElementName.equals("Project");
            } while (isNextElementProject);
        }
//        assertEquals(countProjects, 2);
//        assertEquals(countGroups, 4);
    }

    private StaxStreamProcessor getStaxStreamProcessor() throws XMLStreamException, IOException {
        return new StaxStreamProcessor(Resources.getResource("payload.xml").openStream());
    }
}