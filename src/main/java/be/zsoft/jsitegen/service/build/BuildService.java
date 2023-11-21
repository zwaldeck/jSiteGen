package be.zsoft.jsitegen.service.build;

import be.zsoft.jsitegen.dto.Page;
import be.zsoft.jsitegen.exception.BuildException;
import be.zsoft.jsitegen.holder.ConfigHolder;
import be.zsoft.jsitegen.service.FileSystemService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor

@Service
public class BuildService {

    private final AssetBuildService assetBuildService;
    private final PebbleBuilderService pebbleBuilderService;
    private final SitemapBuilderService sitemapBuilderService;
    private final RobotsBuilderService robotsBuilderService;
    private final ScssCompilerService scssCompilerService;
    private final FileSystemService fileSystemService;

    public void build(@NonNull Path sourcePath, @NonNull Path outputPath) {
        if (!fileSystemService.exists(sourcePath)) {
            throw new BuildException("Could not find input path: %s".formatted(sourcePath));
        }

        try {
            prepareOutputDir(outputPath);

            Path sourceContentPagesPath = Path.of(sourcePath.toString(), "content", "pages");
            Path sourceContentPostsPath = Path.of(sourcePath.toString(), "content", "posts");

            List<Page> contentPages = getAllPagesWithExtension(sourceContentPagesPath, "peb", false);
            List<Page> posts = getAllPagesWithExtension(sourceContentPostsPath, "peb", true);
            List<Page> allPages =  Stream.concat(contentPages.stream(), posts.stream()).toList();

            assetBuildService.buildAssets(sourcePath, outputPath);
            pebbleBuilderService.buildPebbleTemplates(allPages, outputPath);

            if (ConfigHolder.getConfig().shouldCompileScss()) {
                scssCompilerService.compileScss(sourcePath, outputPath);
            }

            if (ConfigHolder.getConfig().shouldGenerateSitemap()) {
                sitemapBuilderService.buildSitemap(allPages, outputPath);
            }

            if (ConfigHolder.getConfig().robotsConfig() != null) {
                robotsBuilderService.buildRobotsTxt(outputPath);
            }

        } catch (IOException e) {
            throw new BuildException("Error occurred during build: %s".formatted(e.getMessage()));
        }
    }

    private void prepareOutputDir(Path outputDir) throws IOException {
        if (!fileSystemService.exists(outputDir)) {
            fileSystemService.createDirectories(outputDir);
            return;
        }

        fileSystemService.deleteDirectory(outputDir);
    }

    private List<Page> getAllPagesWithExtension(Path searchPath, String extension, boolean isPost) {
        return fileSystemService.listFilesWithExtension(searchPath, extension).stream()
                .map(file -> Page.fromFile(file, searchPath.toString(), isPost))
                .collect(Collectors.toList());
    }
}
