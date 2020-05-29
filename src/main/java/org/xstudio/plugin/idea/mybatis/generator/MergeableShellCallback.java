package org.xstudio.plugin.idea.mybatis.generator;

import com.freetmp.mbg.merge.CompilationUnitMerger;
import com.intellij.notification.*;
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
            return CompilationUnitMerger.merge(newFileSource, FileUtils.readFileToString(existingFile, Charset.defaultCharset()));
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
