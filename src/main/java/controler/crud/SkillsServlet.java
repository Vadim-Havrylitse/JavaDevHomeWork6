package controler.crud;

import dto.model.SkillsDto;
import org.thymeleaf.context.Context;
import service.DtoFactory;
import util.ApiEntity;
import util.ApiResponse;
import util.FormType;
import view.UserViewBrowser;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/crud/create/skills",
        "/crud/read/skills",
        "/crud/delete/skills",
        "/crud/update/skills"})
public class SkillsServlet extends HttpServlet {
    private static final UserViewBrowser BROWSER_VIEW = UserViewBrowser.of();
    private static final DtoFactory SERVICE = DtoFactory.init(ApiEntity.SKILLS);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String url = req.getRequestURI();
        if (url.contains("create")) {
            sendInputForm(req, resp, FormType.CREATE);
        }
        if (url.contains("read")) {
            printSkills(req, resp);
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

    private void printSkills(HttpServletRequest req, HttpServletResponse resp) {
        Context context = new Context();
        context.setVariable("skillsList", SERVICE.readAll());
        try {
            BROWSER_VIEW.sendRedirectOnPage(req, resp, "skill_print_data", context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendInputForm(HttpServletRequest req, HttpServletResponse resp, FormType type) {
            String nameHtmlFile = null;
            switch (type){
                case CREATE:
                    nameHtmlFile = "skill_create_form";
                    break;
                case DELETE:
                    nameHtmlFile = "delete_id_input";
                    break;
                case UPDATE:
                    nameHtmlFile = "skill_update_form";
            }
        try {
            BROWSER_VIEW.sendRedirectOnPage(req, resp, nameHtmlFile, new Context());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Context context = new Context();
        ApiResponse apiResponse = null;
        String url = req.getRequestURI();
        if (url.contains("create")) {
            SkillsDto skill = SERVICE.parseRequestToDto(req, SkillsDto.class);
            apiResponse = SERVICE.save(skill);
        }
        if (url.contains("delete")) {
            apiResponse = SERVICE.delete(Long.valueOf(req.getParameter("id")));
        }
        if (url.contains("update")) {
            apiResponse = SERVICE.update(Long.valueOf(req.getParameter("id")),
                    SERVICE.parseRequestToDto(req, SkillsDto.class));
        }
        context.setVariable("apiResponse", apiResponse);
        BROWSER_VIEW.sendRedirectOnPage(req,resp,"api_response", context);
    }
}
