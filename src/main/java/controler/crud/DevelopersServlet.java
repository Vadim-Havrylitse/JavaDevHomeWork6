package controler.crud;

import dto.model.CompaniesDto;
import dto.model.DevelopersDto;
import dto.model.ProjectsDto;
import dto.model.SkillsDto;
import org.thymeleaf.context.Context;
import service.DtoFactory;
import util.ApiEntity;
import util.ApiResponse;
import util.FormType;
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
    private static final UserViewBrowser BROWSER_VIEW = UserViewBrowser.of();
    private static final DtoFactory SERVICE = DtoFactory.init(ApiEntity.DEVELOPERS);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String url = req.getRequestURI();
        if (url.contains("create")) {
            sendInputForm(req, resp, FormType.CREATE);
        }
        if (url.contains("read")) {
            printDevelopers(req, resp);
        }
        if (url.contains("delete")) {
            if (req.getParameter("id") != null){
                ApiResponse apiResponse = SERVICE.delete(Long.valueOf(req.getParameter("id")));
                Context context = new Context();
                context.setVariable("apiResponse", apiResponse);
                BROWSER_VIEW.sendRedirectOnPage(req,resp,"api_response", context);
                return;
            }
            sendInputForm(req, resp, FormType.DELETE);
        }
        if (url.contains("update")) {
            sendInputForm(req,resp, FormType.UPDATE);
        }
    }

    private void printDevelopers(HttpServletRequest req, HttpServletResponse resp) {
        Context context = new Context();
        context.setVariable("developersList", SERVICE.readAll());
        try {
            BROWSER_VIEW.sendRedirectOnPage(req, resp, "developer_print_data", context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendInputForm(HttpServletRequest req, HttpServletResponse resp, FormType type) {
        try {
            if(type.equals(FormType.DELETE)) {
                BROWSER_VIEW.sendRedirectOnPage(req, resp, "delete_id_input", new Context());
                return;
            }
            List<ProjectsDto> projectsList = SERVICE.convertResulSetToDtoList(
                     SERVICE.executeAnyReadQuery("SELECT * FROM projects;"),
                     ProjectsDto.class);
            List<CompaniesDto> companiesList = SERVICE.convertResulSetToDtoList(
                    SERVICE.executeAnyReadQuery("SELECT * FROM companies;"),
                    CompaniesDto.class);
            List<SkillsDto> skillsList = SERVICE.convertResulSetToDtoList(
                     SERVICE.executeAnyReadQuery("SELECT * FROM skills;"),
                 SkillsDto.class);
            Context context = new Context();
            context.setVariable("companiesList", companiesList);
            context.setVariable("projectsList", projectsList);
            context.setVariable("skillsList", skillsList);
            if (type.equals(FormType.CREATE)){
                BROWSER_VIEW.sendRedirectOnPage(req, resp, "developer_create_form", context);
            }
            if (type.equals(FormType.UPDATE)){
                BROWSER_VIEW.sendRedirectOnPage(req, resp, "developer_update_form", context);
            }
         } catch (IOException | SQLException e){
            e.printStackTrace();
         }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Context context = new Context();
        ApiResponse apiResponse = null;
        String url = req.getRequestURI();
        if (url.contains("create")) {
            DevelopersDto dev = SERVICE.parseRequestToDto(req, DevelopersDto.class);
            apiResponse = SERVICE.save(dev);
        }
        if (url.contains("delete")) {
            apiResponse = SERVICE.delete(Long.valueOf(req.getParameter("id")));
        }
        if (url.contains("update")) {
            apiResponse = SERVICE.update(Long.valueOf(req.getParameter("id")),
                    SERVICE.parseRequestToDto(req, DevelopersDto.class));
        }
        context.setVariable("apiResponse", apiResponse);
        BROWSER_VIEW.sendRedirectOnPage(req,resp,"api_response", context);
    }
}