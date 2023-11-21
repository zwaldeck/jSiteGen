package be.zsoft.jsitegen.command;

import be.zsoft.jsitegen.service.ConfigLoaderService;
import be.zsoft.jsitegen.service.DefaultOptionResolver;
import be.zsoft.jsitegen.service.build.BuildService;
import be.zsoft.jsitegen.service.newsite.GenerateNewSiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.context.InteractionMode;

import java.nio.file.Path;
import java.util.Optional;

//TODO: Exception handling
@RequiredArgsConstructor

@Command
public class MainCommand {

    private final DefaultOptionResolver defaultOptionResolver;
    private final ConfigLoaderService configLoaderService;
    private final BuildService buildService;
    private final GenerateNewSiteService generateNewSiteService;

    @Command(command = "build", description = "Build the static site", interactionMode = InteractionMode.NONINTERACTIVE)
    public String build(
            @Option(required = false, longNames = "profile", shortNames = 'P', label = "Profile") Optional<String> configProfile,
            @Option(required = false, longNames = "input", shortNames = 'I', label = "Input dir") Optional<String> input,
            @Option(required = false, longNames = "output", shortNames = 'O', label = "Output dir") Optional<String> output
    ) {
        Path sourcePath = Path.of(defaultOptionResolver.getOrDefaultInputDir(input));
        Path outputPath = Path.of(defaultOptionResolver.getOrDefaultOutputDir(output));

        configLoaderService.loadConfig(sourcePath, configProfile.orElse(""));
        buildService.build(sourcePath, outputPath);

        return "Site successfully build";
    }

    @Command(command = "new", description = "Create a new directory where the initial files can get generated", interactionMode = InteractionMode.NONINTERACTIVE)
    public String newSite(
            @Option(required = true, longNames = "name", shortNames = 'N', label = "Name") String name,
            @Option(required = false, longNames = "scss", label = "scss") boolean scss,
            @Option(required = false, longNames = "output", shortNames = 'O', label = "Output dir") Optional<String> output
    ) {

        Path outputPath = Path.of(defaultOptionResolver.getOrDefaultNewOutputDir(output, name));

        generateNewSiteService.generateNewSite(outputPath, scss, name);

        return "New site files generated: %s".formatted(outputPath);
    }
}
