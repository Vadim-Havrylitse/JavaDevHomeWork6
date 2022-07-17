package view;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import util.PropertiesLoader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UserViewBrowserImpl implements UserViewBrowser {

    @Override
    public void sendRedirect(HttpServletResponse resp, String propertyName) throws IOException {
        resp.sendRedirect(PropertiesLoader.getProperty(propertyName));
    }

    @Override
    public void sendRedirectOnPage(HttpServletRequest req, HttpServletResponse resp, String nameHtmlFile, Context context) throws IOException {
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(createTemplateResolver(req));
        resp.setContentType("text/html;charset=UTF-8");
        templateEngine.process(nameHtmlFile, context, resp.getWriter());
    }

    private ServletContextTemplateResolver createTemplateResolver(HttpServletRequest req){
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(req.getServletContext());
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setPrefix("/WEB-INF/thymeleaf/");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheable(true);
        return templateResolver;
    }
}