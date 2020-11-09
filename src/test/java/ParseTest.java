import com.freetmp.mbg.merge.CompilationUnitMerger;
import com.github.javaparser.ParseException;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

public class ParseTest {
    private static final Logger logger = LoggerFactory.getLogger(ParseTest.class);

    @Test
    public void parseObject() throws IOException, ParseException {
        // /Users/ca62785/Codes/xstudio-java-merge/src/test/java/com/xstudio/test/A.java
        URL resource = ParseTest.class.getResource("");
        String path = resource.getPath();
        String sourcePathParent = path.substring(0, path.indexOf("/build"));
        File afile = new File("/Users/ca62785/Codes/xstudio-java-merge/src/test/java/com/xstudio/test/object/first/FirstObjectWithAnnotation.java");
        File bfile = new File("/Users/ca62785/Codes/xstudio-java-merge/src/test/java/com/xstudio/test/object/second/SecondObject.java");
        String merge = CompilationUnitMerger.merge(
                FileUtils.readFileToString(afile, Charset.defaultCharset()),
                FileUtils.readFileToString(bfile, Charset.defaultCharset())
        );
        logger.info("merge result: \n{}", merge);
    }
}
