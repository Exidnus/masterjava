package ru.javaops.masterjava.export;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.javaops.masterjava.export.results.GroupResult;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by dmitriy_varygin on 12.11.16.
 */
@RequiredArgsConstructor
class ExportChain {

    @NonNull private final List<BaseExport> exports;

    String process(final InputStream is, final int chunkSize) {
        try (final StaxStreamProcessor processor = new StaxStreamProcessor(is)) {
            return exports.stream()
                    .map(element -> {
                        try {
                            return element.process(processor, chunkSize);
                        } catch (XMLStreamException e) {
                            return new GroupResult("") {
                                @Override
                                public String toString() {
                                    return "Failed export through " + element;
                                }
                            };
                        }
                    })
                    .map(GroupResult::toString)
                    .collect(Collectors.joining("<br/>"));
        } catch (XMLStreamException e) {
            return "Failed read all xml";
        }
    }
}
