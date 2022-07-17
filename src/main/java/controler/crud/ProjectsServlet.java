package controler.crud;

import dto.ApiEntity;
import dto.factory.DtoFactory;
import dto.model.CompaniesDto;
import dto.model.CustomersDto;
import dto.model.ProjectsDto;
import dto.service.DtoService;
import org.thymeleaf.context.Context;
import util.ApiResponse;
import util.FormType;
import util.PropertiesLoader;
import view.UserViewBrowser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(urlPatterns =  {"/crud/create/projects",
        "/crud/read/projects",
        "/crud/delete/projects",
        "/crud/update/projects"})
public class ProjectsServlet extends HttpServlet {
    private static final UserViewBrowser browser = UserViewBrowser.of();
    private static final DtoFactory factory = DtoFactory.init(ApiEntity.PROJECTS);
    private static final DtoService dtoService = new DtoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getRequestURI().substring(6).split("/")[0];
        switch (action){
            case "create":
                sendInputForm(req, resp, FormType.CREATE);
                break;
            case "read":
                printProjects(req,resp);
                break;
            case "delete":
                if (req.getParameter("id") != null){
                    ApiResponse apiResponse = factory.delete(Long.valueOf(req.getParameter("id")));
                    Context context = new Context();
                    context.setVariable("apiResponse", apiResponse);
                    browser.sendRedirectOnPage(req,resp,"api_response", context);
                    return;
                }
                browser.sendRedirectOnPage(req,resp,"delete_id_input", new Context());
                break;
            case "update":
                sendInputForm(req,resp, FormType.UPDATE);
        }
    }

    private void printProjects(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Context context = new Context();
        context.setVariable("projectsList", factory.readAll());
        try {
            browser.sendRedirectOnPage(req, resp, "project_print_data", context);
        } catch (IOException e) {
            resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, e.getMessage());
        }
    }

    private void sendInputForm(HttpServletRequest req, HttpServletResponse resp, FormType type) throws IOException {
        try {
            if (type.equals(FormType.DELETE)) {
                browser.sendRedirectOnPage(req, resp, "delete_id_input", new Context());
                return;
            }

            List<CompaniesDto> companiesList = dtoService.convertResulSetToDtoList(
                    factory.executeAnyReadQuery("SELECT * FROM companies;"),
                    CompaniesDto.class);
            List<CustomersDto> customersList = dtoService.convertResulSetToDtoList(
                    factory.executeAnyReadQuery("SELECT * FROM customers;"),
                    CustomersDto.class);
            Context context = new Context();
            context.setVariable("companiesList", companiesList);
            context.setVariable("customersList", customersList);
            if (type.equals(FormType.CREATE)){
                browser.sendRedirectOnPage(req, resp, "project_create_form", context);
            }
            if (type.equals(FormType.UPDATE)){
                browser.sendRedirectOnPage(req, resp, "project_update_form", context);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getRequestURI().substring(6).split("/")[0];
        Context context = new Context();
        ApiResponse apiResponse;
        switch (action){
            case "create":
                ProjectsDto project = dtoService.parseRequestToDto(req, ProjectsDto.class);
                apiResponse = factory.save(project);
                break;
            case "delete":
                apiResponse = factory.delete(Long.valueOf(req.getParameter("id")));
                break;
            case "update":
                apiResponse = factory.update(Long.valueOf(req.getParameter("id")),
                        dtoService.parseRequestToDto(req, ProjectsDto.class));
                break;
            default:
                resp.sendRedirect(PropertiesLoader.getProperty("crud"));
                return;
        }
        context.setVariable("apiResponse", apiResponse);
        browser.sendRedirectOnPage(req,resp,"api_response", context);
    }
}
