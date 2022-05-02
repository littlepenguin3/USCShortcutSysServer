package com.littlepenguin.uscshortcutsysserver.VO;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Data
public class BrowserDriver {
    public String name;
    public String path;
}
