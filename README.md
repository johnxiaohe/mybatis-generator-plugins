## 自定义Mybatis构建插件
> 该插件实现构建可生成使用lombok注解的实体类  
> 并将数据库中的列名列注解标注在属性上  
> 还加入了Swagger类/属性注解

## 示例
```java
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Hexy
 * @date 2021-03-19
 * @tablename test
 * @tablecomment TEST: 测试表
 */
@Data
@ToString
@Accessors(chain = true)
@ApiModel(value = "PARK: 测试表实体类", description = "PARK: 测试表实体类")
public class ParkTest implements Serializable {
    
    /**
     * @fieldname name
     * @fieldcomment 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * @fieldname age
     * @fieldcomment 年龄
     */
    @ApiModelProperty(value = "年龄")
    private Integer age;

    /**
     * @fieldname parent
     * @fieldcomment 父亲
     */
    @ApiModelProperty(value = "父亲")
    private String parent;

    private static final long serialVersionUID = 1L;
}
```

## 配置方法
#### POM
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.mybatis.generator</groupId>
            <artifactId>mybatis-generator-maven-plugin</artifactId>
            <version>1.3.5</version>
            <configuration>
                <configurationFile>src/main/resources/generatorConfig.xml</configurationFile>
                <verbose>true</verbose>
                <overwrite>true</overwrite>
            </configuration>
            <dependencies>
                <!--添加该依赖可去除xml中指定jar包地址属性-->
                <dependency>
                    <groupId>mysql</groupId>
                    <artifactId>mysql-connector-java</artifactId>
                    <version>8.0.17</version>
                </dependency>
                <dependency>
                    <groupId>com.reuben.generator</groupId>
                    <artifactId>mybatis-generator-plugins</artifactId>
                    <version>1.0-SNAPshot</version>
                </dependency>
            </dependencies>
        </plugin>
    </plugins>
</build>
```

#### 构建配置
> generatorConfig.xml和mysql.properties文件放于resource下.修改mysql.properties中的配置参数即可.
> 如需要进行自定义则修改`ModelAnnotationsPlugin.makeAnnotations`方法即可

## 使用
> 将该项目打为jar包,并添加到maven仓库即可.