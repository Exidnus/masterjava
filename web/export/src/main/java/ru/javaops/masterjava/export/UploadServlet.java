package ru.javaops.masterjava.export;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import ru.javaops.masterjava.common.web.ThymeleafUtil;
import ru.javaops.masterjava.persist.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@WebServlet("/")
public class UploadServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(UploadServlet.class);
    private final UserExport userExport = new UserExport();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final WebContext webContext = new WebContext(req, resp, req.getServletContext(), req.getLocale());
        final TemplateEngine engine = ThymeleafUtil.getTemplateEngine(getServletContext());
        engine.process("export", webContext, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final ServletFileUpload upload = new ServletFileUpload();
        try {
//            https://commons.apache.org/proper/commons-fileupload/streaming.html
            final FileItemIterator itemIterator = upload.getItemIterator(req);
            while (itemIterator.hasNext()) { //expect that it's only one file
                FileItemStream fileItemStream = itemIterator.next();
                if (!fileItemStream.isFormField()) {
                    try (InputStream is = fileItemStream.openStream()) {
                        List<User> users = userExport.process(is);
                        users.forEach(u -> LOG.info(u.toString()));
                    }
                    break;
                }
            }
            LOG.info("XML successfully uploaded");
        } catch (Exception e) {
            LOG.info(e.getMessage());
        }
        resp.sendRedirect(req.getContextPath());
    }
}
