package org.xstudio.plugin.idea.action;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import org.xstudio.plugin.idea.ui.GeneratorUi;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaobiao
 * @version 2019/9/20
 */
public class MainAction extends AnAction {
    private static final String title = "Mybatis Spring Code Generator";

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiElement[] psiElements = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);

        if (psiElements == null || psiElements.length == 0) {
            Messages.showMessageDialog("Please select one table or more then one columns", title, Messages.getWarningIcon());
            return;
        }

        if (psiElements.length > 1) {
            List<String> errorPsiElement = new ArrayList<>();
            errorPsiElement.add("FamilyGroup");
            errorPsiElement.add("DbNamespaceImpl");
            for (PsiElement psiElement : psiElements) {
                if (errorPsiElement.contains(psiElement.getClass().getSimpleName())) {
                    Messages.showMessageDialog("Please select only one table", title, Messages.getWarningIcon());
                    return;
                }
            }
        }

        new GeneratorUi(e);
    }
}
