package com.reuben.generator.plugins;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class ModelAnnotationsPlugin extends PluginAdapter {

    private FullyQualifiedJavaType apiModel;
    private FullyQualifiedJavaType accessors;
    private FullyQualifiedJavaType lombok;
    //字段注解添加类
    private FullyQualifiedJavaType apiModelProperty;
    private FullyQualifiedJavaType toStringSerializer;
    private FullyQualifiedJavaType jsonSerialize;
    private FullyQualifiedJavaType jsonFormat;
    private FullyQualifiedJavaType dateTimeFormat;

    private String generatorTime = "1970-01-01";

    public ModelAnnotationsPlugin() {
        super();

        this.apiModel = new FullyQualifiedJavaType("io.swagger.annotations.ApiModel");
        this.accessors = new FullyQualifiedJavaType("lombok.experimental.Accessors");
        this.lombok = new FullyQualifiedJavaType("lombok.*");

        this.apiModelProperty = new FullyQualifiedJavaType("io.swagger.annotations.ApiModelProperty");
        this.toStringSerializer = new FullyQualifiedJavaType("com.fasterxml.jackson.databind.ser.std.ToStringSerializer");
        this.jsonSerialize = new FullyQualifiedJavaType("com.fasterxml.jackson.databind.annotation.JsonSerialize");
        this.jsonFormat = new FullyQualifiedJavaType("com.fasterxml.jackson.annotation.JsonFormat");
        this.dateTimeFormat = new FullyQualifiedJavaType("org.springframework.format.annotation.DateTimeFormat");

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        this.generatorTime = dateFormatter.format(new Date());
    }

    @Override
    public boolean validate(List<String> warnings) {
        // this plugin is always valid
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        String typeSortName = field.getType().getShortName();
        switch (typeSortName){
            case "Long":
                topLevelClass.addImportedType(jsonSerialize);
                topLevelClass.addImportedType(toStringSerializer);
                field.addAnnotation("@JsonSerialize(using = ToStringSerializer.class)");
                break;
            case "Date":
                topLevelClass.addImportedType(jsonFormat);
                topLevelClass.addImportedType(dateTimeFormat);
                field.addAnnotation("@DateTimeFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")");
                field.addAnnotation("@JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\", timezone = \"GMT+8\")");
                break;
            default:
                break;
        }
        field.addAnnotation("@ApiModelProperty(value = \""+introspectedColumn.getRemarks()+"\")");
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        makeAnnotations(topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        makeAnnotations(topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        makeAnnotations(topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

    private void makeAnnotations(TopLevelClass topLevelClass, IntrospectedTable introspectedTable){
        // 类引入
        topLevelClass.addImportedType(apiModel);
        topLevelClass.addImportedType(accessors);
        topLevelClass.addImportedType(lombok);
        //属性引入
        topLevelClass.addImportedType(apiModelProperty);

        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" * @author "+properties.getProperty("author", properties.getProperty("author", "Hexy")));
        topLevelClass.addJavaDocLine(" * @date "+generatorTime);
        topLevelClass.addJavaDocLine(" * @tablename "+introspectedTable.getFullyQualifiedTable());
        topLevelClass.addJavaDocLine(" * @tablecomment " + introspectedTable.getRemarks());
        topLevelClass.addJavaDocLine(" */");
//        topLevelClass.addAnnotation("@TableName("+introspectedTable.getFullyQualifiedTable()+")");
        topLevelClass.addAnnotation("@Data");
        topLevelClass.addAnnotation("@ToString");
        topLevelClass.addAnnotation("@Accessors(chain = true)");

        if(null != topLevelClass.getSuperClass() && "" != topLevelClass.getSuperClass().getFullyQualifiedName() && "Object" != topLevelClass.getSuperClass().getShortName()){
            topLevelClass.addAnnotation("@EqualsAndHashCode(callSuper = true)");
        }

        topLevelClass.addAnnotation("@ApiModel(value = \""+introspectedTable.getRemarks()+"实体类\", description = \""+introspectedTable.getRemarks()+"实体类\")");
    }
}
