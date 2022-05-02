package com.littlepenguin.uscshortcutsysserver.VO;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component("Python")
@NoArgsConstructor
@Data
@ConfigurationProperties(prefix = "py")
public class Python {
    public String pyLocation;
    public String scriptName;
}
