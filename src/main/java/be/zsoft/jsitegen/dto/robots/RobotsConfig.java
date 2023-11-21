package be.zsoft.jsitegen.dto.robots;

import java.util.List;

public record RobotsConfig(
        List<RobotUserAgent> agents,
        List<String> sitemaps
) {
}
