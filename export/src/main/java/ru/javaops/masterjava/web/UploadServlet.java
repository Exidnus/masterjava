package ru.javaops.masterjava.web;

import com.google.common.base.Strings;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Varygin DV {@literal <OUT-Varygin-DV@mail.ca.sbrf.ru>}
 */
@WebServlet("/export")
public class UploadServlet extends HttpServlet {

    private final UserServiceXml userServiceXml = UserServiceXml.getUserServiceXml();
    private final ExecutorService service = Executors.newFixedThreadPool(4);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final WebContext webContext = new WebContext(req, resp, req.getServletContext(), req.getLocale());
        final TemplateEngine engine = ThymeleafAppUtil.getTemplateEngine(getServletContext());
        engine.process("export", webContext, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final DiskFileItemFactory factory = new DiskFileItemFactory();
        final ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            final List<FileItem> fileItems = upload.parseRequest(req);
            final List<Future> futures = new ArrayList<>();
            fileItems.stream()
                    .filter(file -> !Strings.isNullOrEmpty(file.getName()))
                    .forEach(file -> {
                        final Future<?> future = service.submit(() -> {
                            try (InputStream is = file.getInputStream()) {
                                userServiceXml.saveUsersFromXmlToBD(is);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        futures.add(future);
                    });

            futures.forEach(future -> {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (FileUploadException e) {
            throw new RuntimeException(e);
        }
    }
}
