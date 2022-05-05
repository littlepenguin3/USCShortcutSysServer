
import com.littlepenguin.uscshortcutsysserver.utils.DESUtils;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.Queue;


public class TestEncrypDES3 {
    @Test
    void test1() throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, IOException {
        byte[] encrytor = DESUtils.Encrytor("004775");
        FileUtils.writeByteArrayToFile(new File("C:\\Users\\38122\\Desktop\\1.txt"),encrytor);
        System.out.println(new String(DESUtils.Decryptor(encrytor)));
    }
    @Test
    void test2() throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        byte[] bs = FileUtils.readFileToByteArray(new File("C:\\Users\\38122\\Desktop\\1.txt"));
        System.out.println(new String(DESUtils.Decryptor(bs)));
    }
    @Test
    void test3(){
        String s=new String("test");
        Queue<String> q= new LinkedList<String>();
        q.add(s);
        q.add(s);
        System.out.println(q.peek());
        q.remove();
        System.out.println(q.peek());
    }
    private void testFinally(){
        try{
            return;
        }finally {
            System.out.println(123);
        }
    }
    @Test
    void test4(){
        testFinally();
    }
}