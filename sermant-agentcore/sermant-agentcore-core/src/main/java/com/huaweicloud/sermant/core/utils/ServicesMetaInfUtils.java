/*
 * Copyright (C) 2024-2024 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huaweicloud.sermant.core.utils;

import com.huaweicloud.sermant.core.service.httpserver.annotation.HttpRouteMapping;

import org.kohsuke.MetaInfServices;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * 添加@HttpRouteMapping注解的类，
 * 在编译时自动生成com.huaweicloud.sermant.core.service.httpserver.api.HttpRouteHandler的SPI
 *
 * @author zwmagic
 * @since 2024-02-03
 */
@SupportedSourceVersion(value = SourceVersion.RELEASE_8)
@MetaInfServices(Processor.class)
public class ServicesMetaInfUtils extends AbstractProcessor {

    private static final String ANNOTATION =
            "com.huaweicloud.sermant.core.service.httpserver.annotation.HttpRouteMapping";
    private static final String INTERFACE = "com.huaweicloud.sermant.core.service.httpserver.api.HttpRouteHandler";

    private Messager messager;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(ANNOTATION);
        return annotations;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }

        if (roundEnv.getRootElements().isEmpty()) {
            return false;
        }

        try {
            Elements elements = processingEnv.getElementUtils();
            Set<String> services = new TreeSet<>();
            for (Element element : roundEnv.getElementsAnnotatedWith(HttpRouteMapping.class)) {
                HttpRouteMapping annotation = element.getAnnotation(HttpRouteMapping.class);
                if (annotation == null) {
                    continue;
                }
                if (!element.getKind().isClass() && !element.getKind().isInterface()) {
                    continue;
                }
                services.add(elements.getBinaryName((TypeElement) element).toString());
            }
            this.writeToMetaInf(INTERFACE, services);
        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
        return false;
    }

    private void writeToMetaInf(String contact, Set<String> services) {
        if (services.isEmpty()) {
            return;
        }

        Filer filer = processingEnv.getFiler();
        PrintWriter pw = null;
        try {
            String fileName = getResourceFileName(contact);
            messager.printMessage(Diagnostic.Kind.NOTE, "Writing " + fileName);
            FileObject fo = filer.createResource(StandardLocation.CLASS_OUTPUT, "", fileName);
            pw = new PrintWriter(new OutputStreamWriter(fo.openOutputStream(), StandardCharsets.UTF_8));

            for (String service : services) {
                pw.println(service);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write generated files: " + e);
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    private String getResourceFileName(String contact) {
        return "META-INF/services/" + contact;
    }

}