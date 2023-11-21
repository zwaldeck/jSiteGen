package be.zsoft.jsitegen.service.build;

import be.zsoft.jsitegen.exception.BuildException;
import be.zsoft.jsitegen.fixtures.ConfigFixtures;
import be.zsoft.jsitegen.holder.ConfigHolder;
import be.zsoft.jsitegen.service.FileSystemService;
import be.zsoft.jsitegen.service.ShellColorHelper;
import org.jline.terminal.Terminal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RobotsBuilderServiceTest {

    @Mock
    private FileSystemService fileSystemService;

    @Mock
    private Terminal terminal;

    @Mock
    private ShellColorHelper shellColorHelper;

    @Mock
    private PrintWriter printWriter;

    @InjectMocks
    private RobotsBuilderService service;

    @Test
    void buildRobotsTxt_noConfig() throws Exception {
        ConfigHolder.setConfig(ConfigFixtures.aValidConfigWithNoOptions());

        service.buildRobotsTxt(Path.of("output"));

        verify(fileSystemService, never()).writeFile(any(Path.class), anyString());
    }

    @Test
    void buildRobotsTxt_ioException() throws Exception {
        ConfigHolder.setConfig(ConfigFixtures.aValidConfigWithAllOptions());

        doThrow(new IOException()).when(fileSystemService).writeFile(any(Path.class), anyString());

        assertThrows(BuildException.class, () -> service.buildRobotsTxt(Path.of("output")));

        verify(fileSystemService).writeFile(any(Path.class), anyString());
    }

    @Test
    void buildRobotsTxt_success() throws Exception {
        ConfigHolder.setConfig(ConfigFixtures.aValidConfigWithAllOptions());

        when(terminal.writer()).thenReturn(printWriter);
        when(shellColorHelper.getSuccessMessage(anyString())).thenReturn("msg");

        service.buildRobotsTxt(Path.of("output"));

        verify(fileSystemService).writeFile(Path.of("output", "robots.txt"), getExpectedRobots());
        verify(printWriter).println(anyString());
        verify(shellColorHelper).getSuccessMessage(anyString());
        verify(terminal).flush();
    }

    private String getExpectedRobots() {
        StringBuilder sb = new StringBuilder();

        sb.append("User-agent: *\n")
                .append("Allow: /\n")
                .append("\n\n")
                .append("Sitemap: https://example.com/sitemap.xml\n");

        return sb.toString();
    }
}