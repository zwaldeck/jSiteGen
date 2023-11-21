package be.zsoft.jsitegen.service.build;

import be.zsoft.jsitegen.exception.BuildException;
import be.zsoft.jsitegen.service.FileSystemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetBuildServiceTest {

    @Mock
    private FileSystemService fileSystemService;

    @InjectMocks
    private AssetBuildService service;

    @Test
    void buildAssets_sourceNotFound() {
        Path sourceAssetPath = Path.of("input", "assets");

        when(fileSystemService.exists(sourceAssetPath)).thenReturn(false);

        assertThrows(BuildException.class, () -> service.buildAssets(Path.of("input"), Path.of("output")));
    }

    @Test
    void buildAssets_success() throws Exception {
        Path sourceAssetPath = Path.of("input", "assets");
        Path outputAssetPath = Path.of("output", "assets");

        when(fileSystemService.exists(sourceAssetPath)).thenReturn(true);
        when(fileSystemService.exists(outputAssetPath)).thenReturn(false);

        service.buildAssets(Path.of("input"), Path.of("output"));

        verify(fileSystemService).exists(sourceAssetPath);
        verify(fileSystemService).exists(outputAssetPath);
        verify(fileSystemService).createDirectories(outputAssetPath);
        verify(fileSystemService).copyDirectory(sourceAssetPath, outputAssetPath);
    }
}