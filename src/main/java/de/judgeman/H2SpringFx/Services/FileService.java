package de.judgeman.H2SpringFx.Services;

import java.io.File;

/**
 * Created by Paul Richter on Thu 30/03/2020
 */
public class FileService {

    public static String getWorkingPath() {
        return new File(".").toURI().resolve(".").getPath();
    }

    public static String getJarName() {
        return new File(FileService.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
    }
}
