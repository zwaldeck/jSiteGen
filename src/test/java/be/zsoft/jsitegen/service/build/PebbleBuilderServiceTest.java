package be.zsoft.jsitegen.service.build;

import be.zsoft.jsitegen.dto.Page;
import be.zsoft.jsitegen.exception.BuildException;
import be.zsoft.jsitegen.fixtures.PageFixtures;
import be.zsoft.jsitegen.service.FileSystemService;
import be.zsoft.jsitegen.service.ShellColorHelper;
import be.zsoft.jsitegen.service.util.WriterFactory;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.cache.CacheKey;
import io.pebbletemplates.pebble.cache.PebbleCache;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.jline.terminal.Terminal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PebbleBuilderServiceTest {

    @Mock
    private PebbleEngine pebbleEngine;

    @Mock
    private PebbleCache<Object, PebbleTemplate> templateCache;

    @Mock
    private PebbleCache<CacheKey, Object> tagCache;

    @Mock
    private GlobalVariableResolver globalVariableResolver;

    @Mock
    private Terminal terminal;

    @Mock
    private ShellColorHelper shellColorHelper;

    @Mock
    private FileSystemService fileSystemService;

    @Mock
    private PrintWriter printWriter;

    @Mock
    private PebbleTemplate pebbleTemplate;

    @Mock
    private WriterFactory writerFactory;

    @Mock
    private Writer writer;

    @InjectMocks
    private PebbleBuilderService service;

    @BeforeEach
    void setup() {
        when(pebbleEngine.getTemplateCache()).thenReturn(templateCache);
        when(pebbleEngine.getTagCache()).thenReturn(tagCache);
    }

    @Test
    void buildPebbleTemplates_ioException() throws Exception{
        Map<String, Object> context = Map.of("title", "title");
        List<Page> pages = List.of(PageFixtures.anIndexPage(), PageFixtures.aBlogPost());
        Path outputPath = Path.of("output");

        when(globalVariableResolver.resolveGlobalVariables(pages)).thenReturn(context);
        doThrow(new IOException()).when(fileSystemService).createDirectories(outputPath);

        assertThrows(BuildException.class, () -> service.buildPebbleTemplates(pages, outputPath));

        verify(templateCache).invalidateAll();
        verify(tagCache).invalidateAll();
        verify(globalVariableResolver).resolveGlobalVariables(pages);
        verify(fileSystemService).createDirectories(outputPath);
    }

    @Test
    void buildPebbleTemplates_success() throws Exception {
        Map<String, Object> context = Map.of("title", "title");
        List<Page> pages = List.of(PageFixtures.anIndexPage(), PageFixtures.aBlogPost());
        Path outputPath = Path.of("output");

        when(globalVariableResolver.resolveGlobalVariables(pages)).thenReturn(context);
        when(writerFactory.getWriter(any(Path.class))).thenReturn(writer);
        when(pebbleEngine.getTemplate(anyString())).thenReturn(pebbleTemplate);
        when(terminal.writer()).thenReturn(printWriter);
        when(shellColorHelper.getSuccessMessage(anyString())).thenReturn("msg");

        service.buildPebbleTemplates(pages, outputPath);

        verify(templateCache).invalidateAll();
        verify(tagCache).invalidateAll();
        verify(globalVariableResolver).resolveGlobalVariables(pages);
        verify(fileSystemService, times(2)).createDirectories(any(Path.class));
        verify(writerFactory, times(2)).getWriter(any(Path.class));
        verify(pebbleEngine, times(2)).getTemplate(anyString());
        verify(pebbleTemplate, times(2)).evaluate(any(Writer.class), eq(context));
        verify(printWriter, times(2)).println(anyString());
        verify(shellColorHelper, times(2)).getSuccessMessage(anyString());
        verify(terminal, times(2)).flush();
    }
}