package org.openspg.idea.schema.util;

import java.io.File;

public class FileUtils {

    public static String separator() {
        if (File.separator.equals("\\")) {
            return "/";
        } else {
            return "";
        }
    }

}
