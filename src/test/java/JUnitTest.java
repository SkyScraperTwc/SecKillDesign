import com.alibaba.druid.sql.visitor.functions.Char;
import org.junit.Test;

//@RunWith(SpringRunner.class)
//@SpringBootTest()
public class JUnitTest {

    @Test
    public void test2() throws InterruptedException {
        String message = "HELLO world!";
        byte[] b = message.getBytes();
        for (int i = 0; i <b.length ; i++) {
            System.out.println((char)b[i]+"----"+b[i]);
        }
    }

}
