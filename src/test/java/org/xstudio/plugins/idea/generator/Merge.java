package org.xstudio.plugins.idea.generator;

import com.freetmp.mbg.merge.CompilationUnitMerger;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;

/**
 * @author xiaobiao
 * @version 2019/3/20
 */
public class Merge {
    @Test
    public void testMerge() throws FileNotFoundException {
        // creates an input stream for the file to be parsed
        String file1 = "D:\\MyDocuments\\Documents\\github\\idea spring plugin\\src\\test\\java\\org\\xstudio\\plugins\\idea\\generator\\ForTest.java";
        String file2 = "D:\\MyDocuments\\Documents\\github\\idea spring plugin\\src\\test\\java\\org\\xstudio\\plugins\\idea\\test\\ForTest.java";
        try {
            String mergedFileSource =  CompilationUnitMerger.merge(file1, new File(file2), null);
            File  file = new File("D:\\MyDocuments\\Documents\\github\\idea spring plugin\\src\\test\\java\\org\\xstudio\\plugins\\idea\\generator\\MergeResult.java");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(mergedFileSource);
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
