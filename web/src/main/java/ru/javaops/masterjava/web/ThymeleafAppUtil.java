package ru.javaops.masterjava.web;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;

/**
 * @author Varygin DV {@literal <OUT-Varygin-DV@mail.ca.sbrf.ru>}
 */
class ThymeleafAppUtil {

    private ThymeleafAppUtil() {

    }

    static TemplateEngine getTemplateEngine(ServletContext context) {
        final ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheTTLMs(3_600_600L);
        final TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(templateResolver);
        return engine;
    }
}
