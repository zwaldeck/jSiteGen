package be.zsoft.jsitegen.service.build;

import be.zsoft.jsitegen.fixtures.ConfigFixtures;
import be.zsoft.jsitegen.fixtures.PageFixtures;
import be.zsoft.jsitegen.holder.ConfigHolder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class GlobalVariableResolverTest {

    @InjectMocks
    private GlobalVariableResolver resolver;

    @Test
    void resolveGlobalVariables() {
        ConfigHolder.setConfig(ConfigFixtures.aValidConfigWithNoOptions());
        Map<String, Object> expected = Map.of(
                "contentPages", List.of(PageFixtures.anIndexPage()),
                "posts", List.of(PageFixtures.aBlogPost()),
                "pages", List.of(PageFixtures.anIndexPage(), PageFixtures.aBlogPost()),
                "title", "title",
                "keywords", "keyword1,keyword2",
                "description", "description"
        );

        assertEquals(expected, resolver.resolveGlobalVariables(List.of(PageFixtures.anIndexPage(), PageFixtures.aBlogPost())));
    }
}