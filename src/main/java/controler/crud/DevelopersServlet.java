package controler.crud;

import dto.ApiEntity;
import dto.factory.DtoFactory;
import dto.model.CompaniesDto;
import dto.model.DevelopersDto;
import dto.model.ProjectsDto;
import dto.model.SkillsDto;
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

@WebServlet(urlPatterns = {"/crud/create/developers",
        "/crud/read/developers",
        "/crud/delete/developers",
        "/crud/update/developers"})
public class DevelopersServlet extends HttpServlet {
    private static final UserViewBrowser browser = UserViewBrowser.of();
    private static final DtoFactory factory = DtoFactory.init(ApiEntity.DEVELOPERS);
    private static final DtoService dtoService = new DtoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getRequestURI().substring(6).split("/")[0];
        switch (action){
            case "create":
                sendInputForm(req, resp, FormType.CREATE);
                break;
            case "read":
                printDevelopers(req, resp);
                break;
            case "delete":
                if (req.getParameter("id") != null){
                    ApiResponse apiResponse = factory.delete(Long.valueOf(req.getParameter("id")));
                    Context context = new Context();
                    context.setVariable("apiResponse", apiResponse);
                    browser.sendRedirectOnPage(req,resp,"api_response", context);
                    return;
                }
                sendInputForm(req, resp, FormType.DELETE);
                break;
            case "update":
                sendInputForm(req, resp, FormType.UPDATE);
        }
    }

    private void printDevelopers(HttpServletRequest req, HttpServletResponse resp) {
        Context context = new Context();
        context.setVariable("developersList", factory.readAll());
        try {
            browser.sendRedirectOnPage(req, resp, "developer_print_data", context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendInputForm(HttpServletRequest req, HttpServletResponse resp, FormType type) {
        try {
            if(type.equals(FormType.DELETE)) {
                browser.sendRedirectOnPage(req, resp, "delete_id_input", new Context());
                return;
            }
            List<ProjectsDto> projectsList = dtoService.convertResulSetToDtoList(
                     factory.executeAnyReadQuery("SELECT * FROM projects;"),
                     ProjectsDto.class);
            List<CompaniesDto> companiesList = dtoService.convertResulSetToDtoList(
                    factory.executeAnyReadQuery("SELECT * FROM companies;"),
                    CompaniesDto.class);
            List<SkillsDto> skillsList = dtoService.convertResulSetToDtoList(
                     factory.executeAnyReadQuery("SELECT * FROM skills;"),
                 SkillsDto.class);
            Context context = new Context();
            context.setVariable("companiesList", companiesList);
            context.setVariable("projectsList", projectsList);
            context.setVariable("skillsList", skillsList);
            if (type.equals(FormType.CREATE)){
                browser.sendRedirectOnPage(req, resp, "developer_create_form", context);
            }
            if (type.equals(FormType.UPDATE)){
                browser.sendRedirectOnPage(req, resp, "developer_update_form", context);
            }
         } catch (IOException | SQLException e){
            e.printStackTrace();
         }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getRequestURI().substring(6).split("/")[0];
        Context context = new Context();
        ApiResponse apiResponse;
        switch (action){
            case "create":
                DevelopersDto dev = dtoService.parseRequestToDto(req, DevelopersDto.class);
                apiResponse = factory.save(dev);
                break;
            case "delete":
                apiResponse = factory.delete(Long.valueOf(req.getParameter("id")));
                break;
            case "update":
                apiResponse = factory.update(Long.valueOf(req.getParameter("id")),
                        dtoService.parseRequestToDto(req, DevelopersDto.class));
                break;
            default:
                resp.sendRedirect(PropertiesLoader.getProperty("crud"));
                return;
        }
        context.setVariable("apiResponse", apiResponse);
        browser.sendRedirectOnPage(req,resp,"api_response", context);
    }
}