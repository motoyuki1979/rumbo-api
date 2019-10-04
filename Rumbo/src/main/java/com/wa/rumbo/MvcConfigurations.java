package com.wa.rumbo;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@ComponentScan(basePackages = "com.pusher.apps")
@EnableWebMvc
public class MvcConfigurations extends WebMvcConfigurerAdapter {

    public final static String JDBC_URL = "jdbc:mysql://localhost:3306/app_Rumbo?useUnicode=yes&characterEncoding=UTF-8";
    public final static String JDBC_USER = "root";
    //public final static String JDBC_PASSWORD = "redhat";	
    public final static String JDBC_PASSWORD = "p@@p!";
    private static final Logger logger = LoggerFactory.getLogger(MvcConfigurations.class);

    @Bean
    public ViewResolver getViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/jsp/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // TODO Auto-generated method stub
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    }

    @Bean
    public DataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");

        dataSource.setUrl(JDBC_URL);
        dataSource.setUsername(JDBC_USER);
        dataSource.setPassword(JDBC_PASSWORD);
        return dataSource;
    }

    @Bean
    public DbServices getDbServices() {
        return new DbServiceImpl(getDataSource());
    }
}
