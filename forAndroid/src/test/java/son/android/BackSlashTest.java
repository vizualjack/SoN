package son.android;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class BackSlashTest {
    @Test
    public void test() {
        String path = "asdasd\\asdasd\\asdasd\\dasdasd";
        String[] paths = path.split("\\\\");
        System.out.println(paths.length);
        System.out.println(paths[0]);
    }
}