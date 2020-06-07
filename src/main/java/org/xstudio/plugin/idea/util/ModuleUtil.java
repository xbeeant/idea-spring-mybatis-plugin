package org.xstudio.plugin.idea.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;

/**
 * @author Beeant
 * @version 2020/6/7
 */
public class ModuleUtil {
    public static String getPath(Module module, String type) {
        VirtualFile virtualFile = getModule(module, "root");
        String path = virtualFile.getPath();
        switch (type) {
            case "java":
                path += File.separator + "java";
                break;
            case "resource":
            default:
                path += "";
        }
        return path;
    }

    public static VirtualFile getModule(Module module, String type) {
        VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
        VirtualFile virtualFile = contentRoots[0];
        for (VirtualFile contentRoot : contentRoots) {
            if (contentRoot.getPath().contains("build")) {
                continue;
            }
            virtualFile = contentRoot;
        }
        switch (type) {
            case "root":
                return getRoot(virtualFile);
        }
        return virtualFile;
    }

    private static VirtualFile getRoot(VirtualFile virtualFile) {
        if (virtualFile.getPath().contains("src")) {
            return getRoot(virtualFile.getParent());
        }
        return virtualFile;
    }
}
