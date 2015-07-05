package catan.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.util.Map;

/**
 * Explores the Java classes looking for annotations
 * indicating REST endpoints. These are written to an HTML table, documenting
 * basic information about all the known endpoints.
 */
@RestController
@RequestMapping("/api")
public class ApiEndpointListController {
    static final String NEWLINE = System.getProperty("line.separator");

    @Autowired
    private RequestMappingHandlerMapping requestMapping;

    @RequestMapping(value = "",
            method = RequestMethod.GET,
            produces = org.springframework.http.MediaType.TEXT_HTML_VALUE)
    public String getApiDetails() {
        try {
            return outputEndpointsTable();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ":(";
    }

    /**
     * Writes the provided REST endpoints to an HTML file.
     */
    public String outputEndpointsTable() throws IOException {
        StringBuilder out = new StringBuilder();

        out.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">" + NEWLINE);
        out.append("<html>");
        out.append("<head>");
        out.append("<style type=\"text/css\">" + NEWLINE);
        out.append("@import \"http://fonts.googleapis.com/css?family=Montserrat:300,400,700\";\n" +
                "body {\n" +
                "  padding: 0 2em;\n" +
                "  font-family: Montserrat, sans-serif;\n" +
                "  -webkit-font-smoothing: antialiased;\n" +
                "  text-rendering: optimizeLegibility;\n" +
                "  color: #444;\n" +
                "  background: #eee;\n" +
                "}\n" +
                "\n" +
                "h1 {\n" +
                "  font-weight: normal;\n" +
                "  letter-spacing: -1px;\n" +
                "  color: #34495E;\n" +
                "}\n" +
                "\n" +
                ".rwd-table {\n" +
                "  background: #34495E;\n" +
                "  color: #fff;\n" +
                "  border-radius: .4em;\n" +
                "  overflow: hidden;\n" +
                "}\n" +
                ".rwd-table tr {\n" +
                "  border-color: #46627f;\n" +
                "}\n" +
                ".rwd-table th, .rwd-table td {\n" +
                "  margin: .5em 1em;\n" +
                "}\n" +
                "@media (min-width: 480px) {\n" +
                "  .rwd-table th, .rwd-table td {\n" +
                "    padding-left: 0.5em;\n" +
                "    padding-rigth: 0.5em;\n" +
                "  }\n" +
                "}\n" +
                ".rwd-table th, .rwd-table td:before {\n" +
                "  color: #dd5;\n" +
                "}" +
                "input {\n" +
                "    display:inline-block;\n" +
                "    *display: inline;     /* for IE7*/\n" +
                "    zoom:1;              /* for IE7*/\n" +
                "    vertical-align:middle;\n" +
                "}\n" +
                "\n" +
                "label {\n" +
                "    display:inline-block;\n" +
                "    *display: inline;     /* for IE7*/\n" +
                "    zoom:1;              /* for IE7*/\n" +
                "    float: left;\n" +
                "    text-align: left;\n" +
                "    width: 120px;\n" +
                "}");
        out.append("</style>" + NEWLINE);
        out.append("<script src=\"http://code.jquery.com/jquery-2.1.4.min.js\"></script>" + NEWLINE);
        out.append("</head>" + NEWLINE);
        out.append("<body id=\"dt_example\">" + NEWLINE);
        out.append("<table class=\"rwd-table\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\" class=\"display\" id=\"endpoints\">" + NEWLINE);
        out.append("<thead><tr><th>URI</th><th>REST</th><th>method</th><th>parameters</th><th>Try it here</th></tr></thead>" + NEWLINE);
        out.append("<tbody>" + NEWLINE);

        for (Map.Entry<RequestMappingInfo, HandlerMethod> item : requestMapping.getHandlerMethods().entrySet()) {
            RequestMappingInfo mapping = item.getKey();
            HandlerMethod method = item.getValue();

            for (String urlPattern : mapping.getPatternsCondition().getPatterns()) {
                RequestMapping requestMappingAnnotation = method.getMethodAnnotation(RequestMapping.class);

                if (!method.getBeanType().getPackage().getName().contains("catan")
                        || requestMappingAnnotation == null
                        || requestMappingAnnotation.method().length == 0) {
                    continue;
                }

                out.append("<tr>");
                out.append("<form method=\"" + requestMappingAnnotation.method()[0] + "\" action=\"" + urlPattern + "\">");
                out.append("<td>" + urlPattern + "</td>");
                out.append("<td>" + requestMappingAnnotation.method()[0] + "</td>");
                out.append("<td>" + method.getMethod().getName() + "</td>");
                out.append("<td>");

                for (MethodParameter parameter : method.getMethodParameters()) {
                    if (parameter.getParameterAnnotation(RequestParam.class) != null) {
                        out.append("path {" + parameter.getParameterAnnotation(RequestParam.class).value()
                                + "}  (" + parameter.getParameterType().getName() + ")<br/>");
                    }
                }
                out.append("<br/>");
                out.append("<td>");

                for (MethodParameter parameter : method.getMethodParameters()) {
                    if (parameter.getParameterAnnotation(RequestParam.class) != null) {
                        out.append("<label>" + parameter.getParameterAnnotation(RequestParam.class).value()
                                + ":</label><input type=\"text\" name=\""
                                + parameter.getParameterAnnotation(RequestParam.class).value() + "\"><br/>");
                    }
                }

                out.append("<input type=\"submit\" value=\"Submit\">");
                out.append("</td>");
                out.append("</form>");
                out.append("</td>");
                out.append("</tr>" + NEWLINE);
            }

        }
        out.append("</tbody>");
        out.append("</table>");
        out.append("</body></html>");

        return out.toString();
    }


}