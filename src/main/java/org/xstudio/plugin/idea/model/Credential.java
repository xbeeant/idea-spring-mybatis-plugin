package org.xstudio.plugin.idea.model;

/**
 * 保存数据库连接对应的用户名，密码存在keepass库中
 *
 * @author ca62785
 * @date 2020/11/07
 */
public class Credential {

    /**
     * 用户名
     */
    private String username;

    public Credential() {
    }

    public Credential(String username) {
        this.username = username;

    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
