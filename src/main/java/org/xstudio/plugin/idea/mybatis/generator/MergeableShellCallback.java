package org.xstudio.plugin.idea.mybatis.generator;

import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.xstudio.plugin.idea.mybatis.util.JavaFileMerger;

import java.io.File;
import java.io.FileNotFoundException;

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
            return new JavaFileMerger().getNewJavaFile(newFileSource, existingFile.getAbsolutePath());
        } catch (FileNotFoundException e) {
            throw new ShellException(e);
        }
    }
}
