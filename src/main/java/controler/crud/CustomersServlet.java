package controler.crud;

import dto.ApiEntity;
import dto.factory.DtoFactory;
import dto.model.CustomersDto;
import dto.service.DtoService;
import org.thymeleaf.context.Context;
import util.ApiResponse;
import util.FormType;
import util.PropertiesLoader;
import view.UserViewBrowser;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/crud/create/customers",
        "/crud/read/customers",
        "/crud/delete/customers",
        "/crud/update/customers"})
public class CustomersServlet extends HttpServlet {
    private static final UserViewBrowser browser = UserViewBrowser.of();
    private static final DtoFactory factory = DtoFactory.init(ApiEntity.CUSTOMERS);
    private static final DtoService dtoService = new DtoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getRequestURI().substring(6).split("/")[0];
        switch (action){
            case "create":
                sendInputForm(req, resp, FormType.CREATE);
                break;
            case "read":
                printCustomers(req, resp);
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

    private void printCustomers(HttpServletRequest req, HttpServletResponse resp) {
        Context context = new Context();
        context.setVariable("customersList", factory.readAll());
        try {
            browser.sendRedirectOnPage(req, resp, "customer_print_data", context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendInputForm(HttpServletRequest req, HttpServletResponse resp, FormType type) {
            String nameHtmlFile = null;
            switch (type){
                case CREATE:
                    nameHtmlFile = "customer_create_form";
                    break;
                case DELETE:
                    nameHtmlFile = "delete_id_input";
                    break;
                case UPDATE:
                    nameHtmlFile = "customer_update_form";
            }
        try {
            browser.sendRedirectOnPage(req, resp, nameHtmlFile, new Context());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getRequestURI().substring(6).split("/")[0];
        Context context = new Context();
        ApiResponse apiResponse;
        switch (action){
            case "create":
                CustomersDto customer = dtoService.parseRequestToDto(req, CustomersDto.class);
                apiResponse = factory.save(customer);
                break;
            case "delete":
                apiResponse = factory.delete(Long.valueOf(req.getParameter("id")));
                break;
            case "update":
                apiResponse = factory.update(Long.valueOf(req.getParameter("id")),
                        dtoService.parseRequestToDto(req, CustomersDto.class));
                break;
            default:
                resp.sendRedirect(PropertiesLoader.getProperty("crud"));
                return;
        }
        context.setVariable("apiResponse", apiResponse);
        browser.sendRedirectOnPage(req,resp,"api_response", context);
    }
}
