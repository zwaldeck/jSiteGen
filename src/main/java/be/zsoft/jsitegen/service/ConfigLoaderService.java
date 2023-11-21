package be.zsoft.jsitegen.service;

import be.zsoft.jsitegen.dto.Config;
import be.zsoft.jsitegen.exception.ConfigException;
import be.zsoft.jsitegen.holder.ConfigHolder;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@RequiredArgsConstructor

@Service
public class ConfigLoaderService {

    private final YAMLMapper yamlMapper;
    private final FileSystemService fileSystemService;

    public void loadConfig(Path inputPath, String profile) {
        try {
            Config config = yamlMapper.readValue(getFileByProfile(inputPath, profile), Config.class);

            validateConfig(config);

            ConfigHolder.setConfig(config);
        } catch (IOException e) {
            throw new ConfigException("Could not map the config file for profile: %s".formatted(profile), e);
        }
    }

    private File getFileByProfile(Path inputPath, String profile) {
        String filename = StringUtils.hasText(profile) ? "config-%s.yaml".formatted(profile) : "config.yaml";
        Path filePath = Path.of(inputPath.toString(), "config", filename);

        if (!fileSystemService.exists(filePath)) {
            throw new ConfigException("Could not find config file with path: %s".formatted(filePath));
        }

        return filePath.toFile();
    }

    private void validateConfig(Config config) {
        if (config.shouldGenerateSitemap() && !StringUtils.hasText(config.sitemapConfig().domain())) {
            throw new ConfigException("When 'sitemapConfig.generateSitemap' is set to true the field 'sitemapConfig.domain' must be set");
        }
    }
}
