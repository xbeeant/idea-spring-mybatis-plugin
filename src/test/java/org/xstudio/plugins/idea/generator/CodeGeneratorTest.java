package org.xstudio.plugins.idea.generator;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.mybatis.generator.exception.InvalidConfigurationException;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @author xiaobiao
 * @version 2019/3/19
 */
public class CodeGeneratorTest {
    @Test
    public void renderTestMethodName() throws InterruptedException, SQLException, InvalidConfigurationException, IOException {
        JSONObject config = FileUtil.getConfig("/Users/xiaobiao/git/idea-spring-mybatis-plugin/");
        CodeGenerator.generate("/Users/xiaobiao/git/idea-spring-mybatis-plugin", config, null);
    }
}