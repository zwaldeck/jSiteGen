package be.zsoft.jsitegen.dto;

import java.util.List;

public record ScssConfig(boolean scss,
                         List<String> files) {

}
