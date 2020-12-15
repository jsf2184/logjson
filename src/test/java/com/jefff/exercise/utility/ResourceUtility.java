package com.jefff.exercise.utility;


import java.io.File;
import java.net.URL;

public class ResourceUtility {

    public static class Helper {

    }

    @SuppressWarnings({"ConstantConditions"})
    public static String getResourcePath(String name) throws Exception {
        File file = getResourceFile(name);
        if (file == null) {
            return null;
        }
        String absolutePath = file.getAbsolutePath();
        return absolutePath;
    }


    public static File getResourceFile(String name) throws Exception {
        Helper helper = new Helper();
        ClassLoader classLoader = helper.getClass().getClassLoader();
        URL resource = classLoader.getResource(name);
        if (resource == null) {
            throw new Exception(String.format("Unable to find resource with name: %s", name));
        }
        String fileName = resource.getFile();
        File res = new File(fileName);
        return res;
    }
}
