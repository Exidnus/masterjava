package ru.javaops.masterjava.web;

import com.google.common.base.Strings;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import ru.javaops.masterjava.service.UserServiceXml;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

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
        final ServletFileUpload upload = new ServletFileUpload();
        try {
            final FileItemIterator itemIterator = upload.getItemIterator(req);
            while (itemIterator.hasNext()) {
                final FileItemStream stream = itemIterator.next();
                if (!Strings.isNullOrEmpty(stream.getName())) {
                    try (InputStream is = stream.openStream()) {
                        userServiceXml.saveUsersFromXmlToBD("topjava", is);
                    }
                }
            }
        } catch (FileUploadException e) {
            throw new RuntimeException(e);
        }
    }
}
