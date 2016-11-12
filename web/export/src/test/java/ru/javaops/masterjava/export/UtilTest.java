package ru.javaops.masterjava.export;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by dmitriy_varygin on 12.11.16.
 */
public class UtilTest {

    @Test
    public void shouldGetGroupNamesFromString() throws Exception {
        final String groups1 = "topjava07 masterjava01";
        final long count = Util.getGroupNamesFromString(groups1)
                .stream()
                .peek(s -> assertTrue(s.equals("topjava07") || s.equals("masterjava01")))
                .count();
        assertEquals(count, 2);
    }

}