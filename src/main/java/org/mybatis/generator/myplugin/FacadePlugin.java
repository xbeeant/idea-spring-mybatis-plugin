package org.mybatis.generator.myplugin;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.PropertyRegistry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author  xiaobiao
 * @date 2017/2/22.
 */
public class FacadePlugin extends PluginAdapter {
    private String pacage;
    private FullyQualifiedJavaType baseRecordType;
    private FullyQualifiedJavaType rootService;
    private FullyQualifiedJavaType paramedRootService;

    private FullyQualifiedJavaType rootFacadeService;
    private FullyQualifiedJavaType paramedRootFacadeService;
    private FullyQualifiedJavaType rootFacadeServiceImpl;
    private FullyQualifiedJavaType paramedRootFacadeServiceImpl;
    private FullyQualifiedJavaType autowiredAnnotationFqjt = new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired");

    private String baseRecordTypeShortName;

    @Override
    public boolean validate(List<String> warnings) {
        pacage = properties.getProperty("package");
        rootService = new FullyQualifiedJavaType(properties.getProperty("rootService"));
        rootFacadeService = new FullyQualifiedJavaType(properties.getProperty("rootFacadeService"));
        rootFacadeServiceImpl = new FullyQualifiedJavaType(properties.getProperty("rootFacadeServiceImpl"));
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        baseRecordTypeShortName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        paramedRootService = new FullyQualifiedJavaType(properties.getProperty("rootService"));
        baseRecordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        paramedRootService.addTypeArgument(baseRecordType);

        paramedRootFacadeService = new FullyQualifiedJavaType(properties.getProperty("rootFacadeService"));
        paramedRootFacadeService.addTypeArgument(baseRecordType);

        paramedRootFacadeServiceImpl = new FullyQualifiedJavaType(properties.getProperty("rootFacadeServiceImpl"));
        paramedRootFacadeServiceImpl.addTypeArgument(baseRecordType);
        super.initialized(introspectedTable);
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles() {
        return super.contextGenerateAdditionalJavaFiles();
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        List<GeneratedJavaFile> files = new ArrayList<>();
        /*
         * service 接口实现
         */
        String serviceInterfaceString = pacage + ".I" + baseRecordTypeShortName + "Service";
        FullyQualifiedJavaType serviceInterface = new FullyQualifiedJavaType(serviceInterfaceString);

        String facadeServiceInterfaceString = pacage + ".facade.I" + baseRecordTypeShortName + "FacadeService";
        FullyQualifiedJavaType facadeServiceInterface = new FullyQualifiedJavaType(facadeServiceInterfaceString);
        Interface interfaze = new Interface(facadeServiceInterface);
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        interfaze.addJavaDocLine("/**");
        interfaze.addJavaDocLine(" * facade service for table " + introspectedTable.getFullyQualifiedTable().getIntrospectedTableName());
        interfaze.addJavaDocLine(" * ");
        interfaze.addJavaDocLine(" * @author mybatis generator");
        interfaze.addJavaDocLine(" * @version " + new Date());
        interfaze.addJavaDocLine(" */");

        // 设置父类
        interfaze.addSuperInterface(paramedRootFacadeService);
        interfaze.addImportedType(rootFacadeService);
        interfaze.addImportedType(baseRecordType);


        GeneratedJavaFile javaFile = new GeneratedJavaFile(interfaze, context.getJavaModelGeneratorConfiguration()
                .getTargetProject(),
                context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                context.getJavaFormatter());
        files.add(javaFile);


        /*
         * service 继承实现
         */
        String serviceImplString = pacage + ".facade.impl." + baseRecordTypeShortName + "FacadeServiceImpl";
        FullyQualifiedJavaType serviceImpl = new FullyQualifiedJavaType(serviceImplString);
        TopLevelClass topLevelClass = new TopLevelClass(serviceImpl);
        topLevelClass.setSuperClass(paramedRootFacadeService);
        topLevelClass.addImportedType(autowiredAnnotationFqjt);
        topLevelClass.addAnnotation("@Service(version = \"1.0.0\")");
        topLevelClass.addImportedType(new FullyQualifiedJavaType("com.alibaba.dubbo.config.annotation.Service"));

        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" * facade service implements for table " + introspectedTable.getFullyQualifiedTable().getIntrospectedTableName());
        topLevelClass.addJavaDocLine(" * ");
        topLevelClass.addJavaDocLine(" * @author mybatis generator");
        topLevelClass.addJavaDocLine(" * @version " + new Date());
        topLevelClass.addJavaDocLine(" */");

        // 设置父类
        topLevelClass.addSuperInterface(facadeServiceInterface);
        topLevelClass.addImportedType(facadeServiceInterface);
        topLevelClass.addImportedType(rootFacadeService);
        topLevelClass.addImportedType(baseRecordType);


        // service repositoryAnnotation

        String serviceInterfaceAlias = serviceInterface.getShortName().substring(1, 2).toLowerCase() + serviceInterface.getShortName().substring(2);
        Field service = new Field(serviceInterfaceAlias, serviceInterface);
        service.addAnnotation("@Autowired");
        service.setVisibility(JavaVisibility.PRIVATE);
        topLevelClass.addField(service);
        topLevelClass.addImportedType(autowiredAnnotationFqjt);

        // getService Method
        Method getService = new Method("getService");
        getService.addAnnotation("@Override");
        getService.setVisibility(JavaVisibility.PUBLIC);
        getService.setReturnType(paramedRootService);
        topLevelClass.addImportedType(serviceInterface);

        getService.addBodyLine("return this.".concat(serviceInterfaceAlias).concat(";"));
        topLevelClass.addMethod(getService);
        topLevelClass.addImportedType(rootService);
        topLevelClass.addImportedType(paramedRootService);

        javaFile = new GeneratedJavaFile(topLevelClass, context.getJavaModelGeneratorConfiguration()
                .getTargetProject(),
                context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                context.getJavaFormatter());
        files.add(javaFile);
        return files;
    }
}
