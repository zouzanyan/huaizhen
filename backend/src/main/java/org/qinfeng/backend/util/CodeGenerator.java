package org.qinfeng.backend.util;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.nio.file.Paths;

public class CodeGenerator {

    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://127.0.0.1:3306/huaizhen", "root", "root")
                .globalConfig(builder -> builder
                        .author("qinfeng")
                        .outputDir(Paths.get(System.getProperty("user.dir")) + "/backend/src/main/java")
                        .commentDate("yyyy-MM-dd")
                )
                .packageConfig(builder -> builder
                        .parent("org.qinfeng.backend")
                        .entity("entity")
                        .mapper("mapper")
                        .service("service")
                        .serviceImpl("service.impl")
                        .xml("mapper.xml")
                )
                .strategyConfig(builder -> builder
                        .entityBuilder()
                        .enableLombok()
                )
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}

class da{
    public static void main(String[] args) {
        System.out.println(Paths.get(System.getProperty("user.dir")));
    }
}
