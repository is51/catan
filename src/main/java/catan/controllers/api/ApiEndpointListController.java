package catan.controllers.api;

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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Explores the Java classes looking for annotations
 * indicating REST endpoints. These are written to an HTML table, documenting
 * basic information about all the known endpoints.
 */
@RestController
@RequestMapping("/api")
public class ApiEndpointListController {

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

        out.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">")
                .append("<html>")
                .append("<head>")
                .append("<style type=\"text/css\">")
                .append(getTextFromFile("style.css"))
                .append("</style>")
                .append("<script src=\"http://code.jquery.com/jquery-2.1.4.min.js\"></script>")
                .append("<script>")
                .append(getTextFromFile("script.js"))
                .append("</script>")
                .append("</head>")
                .append("<body id=\"dt_example\">")
                .append("<div class='menu'>")
                .append("    <div>Methods:</div>");

        Map<RequestMappingInfo, HandlerMethod> methodEntries = requestMapping.getHandlerMethods();
        Map<HandlerMethod, RequestMappingInfo> invertedMethodEntries = new HashMap<HandlerMethod, RequestMappingInfo>();
        for (Entry<RequestMappingInfo, HandlerMethod> methodEntry : methodEntries.entrySet()) {
            invertedMethodEntries.put(methodEntry.getValue(), methodEntry.getKey());
        }

        List<HandlerMethod> methods = new ArrayList<HandlerMethod>(methodEntries.values());
        Collections.sort(methods, new Comparator<HandlerMethod>() {
            @Override
            public int compare(HandlerMethod o1, HandlerMethod o2) {
                int result = o1.getMethod().getDeclaringClass().getSimpleName().compareTo(o2.getMethod().getDeclaringClass().getSimpleName());
                if (result != 0) {
                    return result;
                }
                result = o1.getMethod().getName().compareTo(o2.getMethod().getName());

                return result;
            }
        });

        StringBuilder leftMenu = new StringBuilder();
        StringBuilder content = new StringBuilder();

        String currentClass = "";
        for (HandlerMethod method : methods) {
            if (!method.getMethod().getDeclaringClass().getSimpleName().equalsIgnoreCase(currentClass)) {
                String breaks = "";
                if(currentClass.length() > 0){
                    breaks = "<br/>";
                }

                currentClass = method.getMethod().getDeclaringClass().getSimpleName();
                leftMenu.append("<h3><a href='#")
                        .append(currentClass)
                        .append("'>")
                        .append(currentClass)
                        .append(":</a></h3>");

                content.append("<div class='block' id='")
                        .append(currentClass)
                        .append("'>")
                        .append("<div class='method controller'>")
                        .append(breaks)
                        .append("Controller: ")
                        .append(currentClass)
                        .append("</div>")
                        .append("</div>");
            }

            RequestMapping requestMappingAnnotation = method.getMethodAnnotation(RequestMapping.class);

            if (!method.getBeanType().getPackage().getName().contains("catan")
                    || requestMappingAnnotation == null
                    || requestMappingAnnotation.method().length == 0) {
                continue;
            }

            leftMenu.append("<a href='#")
                    .append(method.getMethod().getName())
                    .append("'>")
                    .append(method.getMethod().getName())
                    .append("</a>");


            String urlPattern = invertedMethodEntries.get(method).getPatternsCondition().getPatterns().iterator().next();
            content.append("<div class='block' id='")
                    .append(method.getMethod().getName())
                    .append("'>")
                    .append("<form method=\"")
                    .append(requestMappingAnnotation.method()[0])
                    .append("\" action=\"")
                    .append(urlPattern)
                    .append("\">")
                    .append("<div class='method'>Method: ")
                    .append(method.getMethod().getName())
                    .append("</div>")
                    .append("<div class='url-rest'>")
                    .append("    <span class='rest'>")
                    .append(requestMappingAnnotation.method()[0])
                    .append("</span>")
                    .append("<span class='url'>")
                    .append(urlPattern)
                    .append("</span>")
                    .append("</div>")
                    .append("<div class='try'>");

            for (MethodParameter parameter : method.getMethodParameters()) {
                if (parameter.getParameterAnnotation(PathVariable.class) != null) {
                    content.append("<label>{" + parameter.getParameterAnnotation(PathVariable.class).value()
                            + "}:</label><input type=\"text\" class=\"replaceAction\" name=\""
                            + parameter.getParameterAnnotation(PathVariable.class).value() + "\"> <span>(" + parameter.getParameterType().getName() + ")</span><br/>");
                }

                if (parameter.getParameterAnnotation(RequestParam.class) != null) {
                    content.append("<label>" + parameter.getParameterAnnotation(RequestParam.class).value()
                            + ":</label><input type=\"text\" name=\""
                            + parameter.getParameterAnnotation(RequestParam.class).value() + "\"> <span>(" + parameter.getParameterType().getName() + ")</span><br/>");
                }
            }

            content.append("<label class='label-blank'>&nbsp;</label><input type=\"submit\" value=\"Submit\">");
            content.append("</div>");

            content.append("<div class='response'>");
            content.append("");
            content.append("</div>");

            content.append("</form>");
            content.append("</div>");
        }


        out.append(leftMenu);
        out.append("</div>");

        out.append("<div class='list'>");
        out.append(content);
        out.append("<div style='height: 600px'>&nbsp;</div>");
        out.append("</div>");
        out.append("</body></html>");

        return out.toString();
    }

    private String getTextFromFile(String fileName) throws IOException {
        InputStream is = getClass().getResourceAsStream("/api/" + fileName);
        Reader reader = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(reader);
        String result;

        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.getProperty("line.separator"));
                line = br.readLine();
            }

            result = sb.toString();
        } finally {
            br.close();
        }

        return result;
    }
}