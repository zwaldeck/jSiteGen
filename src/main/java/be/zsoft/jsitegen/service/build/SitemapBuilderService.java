package be.zsoft.jsitegen.service.build;

import be.zsoft.jsitegen.dto.Page;
import be.zsoft.jsitegen.exception.BuildException;
import be.zsoft.jsitegen.holder.ConfigHolder;
import be.zsoft.jsitegen.service.util.XmlWriterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor

@Service
public class SitemapBuilderService {

    private final XmlWriterService xmlWriterService;

    public void buildSitemap(List<Page> pages, Path outputPath) {
        try {
            Document doc = buildDocument(pages);
            xmlWriterService.saveDocument(doc, outputPath);
        } catch (Exception e) {
            throw new BuildException("An error occurred when generating the sitemap: '%s'".formatted(e.getMessage()));
        }
    }

    private Document buildDocument(List<Page> pages) throws ParserConfigurationException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        Element urlset = doc.createElement("urlset");
        addUrlSetAttributes(urlset);
        doc.appendChild(urlset);

        buildUrlTag(null, doc, urlset);
        pages.forEach(page -> buildUrlTag(page, doc, urlset));

        return doc;
    }

    private void buildUrlTag(Page page, Document doc, Element urlset) {
        Element url = doc.createElement("url");
        Element loc = doc.createElement("loc");
        Element lastmod = doc.createElement("lastmod");
        Element priority = doc.createElement("priority");

        if (page == null) {
            loc.setTextContent("%s".formatted(ConfigHolder.getConfig().sitemapConfig().domain()));
        } else {
            loc.setTextContent("%s%s%s.html".formatted(ConfigHolder.getConfig().sitemapConfig().domain(), page.relativePath(), page.filenameWithoutExtension()));
        }

        lastmod.setTextContent(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        priority.setTextContent(page == null || page.filenameWithoutExtension().equalsIgnoreCase("index") ? "1.00" : "0.50");

        url.appendChild(loc);
        url.appendChild(lastmod);
        url.appendChild(priority);
        urlset.appendChild(url);
    }

    private void addUrlSetAttributes(Element urlset) {
        urlset.setAttribute("xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");
        urlset.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        urlset.setAttribute("xmlns:schemaLocation", "http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd");
    }

}
