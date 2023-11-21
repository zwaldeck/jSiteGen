package be.zsoft.jsitegen.dto;

import be.zsoft.jsitegen.dto.robots.RobotUserAgent;
import be.zsoft.jsitegen.dto.robots.RobotsConfig;

import java.util.List;

public record Config(
        String title,
        String keywords,
        String description,
        ScssConfig scssConfig,
        SitemapConfig sitemapConfig,
        TemplateConfig templateConfig,
        RobotsConfig robotsConfig
) {

    public boolean shouldGenerateSitemap() {
        return sitemapConfig != null && sitemapConfig.generateSitemap();
    }

    public boolean shouldCompileScss() {
        return scssConfig != null && scssConfig.scss();
    }

    public static Config aDefaultConfig(String name, boolean scss) {
        return new Config(
                name,
                "",
                "",
                new ScssConfig(scss, scss ? List.of("/assets/scss/style.scss") : null),
                new SitemapConfig(true, "https://example.com"),
                new TemplateConfig("/templates/post.peb"),
                new RobotsConfig(
                        List.of(new RobotUserAgent("*", null, List.of("/"))),
                        List.of("https://example.com/sitemap.xml")
                )
        );
    }
}
