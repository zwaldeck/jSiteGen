package be.zsoft.jsitegen.service.build;

import be.zsoft.jsitegen.dto.ScssConfig;
import be.zsoft.jsitegen.exception.BuildException;
import be.zsoft.jsitegen.holder.ConfigHolder;
import be.zsoft.jsitegen.service.FileSystemService;
import be.zsoft.jsitegen.service.ShellColorHelper;
import com.sass_lang.embedded_protocol.OutputStyle;
import de.larsgrefer.sass.embedded.CompileSuccess;
import de.larsgrefer.sass.embedded.SassCompiler;
import de.larsgrefer.sass.embedded.SassCompilerFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.jline.terminal.Terminal;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@RequiredArgsConstructor

@Service
public class ScssCompilerService {

    private final Terminal terminal;
    private final ShellColorHelper shellColorHelper;
    private final FileSystemService fileSystemService;

    public void compileScss(Path input, Path output) {
        ScssConfig config = ConfigHolder.getConfig().scssConfig();

        if (config == null || !config.scss()) {
            return;
        }

        try (SassCompiler sassCompiler = SassCompilerFactory.bundled()) {
            for (String file : config.files()) {
                buildScssFile(file, input, output, sassCompiler);
            }
        } catch (Exception ex) {
            throw new BuildException("An error occurred while compiling scss: %s".formatted(ex.getMessage()), ex);
        }
    }

    private void buildScssFile(String fileLocation, Path input, Path output, SassCompiler compiler) throws Exception {
        Path inputPath = Path.of(input.toString(), fileLocation);
        String filename = FilenameUtils.removeExtension(FilenameUtils.getName(fileLocation)) + ".css";
        Path outputPath = Path.of(output.toString(), "assets", "css", filename);

        fileSystemService.createDirectories(outputPath.getParent());

        CompileSuccess success = compiler.compileFile(inputPath.toFile(), OutputStyle.COMPRESSED);
        String css = success.getCss();

        fileSystemService.writeFile(outputPath, css);

        terminal.writer().println(shellColorHelper.getSuccessMessage("Saved css file: %s".formatted(outputPath)));
        terminal.flush();
    }
}
