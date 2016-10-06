package ru.javaops.masterjava.web;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import ru.javaops.masterjava.da.model.UserDaDto;
import ru.javaops.masterjava.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author Varygin DV {@literal <OUT-Varygin-DV@mail.ca.sbrf.ru>}
 */
@WebServlet("/all")
public class UserServlet extends HttpServlet {

    private final UserService userService = UserService.getUserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final List<UserDaDto> users = userService.getAllSorted();
        final WebContext webContext = new WebContext(req, resp, req.getServletContext(), req.getLocale());
        webContext.setVariable("users", users);
        final TemplateEngine engine = ThymeleafAppUtil.getTemplateEngine(getServletContext());
        engine.process("allUsers", webContext, resp.getWriter());
    }
}
