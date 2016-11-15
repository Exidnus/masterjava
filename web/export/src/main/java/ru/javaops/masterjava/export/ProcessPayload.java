package ru.javaops.masterjava.export;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.val;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * gkislin
 * 15.11.2016
 */
public class ProcessPayload {
    private final CityExport cityExport = new CityExport();
    private final UserExport userExport = new UserExport();
    private final ProjectGroupExport projectGroupExport = new ProjectGroupExport();

    public GroupResult process(InputStream is, int chunkSize) throws XMLStreamException {
        val processor = new StaxStreamProcessor(is);
        val groups = projectGroupExport.process(processor);
        val cities = cityExport.process(processor);
        return userExport.process(processor, groups, cities, chunkSize);
    }

    public static class Result {
        private static final String OK = "OK";

        public String result = OK;

        public boolean isOK() {
            return OK.equals(result);
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    public static class ChunkResult extends Result {
        String startEmail;
        String endEmail;
        int size;

        public void setFail(String message) {
            result = message;
        }

        @Override
        public String toString() {
            return size == 1 ?
                    "Chunk (email='" + startEmail + "'): " + result :
                    "Chunk (startEmail='" + startEmail + '\'' + ", endEmail='" + endEmail + "', size:'" + size + "): " + result;
        }

        public static ChunkResult createWithFail(String email, String fail) {
            ChunkResult chunkResult = new ChunkResult(email, email, 1);
            chunkResult.setFail(fail);
            return chunkResult;
        }
    }

    public static class GroupResult extends Result {
        public List<ChunkResult> chunkResults = new ArrayList<>();
        public int successful;
        public int failed;

        protected void add(ChunkResult chunkResult) {
            chunkResults.add(chunkResult);
            if (chunkResult.isOK()) {
                successful += chunkResult.size;
            } else {
                failed += chunkResult.size;
                result = isOK() ? chunkResult.toString() : "------------------------\n" + chunkResult.toString();
            }
        }

        @Override
        public String toString() {
            return "Result (successful=" + successful + ", failed=" + failed + "): " + result;
        }
    }
}
