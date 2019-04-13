package org.xstudio.plugins.idea.generator;

import com.alibaba.fastjson.JSONObject;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.myplugin.MyShellCallback;
import org.mybatis.generator.config.*;
import org.mybatis.generator.exception.InvalidConfigurationException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaobiao
 * @version 2019/3/11
 */
public class CodeGenerator {
    public static void generate(String projectPath, JSONObject generatorConfig, Project project) throws InvalidConfigurationException, InterruptedException, SQLException, IOException {
        List<String> warnings = new ArrayList<>();
        boolean overwrite = true;
        Configuration config = new Configuration();
        Context context = new Context(ModelType.CONDITIONAL);
        context.setId("MyContext");
        config.addContext(context);
        context.addProperty("autoDelimitKeywords", "true");
        context.addProperty("beginningDelimiter", "`");
        context.addProperty("endingDelimiter", "`");
        context.addProperty("javaFileEncoding", "UTF-8");
        context.addProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING, "UTF-8");

        String table = generatorConfig.getString("table");

        String url = "jdbc:mysql://" + generatorConfig.getString("server")
                + ":" + generatorConfig.getString("port") + "/"
                + table + "?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=UTC&allowMultiQueries=true&autoReconnect=true";
        String dbType = generatorConfig.getString("type");
        String pakage = generatorConfig.getString("pakage");

        if (null == dbType) {
            dbType = "mysql";
        }
        String srcPath = projectPath + "/src/main/java";
        String mapperPath = projectPath + "/src/main/resources/mybatis/" + dbType + "/";
        File file = new File(mapperPath);
        file.mkdirs();

        PluginConfiguration pluginConfiguration;
        String packageName = table.replaceAll("_", ".");


        // ================================= 自定义插件 配置 =============================================================
        if("是".equals(generatorConfig.getString(EnConfigField.TABLE_PREFIX.getCode()))){
            // 表前缀
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("org.mybatis.generator.myplugin.TablePrefixPlugin");
            pluginConfiguration.addProperty("prefix", generatorConfig.getString("prefix"));
            context.addPluginConfiguration(pluginConfiguration);
        }


        // swagger2 注解插件
        if("是".equals(generatorConfig.getString(EnConfigField.SWAGGER.getCode()))) {
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("org.mybatis.generator.myplugin.ModelSwaggerPropertyAnnotationPlugin");
            context.addPluginConfiguration(pluginConfiguration);
        }

        // 标记移除插件配置
        if("是".equals(generatorConfig.getString(EnConfigField.ALIAS_DELETE.getCode()))) {
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("org.mybatis.generator.myplugin.ModelMarkDeleteFieldPlugin");
            context.addPluginConfiguration(pluginConfiguration);
        }

        // 父对象插件配置
        if("是".equals(generatorConfig.getString(EnConfigField.MODEL_ROOT.getCode()))){
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("org.mybatis.generator.myplugin.ModelRootObjectPlugin");
            pluginConfiguration.addProperty("rootObject", generatorConfig.getString("modelRootObject"));
            pluginConfiguration.addProperty("generateGetSetKey", "true");
            pluginConfiguration.addProperty("excludeFields", generatorConfig.getString("ignoreColumns"));
            context.addPluginConfiguration(pluginConfiguration);

            // 给日期类型添加 ***Begin ***End 属性
            // ModelBeginEndFieldPlugin
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("org.mybatis.generator.myplugin.ModelBeginEndFieldPlugin");
            context.addPluginConfiguration(pluginConfiguration);

            // dao 继承父类
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("org.mybatis.generator.myplugin.ClientRootPlugin");
            pluginConfiguration.addProperty("rootClient", generatorConfig.getString("clientRootObject"));
            pluginConfiguration.addProperty("excludeMethods", "countByExample" +
                    ",deleteByExample,deleteByPrimaryKey," +
                    ",insert" +
                    ",insertSelective" +
                    ",selectByExample" +
                    ",selectByPrimaryKey" +
                    ",selectByExampleWithBLOBs" +
                    ",updateByExampleSelective" +
                    ",updateByExample" +
                    ",updateByPrimaryKeySelective" +
                    ",updateByExampleWithBLOBs" +
                    ",updateByPrimaryKeyWithBLOBs" +
                    ",updateByPrimaryKey");
            pluginConfiguration.addProperty("excludeMapper", "deleteByExample" +
                    ",insert" +
                    ",updateByExample" +
                    ",updateByPrimaryKey" +
                    ",updateByPrimaryKeyWithBLOBs" +
                    ",updateByExampleWithBLOBs"
            );
            context.addPluginConfiguration(pluginConfiguration);


            // mapper 模糊搜索插件
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("org.mybatis.generator.myplugin.MapperFuzzySearchPlugin");
            pluginConfiguration.addProperty("nonFuzzyColumn", generatorConfig.getString("nonFuzzyColumns"));
            context.addPluginConfiguration(pluginConfiguration);

            // service 代码服务
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("org.mybatis.generator.myplugin.ServicePlugin");
            pluginConfiguration.addProperty("idGenerator", generatorConfig.getString("idGenerator"));
            pluginConfiguration.addProperty("rootClient", generatorConfig.getString("clientRootObject"));
            pluginConfiguration.addProperty("rootService", generatorConfig.getString("serviceRootObject"));
            pluginConfiguration.addProperty("rootServiceImpl", generatorConfig.getString("serviceImplRootObject"));
            pluginConfiguration.addProperty("package", generatorConfig.getString("pakage") + "." + packageName);
            pluginConfiguration.addProperty("generateGetSetKeyValue", "false");
            context.addPluginConfiguration(pluginConfiguration);
        }

        // json 序列化注解插件配置
        if("是".equals(generatorConfig.getString(EnConfigField.FIELD_JSON.getCode()))) {
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("org.mybatis.generator.myplugin.ModelFieldJsonSerializePlugin");
            context.addPluginConfiguration(pluginConfiguration);
        }

        // facade service 代码服务
        if ("是".equalsIgnoreCase(generatorConfig.getString(EnConfigField.GEN_FACADE.getCode()))) {
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("org.mybatis.generator.myplugin.FacadePlugin");
            pluginConfiguration.addProperty("rootClient", generatorConfig.getString("clientRootObject"));
            pluginConfiguration.addProperty("rootService", generatorConfig.getString("serviceRootObject"));
            pluginConfiguration.addProperty("rootFacadeService", generatorConfig.getString("facadeServiceRootObject"));
            pluginConfiguration.addProperty("rootFacadeServiceImpl", generatorConfig.getString("facadeServiceImplRootObject"));
            pluginConfiguration.addProperty("package", generatorConfig.getString("pakage") + "." + packageName);
            context.addPluginConfiguration(pluginConfiguration);
        }

        // antd 插件
        if ("是".equalsIgnoreCase(generatorConfig.getString(EnConfigField.GEN_WEB.getCode()))) {
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("org.mybatis.generator.myplugin.AntDesignPlugin");
            pluginConfiguration.addProperty("modelName", generatorConfig.getString("modelName"));
            pluginConfiguration.addProperty("componentName", generatorConfig.getString("componentName"));
            context.addPluginConfiguration(pluginConfiguration);
        }

        // ModelLomboxPlugin
        if ("是".equalsIgnoreCase(generatorConfig.getString(EnConfigField.LOMBOK.getCode()))) {
            pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("org.mybatis.generator.myplugin.ModelLomboxPlugin");
            context.addPluginConfiguration(pluginConfiguration);
        }

        // ================================= 必须 配置 ===================================================================

        // ===============================
        // 数据库设置
        JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
        jdbcConnectionConfiguration.setConnectionURL(url);
        jdbcConnectionConfiguration.setDriverClass(generatorConfig.getString("driverClass"));
        jdbcConnectionConfiguration.setPassword(generatorConfig.getString("password"));
        jdbcConnectionConfiguration.setUserId(generatorConfig.getString("username"));
        context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);

        // ===============================
        // 生成表对象设置
        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
        javaModelGeneratorConfiguration.setTargetPackage(pakage + "." + packageName + ".model");
        javaModelGeneratorConfiguration.setTargetProject(srcPath);
        javaModelGeneratorConfiguration.addProperty("trimStrings", "true");
        javaModelGeneratorConfiguration.addProperty("enableSubPackages", "false");
        context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

        // ===============================
        // 生成 dao 接口 配置
        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
        javaClientGeneratorConfiguration.setTargetPackage(pakage + "." + packageName + ".mapper");
        javaClientGeneratorConfiguration.setTargetProject(srcPath + "/");
        javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
        javaClientGeneratorConfiguration.addProperty("enableSubPackages", "false");

        context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);
        String tableName = generatorConfig.getString("tableName");
        if (StringUtils.isEmpty(tableName)) {
            tableName = "%";
        }
        // ===============================
        // 生成表设置
        TableConfiguration tableConfiguration = new TableConfiguration(context);
        tableConfiguration.setTableName(tableName);
        tableConfiguration.setSchema(table);
        tableConfiguration.addProperty("virtualKeyColumns", "VirtualID");
        context.addTableConfiguration(tableConfiguration);

        // ===============================
        // 生成 mapper 文件配置
        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
        sqlMapGeneratorConfiguration.setTargetPackage(table);
        sqlMapGeneratorConfiguration.setTargetProject(mapperPath);
        sqlMapGeneratorConfiguration.addProperty("enableSubPackages", "false");
        context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);

        // ===============================
        // 序列化插件配置
        // SerializablePlugin
        pluginConfiguration = new PluginConfiguration();
        pluginConfiguration.setConfigurationType("org.mybatis.generator.plugins.SerializablePlugin");
        pluginConfiguration.addProperty("suppressJavaInterface", "false");
        pluginConfiguration.addProperty("addGWTInterface", "false");
        context.addPluginConfiguration(pluginConfiguration);

        // ===============================
        // 生成注释配置
        CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
        commentGeneratorConfiguration.addProperty("suppressAllComments", "false");
        commentGeneratorConfiguration.addProperty("addRemarkComments", "true");
        commentGeneratorConfiguration.addProperty("suppressDate", "true");
        context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);

        //   ... fill out the config object as appropriate...
        MyShellCallback callback = new MyShellCallback(overwrite, project);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
    }
}
