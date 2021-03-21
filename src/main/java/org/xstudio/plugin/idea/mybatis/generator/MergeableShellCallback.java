package org.xstudio.plugin.idea.mybatis.generator;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.intellij.notification.*;
import com.intellij.openapi.diagnostic.Logger;
import io.github.xbeeant.javamerge.merger.JavaMerger;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.xstudio.plugin.idea.Constant;

import java.io.File;

/**
 * @author LIQIU
 */
public class MergeableShellCallback extends DefaultShellCallback {

    private static final Logger log = Logger.getInstance(MergeableShellCallback.class);
    private static final JavaParser javaParser = new JavaParser();

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
            CompilationUnit newCu = javaParser.parse(newFileSource).getResult().get();
            log.debug("new:\n {}" , newCu.toString());
            CompilationUnit oldCu = javaParser.parse(existingFile).getResult().get();
            log.debug("old:\n {}", oldCu.toString());
            return JavaMerger.merge(oldCu, newCu, true).toString();
        } catch (Exception e) {
            log.error(e);
            return FileUtils.readFileToString(existingFile, fileEncoding)
        }
    }
}
