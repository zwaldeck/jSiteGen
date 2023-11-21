package be.zsoft.jsitegen.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Service
public class DefaultOptionResolver {

    private static final String DIST = "dist";

    public String getOrDefaultOutputDir(Optional<String> outputDir) {
        return outputDir.orElse(System.getProperty("user.dir") + File.separator + DIST);
    }

    public String getOrDefaultInputDir(Optional<String> inputDir) {
        return inputDir.orElse(System.getProperty("user.dir"));
    }

    public String getOrDefaultNewOutputDir(Optional<String> outputDir, String name) {
        return outputDir.orElse(System.getProperty("user.dir")) + File.separator + name;
    }
}
