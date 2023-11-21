package be.zsoft.jsitegen.service.newsite;

import be.zsoft.jsitegen.dto.Config;
import be.zsoft.jsitegen.exception.GenerateNewException;
import be.zsoft.jsitegen.service.FileSystemService;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;

@RequiredArgsConstructor

@Service
public class GenerateNewSiteService {

    private final YAMLMapper yamlMapper;
    private final FileSystemService fileSystemService;

    public void generateNewSite(Path outputPath, boolean scss, String name) {
        try {
            fileSystemService.createDirectories(outputPath);

            generateCss(scss, outputPath);
            generateConfig(scss, name, outputPath);
            generateTemplates(outputPath);
            generateContent(outputPath);
        } catch (Exception e) {
            throw new GenerateNewException("Could not generate new project: %s".formatted(e.getMessage()));
        }
    }

    private void generateCss(boolean scss, Path outputPath) throws IOException {
        String input = scss ? "/initial-files/scss/style.scss" : "/initial-files/css/style.css";
        Path cssOutput = scss ? Path.of("assets", "scss", "style.scss") : Path.of("assets", "css", "style.css");
        Path fullCssOutput = Path.of(outputPath.toString(), cssOutput.toString());

        fileSystemService.createDirectories(fullCssOutput.getParent());
        fileSystemService.copyFileFromResourcesToPath(input, fullCssOutput);
    }

    private void generateConfig(boolean scss, String name, Path outputPath) throws IOException {
        Path fullConfigPath = Path.of(outputPath.toString(), "config", "config.yaml");
        Config config = Config.aDefaultConfig(name, scss);

        fileSystemService.createDirectories(fullConfigPath.getParent());
        yamlMapper.writeValue(fullConfigPath.toFile(), config);
    }

    private void generateTemplates(Path outputPath) throws Exception {
        Path fullOutputPath = Path.of(outputPath.toString(), "templates");
        fileSystemService.copyDirectoryFromResourcesToPath("/initial-files/templates", fullOutputPath);
    }

    private void generateContent(Path outputPath) throws Exception {
        Path fullOutputPath = Path.of(outputPath.toString(), "content");
        fileSystemService.copyDirectoryFromResourcesToPath("/initial-files/content", fullOutputPath);
    }
}
