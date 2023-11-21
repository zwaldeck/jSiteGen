package be.zsoft.jsitegen.service.build;

import be.zsoft.jsitegen.dto.Page;
import be.zsoft.jsitegen.exception.BuildException;
import be.zsoft.jsitegen.service.FileSystemService;
import be.zsoft.jsitegen.service.ShellColorHelper;
import be.zsoft.jsitegen.service.util.WriterFactory;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import lombok.RequiredArgsConstructor;
import org.jline.terminal.Terminal;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor

@Service
public class PebbleBuilderService {

    private final PebbleEngine pebbleEngine;
    private final GlobalVariableResolver globalVariableResolver;
    private final Terminal terminal;
    private final ShellColorHelper shellColorHelper;
    private final FileSystemService fileSystemService;
    private final WriterFactory writerFactory;

    public void buildPebbleTemplates(List<Page> pages, Path outputPath) {
        Map<String, Object> context = globalVariableResolver.resolveGlobalVariables(pages);

        pages.forEach(page -> buildPage(page, outputPath, context));
    }

    private void buildPage(Page page, Path outputPath, Map<String, Object> context) {
        try {
            String postPath = page.isPost() ? "posts" : "";
            Path savePath = Path.of(outputPath.toString(), postPath, page.relativePath(), page.filenameWithoutExtension() + ".html");
            fileSystemService.createDirectories(savePath.getParent());

            Writer writer = writerFactory.getWriter(savePath);
            PebbleTemplate template = pebbleEngine.getTemplate(page.file().getAbsolutePath());
            template.evaluate(writer, context);

            terminal.writer().println(shellColorHelper.getSuccessMessage("Saved file: %s".formatted(savePath)));
            terminal.flush();
        } catch (IOException e) {
            throw new BuildException("Could not build page: %s/%s".formatted(page.relativePath(), page.filename()), e);
        }
    }
}
