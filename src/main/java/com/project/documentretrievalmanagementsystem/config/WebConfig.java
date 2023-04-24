package com.project.documentretrievalmanagementsystem.config;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;


public class WebConfig implements WebApplicationInitializer {

    @Override
    public void onStartup(javax.servlet.ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext ctx=new AnnotationConfigWebApplicationContext();
        ctx.register(WebMvcConfig.class);//注册SpringMvc的配置类WebMvcConfig
        ctx.setServletContext(servletContext);//和当前ServletContext关联
        /**
         * 注册SpringMvc的DispatcherServlet
         */
        ServletRegistration.Dynamic servlet=servletContext.addServlet("dispatcher", (Servlet) new DispatcherServlet(ctx));
        servlet.addMapping("/");
        servlet.setLoadOnStartup(1);
    }
}
