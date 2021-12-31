package com.mochat.mochat.common.util;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.util.Collections;

/**
 * 代码生成工具类
 * 
 * @author: Ypw / ypwcode@163.com
 * @time: 2021/12/31
 */
public class GeneratorUtils {

    public static void main(String[] args) {
        FastAutoGenerator.create(
                "jdbc:mysql://127.0.0.1:3306/mochat",
                "root",
                "12345678"
        )
                .globalConfig(builder -> {
                    builder.author("mochat") // 设置作者
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("./generator/src/main/java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.mochat.mochat") // 设置父包名
                            .moduleName("gen") // 设置父包模块名
                            .entity("entity") // 设置实体模块名
                            .service("service") // 设置服务模块名
                            .serviceImpl("service.impl") // 设置服务实现模块名
                            .mapper("mapper") // 设置 mapper 模块名
                            .controller("controller") // 设置控制器模块名
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "./generator/src/main/resources/mapper")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder
//                            .addInclude("mc_corp") // 设置需要生成的表名
                            .addTablePrefix("mc") // 设置需要去除的表前缀
                            .entityBuilder() // 以下设置 Entity 配置
                            .enableChainModel() // 开启链式模型
                            .enableLombok() // 开启 Lombok
                            .enableRemoveIsPrefix() // 开启 Boolean 类型字段移除 is 前缀
                            .enableActiveRecord() // 开启 ActiveRecord 模型
                            .logicDeleteColumnName("deleted_at") // 逻辑删除字段名
                            .addTableFills(new Column("created_at", FieldFill.INSERT)) // 添加表字段填充
                            .addTableFills(new Column("updated_at", FieldFill.INSERT_UPDATE)) // 添加表字段填充
                            .idType(IdType.AUTO) // id 自增
                            .formatFileName("%sEntity") // 文件名格式化
                            .build();
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}