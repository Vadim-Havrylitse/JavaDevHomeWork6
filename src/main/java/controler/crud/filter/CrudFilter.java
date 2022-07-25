package controler.crud.filter;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "/crud",
            servletNames = "CrudServlet")
public class CrudFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String parameter = servletRequest.getParameter("select");
        if(parameter == null) {
            filterChain.doFilter(servletRequest, servletResponse);
        }else {
            String[] split = parameter.split("%");
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.sendRedirect("/crud/"
                    + split[0]
                    + "/"
                    + split[1]);
        }
    }

    @Override
    public void destroy() {
    }
}
