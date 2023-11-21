package be.zsoft.jsitegen.service.build;

import be.zsoft.jsitegen.dto.Config;
import be.zsoft.jsitegen.dto.Page;
import be.zsoft.jsitegen.holder.ConfigHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor

@Service
public class GlobalVariableResolver {


    public Map<String, Object> resolveGlobalVariables(List<Page> allPages) {
        Map<String, Object> variables = new HashMap<>();

        loadConfigVariables(variables);

        variables.put("contentPages", allPages.stream().filter(p -> !p.isPost()).toList());
        variables.put("posts", allPages.stream().filter(Page::isPost).toList());
        variables.put("pages", allPages);

        return variables;
    }

    private void loadConfigVariables(Map<String, Object> variables) {
        Config config = ConfigHolder.getConfig();

        if (config == null) return;

        variables.put("title", config.title());
        variables.put("keywords", config.keywords());
        variables.put("description", config.description());
    }

}
