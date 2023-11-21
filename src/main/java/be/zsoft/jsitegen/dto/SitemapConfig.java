package be.zsoft.jsitegen.dto;

public record SitemapConfig(
        boolean generateSitemap,
        String domain
) {
}
