package com.littlepenguin.uscshortcutsysserver.VO;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component("Selenium")
@NoArgsConstructor
@Data
@ConfigurationProperties(prefix = "selenium")
public class Selenium {
    public Boolean isHeadless;
    public BrowserDriver chromeDriver;
    public CardWeb cardWeb;
    public long waitServiceMillis;
    public long waitExceptionMillis;
}
