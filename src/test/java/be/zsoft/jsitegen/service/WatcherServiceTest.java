package be.zsoft.jsitegen.service;

import be.zsoft.jsitegen.exception.WatchException;
import be.zsoft.jsitegen.service.build.BuildService;
import org.jline.terminal.Terminal;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WatcherServiceTest {

    @Mock
    private FileSystemService fileSystemService;

    @Mock
    private ConfigLoaderService configLoaderService;

    @Mock
    private BuildService buildService;

    @Mock
    private ShellColorHelper shellColorHelper;

    @Mock
    private Terminal terminal;

    @Mock
    private PrintWriter printWriter;

    @Mock
    private WatchService watchService;

    @Mock
    private WatchKey watchKey;

    @InjectMocks
    private WatcherService watcherService;

    @Test
    void watchForChanges_inputDoesNotExists() {
        Path input = Path.of("input");
        Path output = Path.of("output");

        when(fileSystemService.exists(input)).thenReturn(false);

        assertThrows(WatchException.class, () -> watcherService.watchForChanges(input, output, "dev"));
    }

    @Test
    void watchForChanges_ioException() throws Exception {
        Path input = Path.of("input");
        Path output = Path.of("output");

        when(fileSystemService.exists(input)).thenReturn(true);
        when(fileSystemService.getNewWatchService()).thenThrow(IOException.class);
        when(terminal.writer()).thenReturn(printWriter);
        when(shellColorHelper.getSuccessMessage(anyString())).thenReturn("hello");

        assertThrows(WatchException.class, () -> watcherService.watchForChanges(input, output, "dev"));

        verify(fileSystemService).exists(input);
        verify(configLoaderService).loadConfig(input, "dev");
        verify(terminal).writer();
        verify(shellColorHelper).getSuccessMessage(anyString());
        verify(printWriter).println(anyString());
        verify(terminal).flush();
    }

    @Test
    @Disabled // TODO: Fix flaky test --> Works when running from IDE
    void watchForChanges_success() throws Exception {
        Path input = Path.of("input");
        Path output = Path.of("output");

        when(fileSystemService.exists(input)).thenReturn(true);
        when(fileSystemService.getNewWatchService()).thenReturn(watchService);
        when(terminal.writer()).thenReturn(printWriter);
        when(shellColorHelper.getSuccessMessage(anyString())).thenReturn("hello");
        when(watchService.take()).thenReturn(watchKey);
        when(watchKey.pollEvents()).thenReturn(List.of(mock(WatchEvent.class)));
        when(watchKey.reset()).thenReturn(false);

        watcherService.watchForChanges(input, output, "dev");

        verify(fileSystemService).exists(input);
        verify(configLoaderService).loadConfig(input, "dev");
        verify(terminal, times(2)).writer();
        verify(shellColorHelper, times(2)).getSuccessMessage(anyString());
        verify(printWriter, times(2)).println(anyString());
        verify(terminal, times(2)).flush();
        verify(fileSystemService).getNewWatchService();
        verify(fileSystemService).registerAllOnWatchService(input, watchService);
        verify(buildService).build(input, output);
    }

}