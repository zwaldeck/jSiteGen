package be.zsoft.jsitegen.service;

import be.zsoft.jsitegen.dto.Config;
import be.zsoft.jsitegen.exception.ConfigException;
import be.zsoft.jsitegen.fixtures.ConfigFixtures;
import be.zsoft.jsitegen.holder.ConfigHolder;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigLoaderServiceTest {

    @Mock
    private YAMLMapper yamlMapper;

    @Mock
    private FileSystemService fileSystemService;

    @InjectMocks
    private ConfigLoaderService service;

    @BeforeEach
    public void setup() {
        ConfigHolder.setConfig(null);
    }

    @Test
    void loadConfig_fileNotFound() {
        when(fileSystemService.exists(any(Path.class))).thenReturn(false);

        assertThrows(ConfigException.class, () -> service.loadConfig(Path.of("input"), null));

        verify(fileSystemService).exists(Path.of("input", "config", "config.yaml"));

        assertNull(ConfigHolder.getConfig());
    }

    @Test
    void loadConfig_mapperException() throws Exception {
        when(fileSystemService.exists(any(Path.class))).thenReturn(true);
        when(yamlMapper.readValue(any(File.class), eq(Config.class))).thenThrow(new IOException());

        assertThrows(ConfigException.class, () -> service.loadConfig(Path.of("input"), null));

        verify(fileSystemService).exists(Path.of("input", "config", "config.yaml"));
        verify(yamlMapper).readValue(any(File.class), eq(Config.class));

        assertNull(ConfigHolder.getConfig());
    }

    @Test
    void loadConfig_sitemapValidationFailed() throws Exception {
        when(fileSystemService.exists(any(Path.class))).thenReturn(true);
        when(yamlMapper.readValue(any(File.class), eq(Config.class))).thenReturn(ConfigFixtures.anInvalidSitemapConfig());

        assertThrows(ConfigException.class, () -> service.loadConfig(Path.of("input"), null));

        verify(fileSystemService).exists(Path.of("input", "config", "config.yaml"));
        verify(yamlMapper).readValue(any(File.class), eq(Config.class));

        assertNull(ConfigHolder.getConfig());
    }

    @Test
    void loadConfig() throws Exception {
        when(fileSystemService.exists(any(Path.class))).thenReturn(true);
        when(yamlMapper.readValue(any(File.class), eq(Config.class))).thenReturn(ConfigFixtures.aValidConfigWithAllOptions());

        service.loadConfig(Path.of("input"), null);

        verify(fileSystemService).exists(Path.of("input", "config", "config.yaml"));
        verify(yamlMapper).readValue(any(File.class), eq(Config.class));

        assertEquals(ConfigFixtures.aValidConfigWithAllOptions(), ConfigHolder.getConfig());
    }
}