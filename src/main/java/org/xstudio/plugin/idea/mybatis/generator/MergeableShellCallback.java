package org.xstudio.plugin.idea.mybatis.generator;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.intellij.openapi.diagnostic.Logger;
import io.github.xbeeant.javamerge.merger.JavaMerger;
import org.apache.commons.io.FileUtils;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * @author xiaobiao
 */
public class MergeableShellCallback extends DefaultShellCallback {

    private static final Logger log = Logger.getInstance(MergeableShellCallback.class);
    private static final JavaParser JAVA_PARSER = new JavaParser();

    public MergeableShellCallback(boolean overwrite) {
        super(overwrite);
    }

    @Override
    public boolean isMergeSupported() {
        return true;
    }

    @Override
    public String mergeJavaFile(String newFileSource, File existingFile, String[] javadocTags, String fileEncoding) throws ShellException {
        CompilationUnit newCu = null;
        CompilationUnit oldCu = null;
        try {
            Optional<CompilationUnit> newCuResult = JAVA_PARSER.parse(newFileSource).getResult();
            if (newCuResult.isPresent()) {
                newCu = newCuResult.get();
            }

            Optional<CompilationUnit> oldCuResult = JAVA_PARSER.parse(existingFile).getResult();
            if (oldCuResult.isPresent()) {
                oldCu = oldCuResult.get();
            }
            if (null == newCu) {
                return oldCuResult.toString();
            }
            if (null == oldCu) {
                return newFileSource;
            }
            return JavaMerger.merge(oldCu, newCu, true).toString();
        } catch (Exception e) {
            log.error(e);
            try {
                return FileUtils.readFileToString(existingFile, fileEncoding);
            } catch (IOException ioException) {
                log.error(e);
                return newFileSource;
            }
        }
    }
}
