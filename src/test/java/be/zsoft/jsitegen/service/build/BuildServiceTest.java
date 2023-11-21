package be.zsoft.jsitegen.service.build;

import be.zsoft.jsitegen.dto.Page;
import be.zsoft.jsitegen.exception.BuildException;
import be.zsoft.jsitegen.fixtures.ConfigFixtures;
import be.zsoft.jsitegen.fixtures.PageFixtures;
import be.zsoft.jsitegen.holder.ConfigHolder;
import be.zsoft.jsitegen.service.FileSystemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuildServiceTest {

    @Mock
    private AssetBuildService assetBuildService;

    @Mock
    private PebbleBuilderService pebbleBuilderService;

    @Mock
    private SitemapBuilderService sitemapBuilderService;

    @Mock
    private RobotsBuilderService robotsBuilderService;

    @Mock
    private ScssCompilerService scssCompilerService;

    @Mock
    private FileSystemService fileSystemService;

    @InjectMocks
    private BuildService service;

    @Test
    void build_sourceNotFound() {
        Path sourcePath = Path.of("input");
        Path outputPath = Path.of("output");

        when(fileSystemService.exists(sourcePath)).thenReturn(false);

        assertThrows(BuildException.class, () -> service.build(sourcePath, outputPath));
    }

    @Test
    void build_ioException() throws Exception {
        Path sourcePath = Path.of("input");
        Path outputPath = Path.of("output");

        when(fileSystemService.exists(sourcePath)).thenReturn(true);
        when(fileSystemService.exists(outputPath)).thenReturn(true);
        doThrow(new IOException()).when(fileSystemService).deleteDirectory(outputPath);

        assertThrows(BuildException.class, () -> service.build(sourcePath, outputPath));

        verify(fileSystemService).exists(sourcePath);
        verify(fileSystemService).exists(outputPath);
    }

    @Test
    void build_withScssAndSitemapAndRobots_distDoesNotExists() throws Exception {
        ConfigHolder.setConfig(ConfigFixtures.aValidConfigWithAllOptions());

        Path sourcePath = Path.of("input");
        Path outputPath = Path.of("output");
        Path sourcePagesPath = Path.of("input", "content", "pages");
        Path sourcePostsPath = Path.of("input", "content", "posts");
        List<Page> allPages = List.of(PageFixtures.anIndexPage(), PageFixtures.aBlogPost());

        when(fileSystemService.exists(sourcePath)).thenReturn(true);
        when(fileSystemService.exists(outputPath)).thenReturn(false);
        when(fileSystemService.listFilesWithExtension(sourcePagesPath, "peb")).thenReturn(List.of(new File("index.html")));
        when(fileSystemService.listFilesWithExtension(sourcePostsPath, "peb")).thenReturn(List.of(new File("post1.peb")));

        service.build(sourcePath, outputPath);

        verify(fileSystemService).exists(sourcePath);
        verify(fileSystemService).exists(outputPath);
        verify(fileSystemService).createDirectories(outputPath);
        verify(fileSystemService, never()).deleteDirectory(outputPath);
        verify(fileSystemService).listFilesWithExtension(sourcePagesPath, "peb");
        verify(fileSystemService).listFilesWithExtension(sourcePostsPath, "peb");
        verify(assetBuildService).buildAssets(sourcePath, outputPath);
        verify(pebbleBuilderService).buildPebbleTemplates(allPages, outputPath);
        verify(scssCompilerService).compileScss(sourcePath, outputPath);
        verify(sitemapBuilderService).buildSitemap(allPages, outputPath);
        verify(robotsBuilderService).buildRobotsTxt(outputPath);
    }

    @Test
    void build_withoutScssAndSitemapAndRobots_distDoesExists() throws Exception{
        ConfigHolder.setConfig(ConfigFixtures.aValidConfigWithNoOptions());

        Path sourcePath = Path.of("input");
        Path outputPath = Path.of("output");
        Path sourcePagesPath = Path.of("input", "content", "pages");
        Path sourcePostsPath = Path.of("input", "content", "posts");
        List<Page> allPages = List.of(PageFixtures.anIndexPage(), PageFixtures.aBlogPost());

        when(fileSystemService.exists(sourcePath)).thenReturn(true);
        when(fileSystemService.exists(outputPath)).thenReturn(true);
        when(fileSystemService.listFilesWithExtension(sourcePagesPath, "peb")).thenReturn(List.of(new File("index.html")));
        when(fileSystemService.listFilesWithExtension(sourcePostsPath, "peb")).thenReturn(List.of(new File("post1.peb")));

        service.build(sourcePath, outputPath);

        verify(fileSystemService).exists(sourcePath);
        verify(fileSystemService).exists(outputPath);
        verify(fileSystemService, never()).createDirectories(outputPath);
        verify(fileSystemService).deleteDirectory(outputPath);
        verify(fileSystemService).listFilesWithExtension(sourcePagesPath, "peb");
        verify(fileSystemService).listFilesWithExtension(sourcePostsPath, "peb");
        verify(assetBuildService).buildAssets(sourcePath, outputPath);
        verify(pebbleBuilderService).buildPebbleTemplates(allPages, outputPath);
        verify(scssCompilerService, never()).compileScss(sourcePath, outputPath);
        verify(sitemapBuilderService, never()).buildSitemap(allPages, outputPath);
        verify(robotsBuilderService, never()).buildRobotsTxt(outputPath);
    }
}