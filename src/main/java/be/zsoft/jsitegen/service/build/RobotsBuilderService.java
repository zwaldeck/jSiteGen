package be.zsoft.jsitegen.service.build;

import be.zsoft.jsitegen.dto.robots.RobotUserAgent;
import be.zsoft.jsitegen.dto.robots.RobotsConfig;
import be.zsoft.jsitegen.exception.BuildException;
import be.zsoft.jsitegen.holder.ConfigHolder;
import be.zsoft.jsitegen.service.FileSystemService;
import be.zsoft.jsitegen.service.ShellColorHelper;
import lombok.RequiredArgsConstructor;
import org.jline.terminal.Terminal;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.file.Path;

@RequiredArgsConstructor

@Service
public class RobotsBuilderService {

    private final FileSystemService fileSystemService;
    private final Terminal terminal;
    private final ShellColorHelper shellColorHelper;

    public void buildRobotsTxt(Path outputPath) {
        StringBuilder sb = new StringBuilder();
        RobotsConfig config = ConfigHolder.getConfig().robotsConfig();

        if (config == null) {
            return;
        }

        config.agents().forEach(agent -> buildAgent(agent, sb));
        config.sitemaps().forEach(sitemap -> sb.append("Sitemap: %s".formatted(sitemap)).append("\n"));

        try {
            saveFile(sb.toString(), outputPath);
        } catch (IOException e) {
            throw new BuildException("An error occurred when generating the robots.txt: '%s'".formatted(e.getMessage()));
        }
    }

    private void buildAgent(RobotUserAgent agent, StringBuilder sb) {
        sb.append("User-agent: %s".formatted(agent.userAgent())).append("\n");

        if (!CollectionUtils.isEmpty(agent.allow())) {
            agent.allow().forEach(allow -> sb.append("Allow: %s".formatted(allow)).append("\n"));
        }

        if (!CollectionUtils.isEmpty(agent.disallow())) {
            agent.disallow().forEach(disallow -> sb.append("Disallow: %s".formatted(disallow)).append("\n"));
        }

        sb.append("\n\n");
    }

    private void saveFile(String robots, Path outputPath) throws IOException {
        Path savePath = Path.of(outputPath.toString(), "robots.txt");

        fileSystemService.writeFile(savePath, robots);

        terminal.writer().println(shellColorHelper.getSuccessMessage("Saved sitemap: %s".formatted(savePath)));
        terminal.flush();
    }
}
