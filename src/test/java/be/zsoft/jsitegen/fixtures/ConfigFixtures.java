package be.zsoft.jsitegen.fixtures;

import be.zsoft.jsitegen.dto.Config;
import be.zsoft.jsitegen.dto.ScssConfig;
import be.zsoft.jsitegen.dto.SitemapConfig;
import be.zsoft.jsitegen.dto.TemplateConfig;
import be.zsoft.jsitegen.dto.robots.RobotUserAgent;
import be.zsoft.jsitegen.dto.robots.RobotsConfig;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class ConfigFixtures {

    public static Config anInvalidSitemapConfig() {
        return new Config(
          "title",
          "keyword1,keyword2",
          "description",
                new ScssConfig(false, List.of()),
                new SitemapConfig(true, null),
                new TemplateConfig("/templates/post.peb"),
                new RobotsConfig(
                        List.of(new RobotUserAgent("*", null, List.of("/"))),
                        List.of("https://example.com/sitemap.xml")
                )
        );
    }

    public static Config aValidConfigWithAllOptions() {
        return new Config(
                "title",
                "keyword1,keyword2",
                "description",
                new ScssConfig(true, List.of("assets/scss/style.scss")),
                new SitemapConfig(true, "https://example.com"),
                new TemplateConfig("/templates/post.peb"),
                new RobotsConfig(
                        List.of(new RobotUserAgent("*", null, List.of("/"))),
                        List.of("https://example.com/sitemap.xml")
                )
        );
    }

    public static Config aValidConfigWithNoOptions() {
        return new Config(
                "title",
                "keyword1,keyword2",
                "description",
                new ScssConfig(false, List.of()),
                new SitemapConfig(false, null),
                new TemplateConfig("/templates/post.peb"),
                null
        );
    }
}
