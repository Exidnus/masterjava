package ru.javaops.masterjava;

import ru.javaops.masterjava.service.UserServiceXml;

import javax.servlet.http.HttpServlet;

/**
 * @author Varygin DV {@literal <OUT-Varygin-DV@mail.ca.sbrf.ru>}
 */
public class UploadServlet extends HttpServlet {

    private final UserServiceXml userServiceXml = UserServiceXml.getUserServiceXml();

    //TODO get or post?
}
