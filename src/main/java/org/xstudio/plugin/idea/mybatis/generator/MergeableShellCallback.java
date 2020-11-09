package org.xstudio.plugin.idea.mybatis.generator;

import com.github.javaparser.ast.CompilationUnit;
import com.intellij.notification.*;
import com.xstudio.javamerge.merger.JavaMerger;
import org.apache.commons.io.FileUtils;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.xstudio.plugin.idea.Constant;

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
            CompilationUnit newCu = new CompilationUnit(newFileSource);
            CompilationUnit oldCu = new CompilationUnit(FileUtils.readFileToString(existingFile, Charset.defaultCharset()));
            return JavaMerger.merge(oldCu, newCu, true).toString();
        } catch (Exception e) {
            e.printStackTrace();
            NotificationGroup balloonNotifications = new NotificationGroup(Constant.TITLE, NotificationDisplayType.STICKY_BALLOON, true);
            Notification notification = balloonNotifications.createNotification("Generated Error", e.getMessage(), NotificationType.ERROR, (notification1, hyperlinkEvent) -> {
            });
            Notifications.Bus.notify(notification);

            throw new ShellException(e);
        }
    }
}
