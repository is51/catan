package catan.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.PathVariable;
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
                "  background: #fff;\n" +
                "}\n" +
                "\n" +

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
                "    width: 140px;\n" +
                "}\n" +
                "\n" +
                "label:first-child.label-blank {\n" +
                "    display:none;\n" +
                "}\n" +
                "\n" +
                ".block {\n" +
                "    padding-top: 30px;\n" +
                "    border-bottom: 1px solid #ddd;\n" +
                "    padding-bottom: 30px;\n" +
                "}\n" +
                "\n" +
                ".url-rest {\n" +
                "    background-color: #f4f4f4;\n" +
                "    padding: 8px 9px;\n" +
                "    border-radius: 5px;\n" +
                "    margin-bottom: 10px;\n" +
                "    \n" +
                "    \n" +
                "    \n" +
                "}\n" +
                "\n" +
                ".url {\n" +
                "    display: inline-block;\n" +
                "    color: #36c;\n" +
                "}\n" +
                "\n" +
                ".rest {\n" +
                "    display: inline-block;\n" +
                "    background-color: #7b3;\n" +
                "    color: #fff;\n" +
                "    padding: 2px 5px;\n" +
                "    border-radius: 5px;\n" +
                "    margin-right: 10px;\n" +
                "    \n" +
                "}\n" +
                "\n" +
                ".method {\n" +
                "    padding: 0 5px 10px 5px;\n" +
                "}\n" +
                "\n" +
                ".try {\n" +
                "    color: #999;\n" +
                "    padding: 5px;\n" +
                "}\n" +
                "\n" +
                ".try span {\n" +
                "    font-size: 0.8rem;\n" +
                "}\n" +
                "\n" +
                ".response {\n" +
                "    background-color: #f7f7f7;\n" +
                "    border-radius: 5px;\n" +
                "    padding: 1px 15px;\n" +
                "    margin-top: 10px;\n" +
                "    font-size: 0.9rem;\n" +
                "    display: none;\n" +
                "    \n" +
                "    \n" +
                "}\n" +
                "\n" +
                ".response.fail {\n" +
                "    background-color: #fff7f7;\n" +
                "}\n" +
                "\n" +
                ".response.done {\n" +
                "    background-color: #f8fff7;\n" +
                "}\n" +
                "\n" +
                ".response button {\n" +
                "    float: right;\n" +
                "    margin: 10px 10px 0 0;\n" +
                "}\n" +
                "");
        out.append("</style>" + NEWLINE);
        out.append("<script src=\"http://code.jquery.com/jquery-2.1.4.min.js\"></script>" + NEWLINE);
        out.append("<script>\n" +
                "$(function() {\n" +
                "  \n" +
                "  $('body').on('click', 'button', function() {\n" +
                "       $(this).closest('.response').hide().html('')\n" +
                "  });\n" +
                "  $('form').on('submit', function(event) {\n" +
                "      var thisForm = $(this);\n" +
                "      var responseEl = thisForm.children('.response');\n" +
                "      $.ajax({\n" +
                "        url: $(event.target).attr('action'),\n" +
                "        method: 'post',\n" +
                "        data: $(event.target).serializeArray(),\n" +
                "        contentType : \"application/x-www-form-urlencoded\"\n" +
                "      })\n" +
                "       .done(function(data, textStatus, jqXHR) {\n" +
                "           responseEl.removeClass('fail');\n" +
                "           responseEl.addClass('done');\n" +
                "           responseEl.html('<button>X</button><p>' + jqXHR.status + ' ' + jqXHR.statusText + '</p>' + ((jqXHR.responseJSON!==undefined) ? '<p>' + JSON.stringify(jqXHR.responseJSON) + '</p>' : '') )\n" +
                "           responseEl.show();\n" +
                "           \n" +
                "      })\n" +
                "       .fail(function(jqXHR, textStatus, errorThrown) {\n" +
                "           responseEl.removeClass('done');\n" +
                "           responseEl.addClass('fail');\n" +
                "           responseEl.html('<button>X</button><p>' + jqXHR.status + ' ' + jqXHR.statusText + '</p>' + ((jqXHR.responseJSON!==undefined) ? '<p>' + JSON.stringify(jqXHR.responseJSON) + '</p>' : '') )\n" +
                "           responseEl.show();\n" +
                "           \n" +
                "      });\n" +
                "    return false;\n" +
                "  });\n" +
                "});\n" +
                "</script>" + NEWLINE);
        out.append("</head>" + NEWLINE);
        out.append("<body id=\"dt_example\">" + NEWLINE);

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

                out.append("<div class='block'>");
                out.append("<form method=\"" + requestMappingAnnotation.method()[0] + "\" action=\"" + urlPattern + "\">");
                out.append("<div class='url-rest'>");
                out.append("    <span class='rest'>" + requestMappingAnnotation.method()[0] + "</span>");
                out.append("    <span class='url'>" + urlPattern + "</span>");
                out.append("</div>");
                out.append("<div class='method'>Method: " + method.getMethod().getName() + "</div>");
                out.append("<div class='try'>");

                for (MethodParameter parameter : method.getMethodParameters()) {
                    if (parameter.getParameterAnnotation(PathVariable.class) != null) {
                        out.append("<label>_" + parameter.getParameterAnnotation(PathVariable.class).value()
                                + "_:</label><input type=\"text\" name=\""
                                + parameter.getParameterAnnotation(PathVariable.class).value() + "\"> <span>(" + parameter.getParameterType().getName() + ")</span><br/>");
                    }

                    if (parameter.getParameterAnnotation(RequestParam.class) != null) {
                        out.append("<label>" + parameter.getParameterAnnotation(RequestParam.class).value()
                                + ":</label><input type=\"text\" name=\""
                                + parameter.getParameterAnnotation(RequestParam.class).value() + "\"> <span>(" + parameter.getParameterType().getName() + ")</span><br/>");
                    }
                }

                out.append("<label class='label-blank'>&nbsp;</label><input type=\"submit\" value=\"Submit\">");
                out.append("</div>");

                out.append("<div class='response'>");
                out.append("");
                out.append("</div>");

                out.append("</form>");
                out.append("</div>" + NEWLINE);
            }

        }
        out.append("</body></html>");

        return out.toString();
    }


}