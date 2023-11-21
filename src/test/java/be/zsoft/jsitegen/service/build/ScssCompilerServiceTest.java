package be.zsoft.jsitegen.service.build;

import be.zsoft.jsitegen.exception.BuildException;
import be.zsoft.jsitegen.fixtures.ConfigFixtures;
import be.zsoft.jsitegen.holder.ConfigHolder;
import be.zsoft.jsitegen.service.FileSystemService;
import be.zsoft.jsitegen.service.ShellColorHelper;
import com.sass_lang.embedded_protocol.OutputStyle;
import de.larsgrefer.sass.embedded.CompileSuccess;
import de.larsgrefer.sass.embedded.SassCompiler;
import de.larsgrefer.sass.embedded.SassCompilerFactory;
import org.jline.terminal.Terminal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScssCompilerServiceTest {

    @Mock
    private FileSystemService fileSystemService;

    @Mock
    private Terminal terminal;

    @Mock
    private ShellColorHelper shellColorHelper;

    @Mock
    private PrintWriter printWriter;

    @Mock
    private SassCompiler sassCompiler;

    @Mock
    private CompileSuccess compileSuccess;

    @InjectMocks
    private ScssCompilerService service;

    @Test
    void compileScss_noConfig() throws Exception {
        ConfigHolder.setConfig(ConfigFixtures.aValidConfigWithNoOptions());

        service.compileScss(Path.of("input"), Path.of("output"));

        verify(fileSystemService, never()).writeFile(any(Path.class), anyString());
    }

    @Test
    void compileScss_exception() throws Exception {
        ConfigHolder.setConfig(ConfigFixtures.aValidConfigWithAllOptions());

        doThrow(new IOException()).when(fileSystemService).createDirectories(Path.of("output", "assets", "css"));

        assertThrows(BuildException.class, () -> service.compileScss(Path.of("input"), Path.of("output")));
    }

    @Test
    void compileScss_success() throws Exception {
        ConfigHolder.setConfig(ConfigFixtures.aValidConfigWithAllOptions());

        try (MockedStatic<SassCompilerFactory> factory = mockStatic(SassCompilerFactory.class)) {
            factory.when(SassCompilerFactory::bundled).thenReturn(sassCompiler);

            when(sassCompiler.compileFile(any(File.class), eq(OutputStyle.COMPRESSED))).thenReturn(compileSuccess);
            when(compileSuccess.getCss()).thenReturn("css");
            when(terminal.writer()).thenReturn(printWriter);
            when(shellColorHelper.getSuccessMessage(anyString())).thenReturn("msg");

            service.compileScss(Path.of("input"), Path.of("output"));

            verify(fileSystemService).createDirectories(Path.of("output", "assets", "css"));
            verify(sassCompiler).compileFile(any(File.class), eq(OutputStyle.COMPRESSED));
            verify(compileSuccess).getCss();
            verify(fileSystemService).writeFile(Path.of("output", "assets", "css", "style.css"), "css");
            verify(printWriter).println(anyString());
            verify(shellColorHelper).getSuccessMessage(anyString());
            verify(terminal).flush();
        }
    }
}