package be.zsoft.jsitegen.service;

import be.zsoft.jsitegen.exception.WatchException;
import be.zsoft.jsitegen.service.build.BuildService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jline.terminal.Terminal;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor

@Service
public class WatcherService {

    private final FileSystemService fileSystemService;
    private final ConfigLoaderService configLoaderService;
    private final BuildService buildService;
    private final ShellColorHelper shellColorHelper;
    private final Terminal terminal;

    public void watchForChanges(@NonNull Path sourcePath, @NonNull Path outputPath, @NonNull String profile) {
        if (!fileSystemService.exists(sourcePath)) {
            throw new WatchException("Could not find input path: %s".formatted(sourcePath));
        }

        configLoaderService.loadConfig(sourcePath, profile);

        terminal.writer().println(shellColorHelper.getSuccessMessage("Started to watch directory: %s".formatted(sourcePath)));
        terminal.flush();

        try {
            watchSource(sourcePath, outputPath);
        } catch (IOException e) {
            throw new WatchException("Something has failed during the watch: %s".formatted(e.getMessage()));
        } catch (InterruptedException e) {
            // nothing we can do
        }
    }

    private void watchSource(Path sourcePath, Path outputPath) throws IOException, InterruptedException {
        try (WatchService watchService = fileSystemService.getNewWatchService()){
            fileSystemService.registerAllOnWatchService(sourcePath, watchService);

            WatchKey key = null;
            CompletableFuture<?> buildTask = null;
            while (true) {
                key = watchService.take();
                List<WatchEvent<?>> events = key.pollEvents();

                // TODO: Find a way to see if other changes during build are triggered... If so re-trigger build when done
                if ((buildTask == null || buildTask.isDone()) && !events.isEmpty()) {
                    buildTask = triggerBuild(sourcePath, outputPath);
                }

                if (!key.reset()) {
                    // Directory no longer accessible
                    break;
                }
            }
        }
    }

    private CompletableFuture<?> triggerBuild(Path sourcePath, Path outputPath) {
        terminal.writer().println(shellColorHelper.getSuccessMessage("Triggering a new build"));
        terminal.flush();

        return CompletableFuture.runAsync(() -> buildService.build(sourcePath, outputPath));
    }
}
