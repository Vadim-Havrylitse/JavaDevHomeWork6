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

    private static final UserViewBrowser BROWSER_VIEW;
    private static final DataBaseService DATA_BASE_SERVICE;
    private static final String QUERY_FOR_SUM_SALARY_IN_SOME_PROJECT;
    private static final String QUERY_FOR_LIST_ALL_DEV_IN_SOME_PROJECT;
    private static final String QUERY_FOR_LIST_ALL_JAVA_DEV;
    private static final String QUERY_FOR_LIST_ALL_MIDDLE_DEV;
    private static final String QUERY_FOR_LIST_ALL_PROJECT_WITH_SPEC_FORMAT;

    static {
        BROWSER_VIEW = UserViewBrowser.of();
        DATA_BASE_SERVICE = DataBaseService.of();
        QUERY_FOR_LIST_ALL_PROJECT_WITH_SPEC_FORMAT = "SELECT projects.release_date, projects.name, COUNT(developers_id) " +
            "FROM projects " +
            "LEFT JOIN developers_projects " +
            "ON developers_id = id " +
            "GROUP BY projects.id;";
        QUERY_FOR_LIST_ALL_MIDDLE_DEV = "SELECT developers.*" +
                "FROM developers " +
                "LEFT JOIN developers_skills " +
                "ON developers_id = developers.id " +
                "LEFT JOIN skills " +
                "ON skills_id = skills.id " +
                "WHERE skills.degree = 'Middle' " +
                "GROUP BY developers.id " +
                "ORDER BY developers.id;";
        QUERY_FOR_LIST_ALL_JAVA_DEV = "SELECT developers.*" +
                "FROM developers " +
                "LEFT JOIN developers_skills " +
                "ON developers_id = developers.id " +
                "LEFT JOIN skills " +
                "ON skills_id = skills.id " +
                "WHERE skills.industry = 'Java' " +
                "GROUP BY developers.id " +
                "ORDER BY developers.id;";
        QUERY_FOR_LIST_ALL_DEV_IN_SOME_PROJECT = "SELECT developers.* " +
                "FROM developers_projects " +
                "LEFT JOIN developers " +
                "ON developers_id = id " +
                "WHERE projects_id = %s;";
        QUERY_FOR_SUM_SALARY_IN_SOME_PROJECT = "SELECT projects.name, SUM(salary) " +
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
            String sqlQuery = String.format(QUERY_FOR_SUM_SALARY_IN_SOME_PROJECT, req.getParameter("sumDev"));
            printTableResult(req,resp,sqlQuery);
            return;
        }
        if (req.getParameter("projectDev") != null){
            String sqlQuery = String.format(QUERY_FOR_LIST_ALL_DEV_IN_SOME_PROJECT, req.getParameter("projectDev"));
            printTableResult(req, resp, sqlQuery);
            return;
        }
        if(req.getParameter("action") != null){
            switch (req.getParameter("action")){
                case "javaDev":
                    printTableResult(req, resp, QUERY_FOR_LIST_ALL_JAVA_DEV);
                    break;
                case "middleDev":
                    printTableResult(req, resp, QUERY_FOR_LIST_ALL_MIDDLE_DEV);
                    break;
                case "projectFormat":
                    printTableResult(req, resp, QUERY_FOR_LIST_ALL_PROJECT_WITH_SPEC_FORMAT);
                    break;
            }
            return;
        }
        BROWSER_VIEW.sendRedirect(resp, "сrud");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BROWSER_VIEW.sendRedirectOnPage(req, resp, "util_query", new Context());
    }

    private void printTableResult(HttpServletRequest req, HttpServletResponse resp, String sqlQuery){
        try {
            ResultSet resultSet = DATA_BASE_SERVICE.readData(sqlQuery);
            List<String> labelList = getLabels(resultSet);
            List<List<String>> valuesList = getValues(resultSet);
            Context context = new Context();
            context.setVariable("labelList", labelList);
            context.setVariable("valuesList", valuesList);
            BROWSER_VIEW.sendRedirectOnPage(req, resp, "util_query_print", context);
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