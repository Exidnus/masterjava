package ru.javaops.masterjava.xml.util;

import lombok.Getter;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;

/**
 * gkislin
 * 23.09.2016
 */
public class StaxStreamProcessor implements AutoCloseable {
    private static final XMLInputFactory FACTORY = XMLInputFactory.newInstance();

    @Getter private final XMLStreamReader reader;
    private boolean isElementFound = false;

    public StaxStreamProcessor(InputStream is) throws XMLStreamException {
        reader = FACTORY.createXMLStreamReader(is);
    }

    public XMLStreamReader getReader() {
        return reader;
    }

    public boolean doUntil(int stopEvent, String value) throws XMLStreamException {
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == stopEvent) {
                if (value.equals(getValue(event))) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean doUntilAndStopIfAnotherElement(int stopEvent, String value) throws XMLStreamException {
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == stopEvent) {
                if (value.equals(getValue(event))) {
                    isElementFound = true;
                    return true;
                } else if (isElementFound) {
                    isElementFound = false;
                    break;
                }
            }
        }
        return false;
    }
    public String getAttribute(String name) throws XMLStreamException {
        int count = reader.getAttributeCount();
        for (int i = 0; i < count; i++) {
            if (reader.getAttributeLocalName(i).equals(name)) {
                return reader.getAttributeValue(i);
            }
        }
        return null;
    }

    public String doUntilAny(int stopEvent, String... values) throws XMLStreamException {
        int length = values.length;
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == stopEvent) {
                String xmlValue = getValue(event);
                for (String value : values) {
                    if (value.equals(xmlValue)) {
                        return xmlValue;
                    }
                }
            }
        }
        return null;
    }

    public String getNextElementName() throws XMLStreamException {
        reader.next();
        reader.next();
        return reader.getLocalName();
    }

    public String getValue(int event) throws XMLStreamException {
        return (event == XMLEvent.CHARACTERS) ? reader.getText() : reader.getLocalName();
    }

    public String getElementValue(String element) throws XMLStreamException {
        return doUntil(XMLEvent.START_ELEMENT, element) ? reader.getElementText() : null;
    }

    @Override
    public void close() {
        if (reader != null) {
            try {
                reader.close();
            } catch (XMLStreamException e) {
                // empty
            }
        }
    }
}
