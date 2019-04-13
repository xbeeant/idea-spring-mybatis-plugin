package org.xstudio.plugins.idea.ui;

import com.alibaba.fastjson.JSONObject;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author xiaobiao
 * @version 2019/3/22
 */
public class UiUtil {
    private UiUtil() {
    }

    /**
     * 添加 输入框
     *
     * @param panel         面板
     * @param labels        标签集
     * @param width         输入框宽度
     * @param initialValues 初始值
     * @return map
     */
    public static Map<String, JTextField> addLabelInput(JPanel panel, List<InputItem> labels, Integer width, JSONObject initialValues) {
        Label label;
        JTextField input;
        Map<String, JTextField> inputs = new HashMap<>();
        for (InputItem item : labels) {
            label = new Label(item.getText());
            label.setPreferredSize(new Dimension(100, 24));
            label.setAlignment(Label.RIGHT);
            panel.add(label);

            input = new JTextField();
            input.setPreferredSize(new Dimension(width, 24));
            input.setHorizontalAlignment(JTextField.LEFT);
            input.setText(initialValues.getString(item.getField()));
            panel.add(input);
            inputs.put(item.getField(), input);
        }
        return inputs;
    }

    public static Map<String, List<JCheckBox>> addCheckBox(JPanel panel, List<CheckBoxItem> checkBoxItems, Integer width, JSONObject initialValues) {
        Label label;
        JCheckBox checkBox;
        Map<String, List<JCheckBox>> checkBoxs = new HashMap<>();
        List<JCheckBox> items;
        for (CheckBoxItem item : checkBoxItems) {
            label = new Label(item.getText());
            label.setPreferredSize(new Dimension(100, 24));
            label.setAlignment(Label.RIGHT);
            panel.add(label);

            List<String> options = item.getOptions();
            items = new ArrayList<>();
            for (String option : options) {
                checkBox = new JCheckBox(option);
                checkBox.setPreferredSize(new Dimension(width, 24));
                checkBox.setHorizontalAlignment(JTextField.LEFT);
                panel.add(checkBox);
                items.add(checkBox);
            }
            checkBoxs.put(item.getField(), items);
        }

        return checkBoxs;
    }

    public static Map<String, List<JRadioButton>> addRadioItems(JPanel panel, List<RadioItem> radioItems, Integer width) {
        Label label;
        JRadioButton radioButton;
        ButtonGroup buttonGroup;
        JPanel buttonPanel;
        Map<String, List<JRadioButton>> radios = new HashMap<>();
        for (RadioItem item : radioItems) {
            buttonGroup = new ButtonGroup();
            buttonPanel = new JBPanel<>(new FlowLayout(FlowLayout.LEFT));
            label = new Label(item.getText());
            label.setPreferredSize(new Dimension(150, 24));
            label.setAlignment(Label.RIGHT);
            buttonPanel.add(label);
            List<JRadioButton> items = new ArrayList<>();
            List<String> options = item.getOptions();
            for (String option : options) {
                radioButton = new JRadioButton(option);
                radioButton.setPreferredSize(new Dimension(width, 24));
                radioButton.setHorizontalAlignment(JTextField.LEFT);
                if (item.getDefaultValue().equals(option)) {
                    radioButton.setSelected(true);
                }
                buttonGroup.add(radioButton);
                buttonPanel.add(radioButton);
                items.add(radioButton);
            }
            radios.put(item.getField(), items);
            panel.add(buttonPanel);
        }
        return radios;
    }
}
