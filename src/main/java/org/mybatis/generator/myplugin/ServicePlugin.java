package org.mybatis.generator.myplugin;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.util.JavaBeansUtil;
import org.mybatis.generator.myplugin.util.PrimaryKeyUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author xiaobiao
 * @date 2017/2/22.
 */
public class ServicePlugin extends PluginAdapter {
    private String pacage;
    private FullyQualifiedJavaType baseRecordType;
    private FullyQualifiedJavaType rootService;
    private FullyQualifiedJavaType rootClient;
    private FullyQualifiedJavaType paramRootClient;
    private FullyQualifiedJavaType idGenerator;
    private String idGeneratorMethod;
    private FullyQualifiedJavaType paramedRootService;
    private FullyQualifiedJavaType rootServiceImpl;
    private FullyQualifiedJavaType paramedRootServiceImpl;
    private FullyQualifiedJavaType autowiredAnnotationFqjt = new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired");
    private Boolean generateSetGetKeyValue = false;
    private String baseRecordTypeShortName;

    @Override
    public boolean validate(List<String> warnings) {
        pacage = properties.getProperty("package");
        rootService = new FullyQualifiedJavaType(properties.getProperty("rootService"));
        String idGeneratorString = properties.getProperty("idGenerator");
        int idx = idGeneratorString.lastIndexOf(".");
        idGeneratorMethod = idGeneratorString.substring(idx);
        idGenerator = new FullyQualifiedJavaType(idGeneratorString.substring(0, idx));
        rootClient = new FullyQualifiedJavaType(properties.getProperty("rootClient"));
        rootServiceImpl = new FullyQualifiedJavaType(properties.getProperty("rootServiceImpl"));
        String generateGetSetKeyValueProperties = properties.getProperty("generateGetSetKeyValue");
        if ("true".equalsIgnoreCase(generateGetSetKeyValueProperties)) {
            generateSetGetKeyValue = true;
        }
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        baseRecordTypeShortName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        paramedRootService = new FullyQualifiedJavaType(properties.getProperty("rootService"));
        baseRecordType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        paramedRootService.addTypeArgument(baseRecordType);


        paramedRootServiceImpl = new FullyQualifiedJavaType(properties.getProperty("rootServiceImpl"));
        paramedRootServiceImpl.addTypeArgument(baseRecordType);

        paramRootClient = new FullyQualifiedJavaType(properties.getProperty("rootClient"));
        paramRootClient.addTypeArgument(baseRecordType);
        FullyQualifiedJavaType primaryKeyTypeFqjt = PrimaryKeyUtil.getFqjt(introspectedTable);
        paramedRootService.addTypeArgument(primaryKeyTypeFqjt);
        paramedRootServiceImpl.addTypeArgument(primaryKeyTypeFqjt);
        paramRootClient.addTypeArgument(primaryKeyTypeFqjt);
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
        String serviceInterfaceString = pacage + ".service.I" + baseRecordTypeShortName + "Service";
        FullyQualifiedJavaType serviceInterface = new FullyQualifiedJavaType(serviceInterfaceString);
        Interface interfaze = new Interface(serviceInterface);
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        interfaze.addJavaDocLine("/**");
        interfaze.addJavaDocLine(" * service for table " + introspectedTable.getFullyQualifiedTable().getIntrospectedTableName());
        interfaze.addJavaDocLine(" * ");
        interfaze.addJavaDocLine(" * @author mybatis generator");
        interfaze.addJavaDocLine(" * @version " + new Date());
        interfaze.addJavaDocLine(" */");

        FullyQualifiedJavaType primaryKeyTypeFqjt = PrimaryKeyUtil.getFqjt(introspectedTable);
        interfaze.addImportedType(primaryKeyTypeFqjt);

        // 设置父类
        interfaze.addSuperInterface(paramedRootService);


        interfaze.addImportedType(rootService);
        interfaze.addImportedType(baseRecordType);


        GeneratedJavaFile javaFile = new GeneratedJavaFile(interfaze, context.getJavaModelGeneratorConfiguration()
                .getTargetProject(),
                context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                context.getJavaFormatter());
        files.add(javaFile);


        /*
         * service 继承实现
         */
        String serviceImplString = pacage + ".service.impl." + baseRecordTypeShortName + "ServiceImpl";
        FullyQualifiedJavaType serviceImpl = new FullyQualifiedJavaType(serviceImplString);
        TopLevelClass topLevelClass = new TopLevelClass(serviceImpl);
        topLevelClass.setSuperClass(paramedRootServiceImpl);
        topLevelClass.addImportedType(autowiredAnnotationFqjt);
        topLevelClass.addAnnotation("@Service");
        topLevelClass.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Service"));

        topLevelClass.addImportedType(primaryKeyTypeFqjt);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" * service implements for table " + introspectedTable.getFullyQualifiedTable().getIntrospectedTableName());
        topLevelClass.addJavaDocLine(" * ");
        topLevelClass.addJavaDocLine(" * @author mybatis generator");
        topLevelClass.addJavaDocLine(" * @version " + new Date());
        topLevelClass.addJavaDocLine(" */");

        // 设置父类
        topLevelClass.addSuperInterface(serviceInterface);
        topLevelClass.addImportedType(serviceInterface);
        topLevelClass.addImportedType(rootServiceImpl);
        topLevelClass.addImportedType(baseRecordType);


        // dao repositoryAnnotation
        FullyQualifiedJavaType mybatis3JavaMapperType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());

        String shortName = mybatis3JavaMapperType.getShortName();

        String daoInterfaceAlias = shortName.substring(0, 1).toLowerCase() + shortName.substring(1);
        Field dao = new Field(daoInterfaceAlias, mybatis3JavaMapperType);
        dao.addAnnotation("@Autowired");
        dao.setVisibility(JavaVisibility.PRIVATE);
        topLevelClass.addField(dao);
        topLevelClass.addImportedType(autowiredAnnotationFqjt);

        // getDao Method
        Method getDao = new Method("getRepositoryDao");
        getDao.addAnnotation("@Override");
        getDao.setVisibility(JavaVisibility.PUBLIC);
        getDao.setReturnType(paramRootClient);
        topLevelClass.addImportedType(mybatis3JavaMapperType);

        getDao.addBodyLine("return this.".concat(daoInterfaceAlias).concat(";"));
        topLevelClass.addMethod(getDao);
        topLevelClass.addImportedType(rootClient);

        // setKey Method
        Method setDefaults = new Method("setDefaults");
        setDefaults.addAnnotation("@Override");
        setDefaults.setVisibility(JavaVisibility.PUBLIC);
        setDefaults.addParameter(new Parameter(baseRecordType, "record"));
        setDefaults.addBodyLine("// todo");
        topLevelClass.addImportedType(baseRecordType);

        Method getKeyValue = new Method("getKeyValue");
        Method setKeyValue = new Method("setKeyValue");
        Method emptyKeyValue = new Method("emptyKeyValue");

        if (generateSetGetKeyValue) {
            // getKeyValue Method
            getKeyValue.addAnnotation("@Override");
            getKeyValue.setVisibility(JavaVisibility.PUBLIC);
            getKeyValue.addParameter(new Parameter(baseRecordType, "record"));
            getKeyValue.setReturnType(FullyQualifiedJavaType.getStringInstance());
            topLevelClass.addImportedType(baseRecordType);

            setKeyValue.addAnnotation("@Override");
            setKeyValue.setVisibility(JavaVisibility.PUBLIC);
            setKeyValue.addParameter(new Parameter(baseRecordType, "record"));
            setKeyValue.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "id"));
            topLevelClass.addImportedType(baseRecordType);

            emptyKeyValue.addAnnotation("@Override");
            emptyKeyValue.setVisibility(JavaVisibility.PUBLIC);
            emptyKeyValue.addParameter(new Parameter(baseRecordType, "record"));
            topLevelClass.addImportedType(baseRecordType);

            topLevelClass.addMethod(getKeyValue);
            topLevelClass.addMethod(emptyKeyValue);
            topLevelClass.addMethod(setKeyValue);
        }

        boolean generated = false;
        boolean noTransferFlag = false;

        StringBuilder annotationString = new StringBuilder();
        annotationString.append("@UnAspectEscapeSpecialString(fields = {\"");
        List<IntrospectedColumn> baseColumns = introspectedTable.getBaseColumns();
        ArrayList<String> strings = new ArrayList<>();
        for(IntrospectedColumn introspectedColumn : baseColumns){
            if (introspectedColumn.getRemarks().indexOf("noTransfer") != -1) {
                noTransferFlag = true;
                strings.add(introspectedColumn.getActualColumnName());
            }
        }
        Integer index = 0;
        for (String columName : strings) {
            index += 1;
                if (index != strings.size()) {
                    annotationString.append(columName);
                    annotationString.append("\",");
                } else {
                    annotationString.append(columName);
                    annotationString.append("\"");
                }
        }
        annotationString.append("})");
        if (noTransferFlag) {
            //重写insertSelective方法
            Method insertSelective = new Method("insertSelective");
            insertSelective.setVisibility(JavaVisibility.PUBLIC);
            FullyQualifiedJavaType returnType = new FullyQualifiedJavaType("Msg");
            FullyQualifiedJavaType model = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
            returnType.addTypeArgument(model);
            insertSelective.setReturnType(returnType);
            insertSelective.addParameter(new Parameter(baseRecordType, "record", annotationString.toString()));
            topLevelClass.addImportedType(baseRecordType);
            insertSelective.addAnnotation("@Override");
            StringBuilder stringBuilder = new StringBuilder("");
            stringBuilder.append("return super.insertSelective(record)");
            topLevelClass.addMethod(insertSelective);
            topLevelClass.addImportedType(new FullyQualifiedJavaType("com.changan.carbond.utils.Msg"));
            insertSelective.addBodyLine(stringBuilder.toString());
            //重写updateByPrimaryKeySelective方法
            Method updateByPrimaryKeySelective = new Method("updateByPrimaryKeySelective");
            updateByPrimaryKeySelective.setVisibility(JavaVisibility.PUBLIC);
            FullyQualifiedJavaType returnType2 = new FullyQualifiedJavaType("Msg");
            returnType2.addTypeArgument(model);
            updateByPrimaryKeySelective.setReturnType(returnType);
            updateByPrimaryKeySelective.addParameter(new Parameter(baseRecordType, "record", annotationString.toString()));
            updateByPrimaryKeySelective.addAnnotation("@Override");
            StringBuilder stringBuilder2 = new StringBuilder("");
            stringBuilder2.append("return super.updateByPrimaryKeySelective(record)");
            topLevelClass.addMethod(updateByPrimaryKeySelective);
            updateByPrimaryKeySelective.addBodyLine(stringBuilder2.toString());
        }
        List<IntrospectedColumn> keyColumns = introspectedTable.getPrimaryKeyColumns();
        if (!keyColumns.isEmpty()) {

            for (IntrospectedColumn keyColumn : keyColumns) {
                String key = JavaBeansUtil.getCamelCaseString(keyColumn.getActualColumnName(), true);
                if (!generated) {
                    StringBuilder sbReturn = new StringBuilder("");

                    if (keyColumn.getFullyQualifiedJavaType().getShortName().equals("Long")) {
                        sbReturn.append("return Long.toString(record.get");
                        sbReturn.append(key);
                        sbReturn.append("());");
                    } else {
                        sbReturn.append("return record.get");
                        sbReturn.append(key);
                        sbReturn.append("();");
                    }

                    getKeyValue.addBodyLine(sbReturn.toString());

                    StringBuilder emptyKeyValueBody = new StringBuilder("record.set");
                    emptyKeyValueBody.append(key);
                    emptyKeyValueBody.append("(null);");
                    emptyKeyValue.addBodyLine(emptyKeyValueBody.toString());

                    StringBuilder setKeyValueBody = new StringBuilder("record.set");
                    setKeyValueBody.append(key);
                    if (keyColumn.getFullyQualifiedJavaType().getShortName().equals("Long")) {
                        setKeyValueBody.append("(Long.valueOf(id));");
                    } else {
                        setKeyValueBody.append("(id);");
                    }
                    setKeyValue.addBodyLine(setKeyValueBody.toString());

                    generated = true;
                }
                if (keyColumn.getFullyQualifiedJavaType().getShortName().equals("String")) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("if(record.get");
                    sb.append(key);
                    sb.append("() == null || \"\".equals(record.get");
                    sb.append(key);
                    sb.append("())) {");
                    setDefaults.addBodyLine(sb.toString());
                    sb = new StringBuilder();
                    sb.append("record.set");
                    sb.append(key);
                    sb.append("(");
                    sb.append("UUID.randomUUID().toString()");
                    sb.append(");");
                    setDefaults.addBodyLine(sb.toString());
                    setDefaults.addBodyLine("}");
                    topLevelClass.addImportedType(new FullyQualifiedJavaType("java.util.UUID"));
                } else if (keyColumn.getFullyQualifiedJavaType().getShortName().equals("Long")) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("if(record.get");
                    sb.append(key);
                    sb.append("() == null ) {");
                    setDefaults.addBodyLine(sb.toString());
                    sb = new StringBuilder();
                    sb.append("record.set");
                    sb.append(key);
                    sb.append("(");
                    sb.append("IdWorker");
                    sb.append(idGeneratorMethod);
                    sb.append("());");
                    setDefaults.addBodyLine(sb.toString());
                    setDefaults.addBodyLine("}");
                    topLevelClass.addImportedType(idGenerator);
                } else {
                    setDefaults.addBodyLine("");
                }
            }
        }

        topLevelClass.addMethod(setDefaults);

        javaFile = new GeneratedJavaFile(topLevelClass, context.getJavaModelGeneratorConfiguration()
                .getTargetProject(),
                context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                context.getJavaFormatter());
        files.add(javaFile);
        return files;
    }
}
