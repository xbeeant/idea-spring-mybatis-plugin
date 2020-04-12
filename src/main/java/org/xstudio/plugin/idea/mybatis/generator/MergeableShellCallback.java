package org.xstudio.plugin.idea.mybatis.generator;

import com.freetmp.mbg.merge.CompilationUnitMerger;
import org.apache.commons.io.FileUtils;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.nio.charset.Charset;

/**
 * @author LIQIU
 */
public class MergeableShellCallback extends DefaultShellCallback {

    public MergeableShellCallback(boolean overwrite) {
        super(overwrite);
    }

    @Override
    public boolean isMergeSupported() {
        return true;
    }

    @Override
    public String mergeJavaFile(String newFileSource, File existingFile, String[] javadocTags, String fileEncoding) throws ShellException {
        try {
            return CompilationUnitMerger.merge(newFileSource, FileUtils.readFileToString(existingFile, Charset.defaultCharset()));
        } catch (Exception e) {
            throw new ShellException(e);
        }
    }
}
