package ru.javaops.masterjava.export;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import ru.javaops.masterjava.common.web.ThymeleafUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@WebServlet("/")
@Slf4j
public class UploadServlet extends HttpServlet {

    private final ExportChain exportChain = new ExportChain(
            //Export classes order must be equal elements order in xml.
            ImmutableList.of(new ProjectGroupExport(), new CityExport(), new UserExport())
    );

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        outExport(req, resp, "", 2000);
    }

    private void outExport(HttpServletRequest req, HttpServletResponse resp, String message, int chunkSize) throws IOException {
        final WebContext webContext =
                new WebContext(req, resp, req.getServletContext(), req.getLocale(),
                        ImmutableMap.of("message", message, "chunkSize", chunkSize));
        final TemplateEngine engine = ThymeleafUtil.getTemplateEngine(getServletContext());
        engine.process("export", webContext, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String message = null;
        int chunkSize = 0;


        try {
//            https://commons.apache.org/proper/commons-fileupload/streaming.html
//            http://stackoverflow.com/a/32476545/548473
            final ServletFileUpload upload = new ServletFileUpload();
            final FileItemIterator itemIterator = upload.getItemIterator(req);
            while (itemIterator.hasNext()) { //expect that it's only one file
                FileItemStream item = itemIterator.next();
                if (item.isFormField()) {
                    if ("chunkSize".equals(item.getFieldName())) {
                        chunkSize = Integer.valueOf(Streams.asString(item.openStream()));
                        if (chunkSize < 1) {
                            message = "Chunk Size must be > 1";
                            break;
                        }
                    }
                } else if (Strings.isNullOrEmpty(item.getName())) {
                    message = "Upload file is not selected";
                } else {
                    try (InputStream is = item.openStream()) {
                        message = exportChain.process(is, chunkSize);
                    }
                    log.info("XML successfully uploaded");
                    break;
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        outExport(req, resp, message, chunkSize);
    }
}
