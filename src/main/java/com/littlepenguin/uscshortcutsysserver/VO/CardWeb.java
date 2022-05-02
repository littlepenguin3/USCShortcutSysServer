package com.littlepenguin.uscshortcutsysserver.VO;

import com.littlepenguin.uscshortcutsysserver.utils.DESUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;

@Component
@NoArgsConstructor
@Data
public class CardWeb {
    public String url;
    public String username;
    public String password;
    public String splitRegForSK;
    public Xpaths xpaths;

    public void setUsername(@NotNull String base64Username) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        username = new String(DESUtils.Decryptor(base64Username.getBytes()));
    }
    public void setPassword(@NotNull String base64Password) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        password = new String(DESUtils.Decryptor(base64Password.getBytes()));
    }
}
