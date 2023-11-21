package be.zsoft.jsitegen.holder;

import be.zsoft.jsitegen.dto.Config;
import lombok.Getter;
import lombok.Setter;

public final class ConfigHolder {

    @Getter
    @Setter
    private static Config config;

    private ConfigHolder() {}

}
