package ru.javaops.masterjava.export;

import com.google.common.base.Preconditions;

import java.util.Arrays;
import java.util.List;

/**
 * Created by dmitriy_varygin on 12.11.16.
 */
public class Util {

    private Util() {

    }

    public static List<String> getGroupNamesFromString(final String groupNamesAsString) {
        return Arrays.asList(Preconditions.checkNotNull(groupNamesAsString)
                .split(" "));
    }
}
