package controler.crud;

import dto.factory.DtoFactory;
import org.thymeleaf.context.Context;
import view.UserViewBrowser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

@WebServlet(value = "/crud", name = "MainServlet")
public class CrudServlet extends HttpServlet {
    private static final UserViewBrowser browser;
    private static final List<String> tablesName;

    static {
        tablesName = DtoFactory.getTablesName();
        browser = UserViewBrowser.of();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String tableName = req.getParameter("table");
        if (!tablesName.contains(tableName)) {
            Context context = new Context();
            context.setVariable("tablesList", tablesName);
            browser.sendRedirectOnPage(req, resp, "MAIN_PAGE", context);
        } else {
            String url = "http://localhost:8081" +
                    req.getServletPath() +
                    "/" +
                    tableName.toLowerCase(Locale.ROOT);
            System.out.println(url);
            resp.sendRedirect(url);
        }
    }
}
