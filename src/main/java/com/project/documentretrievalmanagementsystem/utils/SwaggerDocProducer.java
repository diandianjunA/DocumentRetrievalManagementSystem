package com.project.documentretrievalmanagementsystem.utils;

import io.github.swagger2markup.Swagger2MarkupConfig;
import io.github.swagger2markup.Swagger2MarkupConverter;
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;
import io.github.swagger2markup.markup.builder.MarkupLanguage;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

public class SwaggerDocProducer {
    public static void swaggerDoc() throws MalformedURLException {
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
                .withMarkupLanguage(MarkupLanguage.MARKDOWN)
                // 输出Markdown格式，可以修改文档类型，例MarkupLanguage.ASCIIDOC
                .build();

        Swagger2MarkupConverter.from(new URL("http://localhost:8070/v2/api-docs"))
                .withConfig(config)
                .build()
                .toFolder(Paths.get("src/docs/markdown/generated"));
        //这是生成的文档位置，可以修改为输出单文件，toFolder改为toFile
    }
}
