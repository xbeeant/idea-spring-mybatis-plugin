package org.xstudio.plugin.idea.action;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import org.xstudio.plugin.idea.Constant;
import org.xstudio.plugin.idea.model.TableInfo;
import org.xstudio.plugin.idea.ui.CodeGeneratorUI;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaobiao
 * @version 2019/9/20
 */
public class MainAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiElement[] psiElements = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);

        if (psiElements == null || psiElements.length == 0) {
            Messages.showMessageDialog("Please select one table or more then one columns", Constant.TITLE, Messages.getWarningIcon());
            return;
        }

        List<String> errorPsiElement = new ArrayList<>();
        errorPsiElement.add("FamilyGroup");
        errorPsiElement.add("DbNamespaceImpl");
        errorPsiElement.add("DbDataSourceImpl");
        for (PsiElement psiElement : psiElements) {
            if (errorPsiElement.contains(psiElement.getClass().getSimpleName())) {
                Messages.showMessageDialog("Please select a table", Constant.TITLE, Messages.getWarningIcon());
                return;
            }
        }

        PsiElement current = psiElements[0];
        // 当前选择的表的信息
        TableInfo tableInfo = new TableInfo((DbTable) current);

        CodeGeneratorUI dialog = new CodeGeneratorUI(e, e.getProject(), tableInfo);
        dialog.pack();
        dialog.show();
    }
}
