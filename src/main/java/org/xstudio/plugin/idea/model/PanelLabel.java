package org.xstudio.plugin.idea.model;

import javax.swing.*;

/**
 * @author xiaobiao
 * @version 2019/9/24
 */
public class PanelLabel {
    private JPanel panel;

    private JLabel label;

    private JTextField field;

    public JPanel getPanel() {
        return panel;
    }

    public void setPanel(JPanel panel) {
        this.panel = panel;
    }

    public JLabel getLabel() {
        return label;
    }

    public void setLabel(JLabel label) {
        this.label = label;
    }

    public JTextField getField() {
        return field;
    }

    public void setField(JTextField field) {
        this.field = field;
    }
}
