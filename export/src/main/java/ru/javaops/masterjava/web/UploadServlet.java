package ru.javaops.masterjava.web;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import ru.javaops.masterjava.service.UserServiceXml;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Varygin DV {@literal <OUT-Varygin-DV@mail.ca.sbrf.ru>}
 */
@WebServlet("/export")
public class UploadServlet extends HttpServlet {

    private final UserServiceXml userServiceXml = UserServiceXml.getUserServiceXml();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final WebContext webContext = new WebContext(req, resp, req.getServletContext(), req.getLocale());
        final TemplateEngine engine = ThymeleafAppUtil.getTemplateEngine(getServletContext());
        engine.process("export", webContext, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("!!!");
    }
}
