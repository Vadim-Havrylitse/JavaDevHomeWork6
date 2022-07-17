package controler;

import database.service.DataBaseService;
import org.thymeleaf.context.Context;
import view.UserViewBrowser;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/util_query")
public class UtilQueryServlet extends HttpServlet {

    private static final UserViewBrowser browser;
    private static final DataBaseService database;
    private static final String queryForSumSalaryInSomeProject;
    private static final String queryForListAllDevInSomeProject;
    private static final String queryForListAllJavaDev;
    private static final String queryForListAllMiddleDev;
    private static final String queryForListAllProjectWithSpecFormat;

    static {
        browser = UserViewBrowser.of();
        database = DataBaseService.of();
        queryForListAllProjectWithSpecFormat = "SELECT projects.release_date, projects.name, COUNT(developers_id) " +
            "FROM projects " +
            "LEFT JOIN developers_projects " +
            "ON developers_id = id " +
            "GROUP BY projects.id;";
        queryForListAllMiddleDev = "SELECT developers.*" +
                "FROM developers " +
                "LEFT JOIN developers_skills " +
                "ON developers_id = developers.id " +
                "LEFT JOIN skills " +
                "ON skills_id = skills.id " +
                "WHERE skills.degree = 'Middle' " +
                "GROUP BY developers.id " +
                "ORDER BY developers.id;";
        queryForListAllJavaDev = "SELECT developers.*" +
                "FROM developers " +
                "LEFT JOIN developers_skills " +
                "ON developers_id = developers.id " +
                "LEFT JOIN skills " +
                "ON skills_id = skills.id " +
                "WHERE skills.industry = 'Java' " +
                "GROUP BY developers.id " +
                "ORDER BY developers.id;";
        queryForListAllDevInSomeProject = "SELECT developers.* " +
                "FROM developers_projects " +
                "LEFT JOIN developers " +
                "ON developers_id = id " +
                "WHERE projects_id = %s;";
        queryForSumSalaryInSomeProject = "SELECT projects.name, SUM(salary) " +
                "FROM projects " +
                "LEFT JOIN developers_projects " +
                "ON id = projects_id " +
                "LEFT JOIN developers " +
                "ON developers_id = developers.id " +
                "WHERE projects.id = %s "+
                "GROUP BY projects.name;";
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if(req.getParameter("sumDev") != null){
            String sqlQuery = String.format(queryForSumSalaryInSomeProject, req.getParameter("sumDev"));
            printTableResult(req,resp,sqlQuery);
            return;
        }
        if (req.getParameter("projectDev") != null){
            String sqlQuery = String.format(queryForListAllDevInSomeProject, req.getParameter("projectDev"));
            printTableResult(req, resp, sqlQuery);
            return;
        }
        if(req.getParameter("action") != null){
            switch (req.getParameter("action")){
                case "javaDev":
                    printTableResult(req, resp, queryForListAllJavaDev);
                    break;
                case "middleDev":
                    printTableResult(req, resp, queryForListAllMiddleDev);
                    break;
                case "projectFormat":
                    printTableResult(req, resp, queryForListAllProjectWithSpecFormat);
                    break;
            }
            return;
        }
        browser.sendRedirect(resp, "сrud");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        browser.sendRedirectOnPage(req, resp, "util_query", new Context());
    }

    private void printTableResult(HttpServletRequest req, HttpServletResponse resp, String sqlQuery){
        try {
            ResultSet resultSet = database.readData(sqlQuery);
            List<String> labelList = getLabels(resultSet);
            List<List<String>> valuesList = getValues(resultSet);
            Context context = new Context();
            context.setVariable("labelList", labelList);
            context.setVariable("valuesList", valuesList);
            browser.sendRedirectOnPage(req, resp, "util_query_print", context);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private List<List<String>> getValues(ResultSet resultSet) throws SQLException {
        List<List<String>> result = new ArrayList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int maxColumns = metaData.getColumnCount();
        while(resultSet.next()){
            List<String> resultElement = new ArrayList<>();
            for (int i = 1; i <= maxColumns; i++) {
                resultElement.add(resultSet.getString(i));
            }
            result.add(resultElement);
        }
        return result;
    }

    private List<String> getLabels(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int maxColumns = metaData.getColumnCount();
        List<String> result = new ArrayList<>();
        for (int i = 1; i <= maxColumns; i++) {
            result.add(metaData.getColumnLabel(i));
        }
        return result;
    }
}