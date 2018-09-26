/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.quartztest;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Koby
 */
public class FileSystem {

    private static Path getResourcesPath() {
        return Paths.get(new File(Thread.currentThread().getContextClassLoader().getResource("").getPath()).getAbsolutePath());
    }

    public static Path getQuartzHsqlScript() throws FileNotFoundException {
        Path p = getResourcesPath().resolve("quartz_tables.sql");
        if (p == null || !Files.exists(p)) {
            throw new FileNotFoundException("quartz_tables.sql not found");
        }
        return p;
    }
    
    public static Path getLogbackHsqlScript() throws FileNotFoundException {
        Path p = getResourcesPath().resolve("logback_tables.sql");
        if (p == null || !Files.exists(p)) {
            throw new FileNotFoundException("logback_tables.sql not found");
        }
        return p;
    }
    
    public static Path getApplicationHsqlScript() throws FileNotFoundException {
        Path p = getResourcesPath().resolve("application_tables.sql");
        if (p == null || !Files.exists(p)) {
            throw new FileNotFoundException("application_tables.sql not found");
        }
        return p;
    }
    
    public static Path getQuartzProperties() throws FileNotFoundException {
        Path p = getResourcesPath().resolve("quartz.properties");
        if (p == null || !Files.exists(p)) {
            throw new FileNotFoundException("quartz.properties not found");
        }
        return p;
    }
}
