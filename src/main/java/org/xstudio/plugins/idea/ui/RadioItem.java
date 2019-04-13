package org.xstudio.plugins.idea.ui;

import java.util.List;

/**
 * @author xiaobiao
 * @version 2019/3/22
 */
public class RadioItem {
    private String text;

    private String field;

    private List<String> options;

    private String defaultValue;

    public RadioItem(String text, String field, List<String> options, String defaultValue) {
        this.text = text;
        this.field = field;
        this.options = options;
        this.defaultValue = defaultValue;
    }

    /**
     * Getter for property 'text'.
     *
     * @return Value for property 'text'.
     */
    public String getText() {
        return text;
    }

    /**
     * Setter for property 'text'.
     *
     * @param text Value to set for property 'text'.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Getter for property 'field'.
     *
     * @return Value for property 'field'.
     */
    public String getField() {
        return field;
    }

    /**
     * Setter for property 'field'.
     *
     * @param field Value to set for property 'field'.
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * Getter for property 'options'.
     *
     * @return Value for property 'options'.
     */
    public List<String> getOptions() {
        return options;
    }

    /**
     * Setter for property 'options'.
     *
     * @param options Value to set for property 'options'.
     */
    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
