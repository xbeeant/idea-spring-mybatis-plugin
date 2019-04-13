package org.mybatis.generator.myplugin;

import com.freetmp.mbg.merge.CompilationUnitMerger;
import com.intellij.openapi.project.Project;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.*;

/**
 * @author xiaobiao
 * @version 2019/3/20
 */
public class MyShellCallback extends DefaultShellCallback {
    private Project project;

    public MyShellCallback(boolean overwrite) {
        super(overwrite);
    }

    public MyShellCallback(boolean overwrite, Project project) {
        super(overwrite);
        this.project = project;
    }

    @Override
    public File getDirectory(String targetProject, String targetPackage) throws ShellException {
        return super.getDirectory(targetProject, targetPackage);
    }

    @Override
    public void refreshProject(String project) {
        super.refreshProject(project);
    }

    @Override
    public boolean isMergeSupported() {
        return true;
    }

    @Override
    public boolean isOverwriteEnabled() {
        return super.isOverwriteEnabled();
    }

    @Override
    public String mergeJavaFile(String newFileSource, File existingFile, String[] javadocTags, String fileEncoding) throws ShellException {
        String mergedFileSource;
        try {
            mergedFileSource = CompilationUnitMerger.merge(newFileSource, existingFile, project);
        } catch (Exception e) {
            throw new ShellException(e);
        }

        return mergedFileSource;
    }
}
