package org.xstudio.plugins.idea.ui;

/**
 * @author xiaobiao
 * @version 2019/3/19
 */
public class InputItem {
    private String text;

    private String field;

    public InputItem(String text, String field) {
        this.text = text;
        this.field = field;
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
}
