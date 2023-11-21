package be.zsoft.jsitegen.service.newsite;

import be.zsoft.jsitegen.dto.Config;
import be.zsoft.jsitegen.exception.GenerateNewException;
import be.zsoft.jsitegen.service.FileSystemService;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GenerateNewSiteServiceTest {

    @Mock
    private YAMLMapper yamlMapper;

    @Mock
    private FileSystemService fileSystemService;

    @InjectMocks
    private GenerateNewSiteService service;

    @Test
    void generateNewSite_exceptionIsThrown() throws Exception {
        doThrow(new IOException()).when(fileSystemService).createDirectories(any(Path.class));

        assertThrows(GenerateNewException.class, () -> service.generateNewSite(Path.of("output"), true, "my_site"));
    }

    @Test
    void generateNewSite_success_scss() throws Exception {
        Path output = Path.of("output");

        service.generateNewSite(output, true, "my_site");

        verify(fileSystemService).createDirectories(output);
        verify(fileSystemService).createDirectories(Path.of("output", "assets", "scss"));
        verify(fileSystemService).copyFileFromResourcesToPath(
                "/initial-files/scss/style.scss",
                Path.of("output", "assets", "scss", "style.scss")
        );
        verify(fileSystemService).createDirectories(Path.of("output", "config"));
        verify(yamlMapper).writeValue(
                Path.of("output", "config", "config.yaml").toFile(),
                Config.aDefaultConfig("my_site", true)
        );
        verify(fileSystemService).copyDirectoryFromResourcesToPath(
                "/initial-files/templates",
                Path.of("output", "templates")
        );
        verify(fileSystemService).copyDirectoryFromResourcesToPath(
                "/initial-files/content",
                Path.of("output", "content")
        );
    }

    @Test
    void generateNewSite_success_css() throws Exception {
        Path output = Path.of("output");

        service.generateNewSite(output, false, "my_site");

        verify(fileSystemService).createDirectories(output);
        verify(fileSystemService).createDirectories(Path.of("output", "assets", "css"));
        verify(fileSystemService).copyFileFromResourcesToPath(
                "/initial-files/css/style.css",
                Path.of("output", "assets", "css", "style.css")
        );
        verify(fileSystemService).createDirectories(Path.of("output", "config"));
        verify(yamlMapper).writeValue(
                Path.of("output", "config", "config.yaml").toFile(),
                Config.aDefaultConfig("my_site", false)
        );
        verify(fileSystemService).copyDirectoryFromResourcesToPath(
                "/initial-files/templates",
                Path.of("output", "templates")
        );
        verify(fileSystemService).copyDirectoryFromResourcesToPath(
                "/initial-files/content",
                Path.of("output", "content")
        );
    }
}