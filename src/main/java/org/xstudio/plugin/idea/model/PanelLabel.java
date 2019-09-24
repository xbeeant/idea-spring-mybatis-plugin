package org.xstudio.plugin.idea.model;

import lombok.Data;

import javax.swing.*;

/**
 * @author xiaobiao
 * @version 2019/9/24
 */
@Data
public class PanelLabel {
    private JPanel panel;

    private JLabel label;

    private JTextField field;
}
