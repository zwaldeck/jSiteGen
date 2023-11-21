package be.zsoft.jsitegen.dto.robots;

import java.util.List;

public record RobotUserAgent(
        String userAgent,
        List<String> disallow,
        List<String> allow
) {
}
