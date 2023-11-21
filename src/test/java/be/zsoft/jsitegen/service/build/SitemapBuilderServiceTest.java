package be.zsoft.jsitegen.service.build;

import be.zsoft.jsitegen.fixtures.ConfigFixtures;
import be.zsoft.jsitegen.fixtures.PageFixtures;
import be.zsoft.jsitegen.holder.ConfigHolder;
import be.zsoft.jsitegen.service.util.XmlWriterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SitemapBuilderServiceTest {

    @Mock
    private XmlWriterService xmlWriterService;

    @InjectMocks
    private SitemapBuilderService service;

    @Captor
    private ArgumentCaptor<Document> docCaptor;

    @Test
    void buildSitemap_success() throws Exception {
        ConfigHolder.setConfig(ConfigFixtures.aValidConfigWithAllOptions());
        Path output = Path.of("output");

        service.buildSitemap(List.of(PageFixtures.anIndexPage(), PageFixtures.aBlogPost()), output);

        verify(xmlWriterService).saveDocument(docCaptor.capture(), eq(output));

        Document doc = docCaptor.getValue();
        Node urlset = doc.getElementsByTagName("urlset").item(0);
        assertNotNull(urlset);
        assertEquals(3, urlset.getChildNodes().getLength());

        // TODO: Check every url tag in urlset
    }
}