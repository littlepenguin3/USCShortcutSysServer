package com.littlepenguin.uscshortcutsysserver.VO;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Data
public class Xpaths {
    public String teacherNumber;
    public String teacherPassword;
    public String checkCodeImg;
    public String checkCodeInput;
    public String loginButton;
    public String validCheckElement;
}
