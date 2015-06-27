package catan.controllers;

import com.sun.jersey.api.core.HttpContext;
import org.apache.wink.common.annotations.Parent;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Explores the Java classes in a given package, looking for annotations
 * indicating REST endpoints. These are written to an HTML table, documenting
 * basic information about all the known endpoints.
 *
 */
@Path("/")
public class ApiEndpointListController {

    @GET
    @Path("/")
    @Produces({MediaType.TEXT_HTML})
    public String getApiDetails(@Context HttpContext context) {
        String url = context.getRequest().getAbsolutePath().getPath();
        //String query = context.getRequest().getRequestUri().toASCIIString();


        ApiEndpointListController apiEndpointListController = new ApiEndpointListController();
        try {
            // the root package where Java classes implementing web services
            //  endpoints can be found - the place to start the search from
            String packagename = "catan.controllers";

            List<Endpoint> endpoints = apiEndpointListController.findRESTEndpoints(packagename, url);
            return apiEndpointListController.outputEndpointsTable(endpoints);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return ":(";
    }


    /**
     * Writes the provided REST endpoints to an HTML file.
     */
    public String outputEndpointsTable(List<Endpoint> endpoints) throws IOException {
        StringBuilder out = new StringBuilder();

        out.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">" + NEWLINE);
        out.append("<html>");
        out.append("<head>");
        out.append("<style type=\"text/css\">" + NEWLINE);
        out.append("@import \"http://fonts.googleapis.com/css?family=Montserrat:300,400,700\";\n" +
                ".rwd-table {\n" +
                "  margin: 1em 0;\n" +
                "  min-width: 300px;\n" +
                "}\n" +
                ".rwd-table tr {\n" +
                "  border-top: 1px solid #ddd;\n" +
                "  border-bottom: 1px solid #ddd;\n" +
                "}\n" +
                ".rwd-table th {\n" +
                "  display: none;\n" +
                "}\n" +
                ".rwd-table td {\n" +
                "  display: block;\n" +
                "}\n" +
                ".rwd-table td:last-child {\n" +
                "  padding-bottom: .5em;\n" +
                "}\n" +
                ".rwd-table td:before {\n" +
                "  content: attr(data-th) \": \";\n" +
                "  font-weight: bold;\n" +
                "  width: 6.5em;\n" +
                "  display: inline-block;\n" +
                "}\n" +
                "@media (min-width: 480px) {\n" +
                "  .rwd-table td:before {\n" +
                "    display: none;\n" +
                "  }\n" +
                "}\n" +
                ".rwd-table th, .rwd-table td {\n" +
                "  text-align: left;\n" +
                "}\n" +
                "@media (min-width: 480px) {\n" +
                "  .rwd-table th, .rwd-table td {\n" +
                "    display: table-cell;\n" +
                "    padding: .25em .5em;\n" +
                "  }\n" +
                "  .rwd-table th:last-child, .rwd-table td:last-child {\n" +
                "    padding-right: 0;\n" +
                "  }\n" +
                "}\n" +
                "\n" +
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
        out.append("<script src=“http://code.jquery.com/jquery-2.1.4.min.js”></script>" + NEWLINE);
        out.append("</head>" + NEWLINE);
        out.append("<body id=\"dt_example\">" + NEWLINE);
        out.append("<table class=\"rwd-table\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\" class=\"display\" id=\"endpoints\">" + NEWLINE);
        out.append("<thead><tr><th>URI</th><th>REST</th><th>method</th><th>parameters</th><th>Try it here</th></tr></thead>" + NEWLINE);
        out.append("<tbody>" + NEWLINE);
        String currentJavaClass = "";
        for (Endpoint endpoint : endpoints) {
            if (!currentJavaClass.equals(endpoint.javaClass)) {
                currentJavaClass = endpoint.javaClass;
                out.append("<tr><td colspan=\"5\" style=\"padding: 1em\">" + currentJavaClass + " : </td></tr>");
            }

            switch (endpoint.method) {
                case GET:
                    out.append("<tr class='gradeA'>");
                    break;
                case PUT:
                    out.append("<tr class='gradeC'>");
                    break;
                case POST:
                    out.append("<tr class='gradeU'>");
                    break;
                case DELETE:
                    out.append("<tr class='gradeX'>");
                    break;
                default:
                    out.append("<tr>");
            }

            out.append("<form enctype=\"application/json\" method=\"" + endpoint.method + "\" action=\"" + endpoint.uri + "\">");
            out.append("<td>" + endpoint.uri + "</td>");
            out.append("<td>" + endpoint.method + "</td>");
            out.append("<td>" + endpoint.javaMethodName + "</td>");
            out.append("<td>");

            for (EndpointParameter parameter : endpoint.pathParameters) {
                out.append("path {" + parameter.name + "}  (" + parameter.javaType + ")<br/>");
            }
            for (EndpointParameter parameter : endpoint.queryParameters) {
                out.append("query: " + parameter.name + " (" + parameter.javaType + ") ");
                if (parameter.defaultValue != null) {
                    out.append("default = \"" + parameter.defaultValue + "\"");
                }
                out.append("<br/>");
            }
            out.append("<br/>");

            out.append("<td>");
            for (EndpointParameter parameter : endpoint.pathParameters) {
                out.append("<label>" + parameter.name + ":</label><input type=\"text\" name=\"" + parameter.name + "\"><br/>");
            }
            for (EndpointParameter parameter : endpoint.queryParameters) {
                out.append("<label>" + parameter.name + ":</label><input type=\"text\" name=\"" + parameter.name + "\"><br/>");
            }

            out.append("<input type=\"submit\" value=\"Submit\">");
            out.append("</td>");
            out.append("</form>");
            out.append("</td>");
            out.append("</tr>" + NEWLINE);
        }
        out.append("</tbody>");
        out.append("</table>");
        out.append("</body></html>");

        return out.toString();
    }

    /**
     * Returns REST endpoints defined in Java classes in the specified package.
     */
    @SuppressWarnings("rawtypes")
    public List<Endpoint> findRESTEndpoints(String basepackage, String url) throws IOException, ClassNotFoundException {
        List<Endpoint> endpoints = new ArrayList<Endpoint>();

        List<Class> classes = getClasses(basepackage);

        for (Class<?> clazz : classes) {
            Annotation annotation = clazz.getAnnotation(Path.class);
            if (annotation != null) {

                String basePath = getRESTEndpointPath(clazz);
                basePath = basePath.equals("/") ? url.substring(0, url.length() - 1) : url.substring(0, url.length() - 1) + basePath;
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(GET.class)) {
                        endpoints.add(createEndpoint(method, MethodEnum.GET, clazz, basePath));
                    } else if (method.isAnnotationPresent(PUT.class)) {
                        endpoints.add(createEndpoint(method, MethodEnum.PUT, clazz, basePath));
                    } else if (method.isAnnotationPresent(POST.class)) {
                        endpoints.add(createEndpoint(method, MethodEnum.POST, clazz, basePath));
                    } else if (method.isAnnotationPresent(DELETE.class)) {
                        endpoints.add(createEndpoint(method, MethodEnum.DELETE, clazz, basePath));
                    }
                }
            }
        }

        return endpoints;
    }


    /**
     * Create an endpoint object to represent the REST endpoint defined in the
     * specified Java method.
     */
    private Endpoint createEndpoint(Method javaMethod, MethodEnum restMethod, Class<?> clazz, String classUri) {
        Endpoint newEndpoint = new Endpoint();
        newEndpoint.method = restMethod;
        newEndpoint.javaMethodName = javaMethod.getName();
        newEndpoint.javaClass = clazz.getName();

        Path path = javaMethod.getAnnotation(Path.class);
        if (path != null) {
            newEndpoint.uri = classUri + path.value();
        } else {
            newEndpoint.uri = classUri;
        }
        discoverParameters(javaMethod, newEndpoint);
        return newEndpoint;
    }

    /**
     * Get the parameters for the specified endpoint from the provided java method.
     */
    @SuppressWarnings("rawtypes")
    private void discoverParameters(Method method, Endpoint endpoint) {

        Annotation[][] annotations = method.getParameterAnnotations();
        Class[] parameterTypes = method.getParameterTypes();

        for (int i = 0; i < parameterTypes.length; i++) {
            Class parameter = parameterTypes[i];

            // ignore parameters used to access context
            if ((parameter == Request.class)
                //        || (parameter == javax.servlet.http.HttpServletResponse.class)
                //        || (parameter == javax.servlet.http.HttpServletRequest.class)
                    ) {
                continue;
            }

            EndpointParameter nextParameter = new EndpointParameter();
            nextParameter.javaType = parameter.getName();

            Annotation[] parameterAnnotations = annotations[i];
            for (Annotation annotation : parameterAnnotations) {
                if (annotation instanceof PathParam) {
                    nextParameter.parameterType = ParameterType.PATH;
                    PathParam pathparam = (PathParam) annotation;
                    nextParameter.name = pathparam.value();
                } else if (annotation instanceof QueryParam) {
                    nextParameter.parameterType = ParameterType.QUERY;
                    QueryParam queryparam = (QueryParam) annotation;
                    nextParameter.name = queryparam.value();
                } else if (annotation instanceof FormParam) {
                    nextParameter.parameterType = ParameterType.QUERY;
                    FormParam formParam = (FormParam) annotation;
                    nextParameter.name = formParam.value();
                } else if (annotation instanceof DefaultValue) {
                    DefaultValue defaultvalue = (DefaultValue) annotation;
                    nextParameter.defaultValue = defaultvalue.value();
                }
            }

            switch (nextParameter.parameterType) {
                case PATH:
                    endpoint.pathParameters.add(nextParameter);
                    break;
                case QUERY:
                    endpoint.queryParameters.add(nextParameter);
                    break;
            }
        }
    }

    /**
     * Get the REST endpoint path for the specified class. This involves
     * (recursively) looking for @Parent annotations and getting the path for
     * that class before appending the location in the @Path annotation.
     */
    private String getRESTEndpointPath(Class<?> clazz) {
        String path = "";
        while (clazz != null) {
            Annotation annotation = clazz.getAnnotation(Path.class);
            if (annotation != null) {
                path = ((Path) annotation).value() + path;
            }

            Annotation parent = clazz.getAnnotation(Parent.class);
            if (parent != null) {
                clazz = ((Parent) parent).value();
            } else {
                clazz = null;
            }
        }
        if (path.endsWith("/") == false) {
            path = path + "/";
        }
        return path;
    }


    /**
     * Returns all of the classes in the specified package (including sub-packages).
     */
    @SuppressWarnings("rawtypes")
    private List<Class> getClasses(String pkg) throws IOException, ClassNotFoundException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        // turn package into the folder equivalent
        String path = pkg.replace('.', '/');
        Enumeration<URL> resources = classloader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(getClasses(directory, pkg));
        }
        return classes;
    }

    /**
     * Returns a list of all the classes from the package in the specified
     * directory. Calls itself recursively until no more directories are found.
     */
    @SuppressWarnings("rawtypes")
    private List<Class> getClasses(File dir, String pkg) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!dir.exists()) {
            return classes;
        }
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(getClasses(file, pkg + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(pkg + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }


    //
    // used to store the collection of attributes for a web services endpoint
    //

    static final String NEWLINE = System.getProperty("line.separator");

    enum MethodEnum {PUT, POST, GET, DELETE}

    enum ParameterType {QUERY, PATH, PAYLOAD}

    public class Endpoint {
        String uri;
        MethodEnum method;

        String javaClass;
        String javaMethodName;

        List<EndpointParameter> queryParameters = new ArrayList<ApiEndpointListController.EndpointParameter>();
        List<EndpointParameter> pathParameters = new ArrayList<ApiEndpointListController.EndpointParameter>();
    }

    public class EndpointParameter {
        ParameterType parameterType = ParameterType.PAYLOAD;
        String javaType;
        String defaultValue;
        String name;
    }
}