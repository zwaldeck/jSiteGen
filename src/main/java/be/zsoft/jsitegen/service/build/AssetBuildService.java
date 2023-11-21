package be.zsoft.jsitegen.service.build;

import be.zsoft.jsitegen.exception.BuildException;
import be.zsoft.jsitegen.service.FileSystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;

@RequiredArgsConstructor

@Service
public class AssetBuildService {

    private final FileSystemService fileSystemService;

    public void buildAssets(Path sourcePath, Path outputDir) throws IOException {
        Path sourceAssetPath = Path.of(sourcePath.toString(), "assets");
        Path outputAssetPath = Path.of(outputDir.toString(), "assets");

        if (!fileSystemService.exists(sourceAssetPath)) {
            throw new BuildException("Could not locate the 'assets' folder in source directory '%s'".formatted(sourcePath));
        }

        if (!fileSystemService.exists(outputAssetPath)) {
            fileSystemService.createDirectories(outputAssetPath);
        }

        fileSystemService.copyDirectory(sourceAssetPath, outputAssetPath);
    }
}
