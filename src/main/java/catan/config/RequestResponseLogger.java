package catan.config;

import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebFilter("/api/*")
public class RequestResponseLogger implements Filter {
    private Logger logger = LoggerFactory.getLogger(RequestResponseLogger.class);

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest plainRequest, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) plainRequest;
        if (logger.isInfoEnabled()) {
            StringBuilder sbAppender = new StringBuilder();
            request.setAttribute("WEB_EXECUTE_TIME", System.currentTimeMillis());
            sbAppender
                    .append("\n\n=== New Request ==========================================\n")
                    .append("    Request URL : [")
                    .append(request.getRequestURI())
                    .append("]\n")
                    .append("    Client Address : [")
                    .append(request.getRemoteHost())
                    .append("(IP:")
                    .append(request.getRemoteAddr())
                    .append(")")
                    .append("]\n")
                    .append("    Request Method Type : [")
                    .append(request.getMethod())
                    .append("]\n")
                    .append("    Request Encoding : [")
                    .append(request.getCharacterEncoding())
                    .append("]\n");
            if (!ServletFileUpload.isMultipartContent(request)) {
                sbAppender.append("    Request Parameters : [ ");
                Map<String, String[]> parameters = new HashMap<String, String[]>(request.getParameterMap());
                boolean firstParameter = true;
                for (String key : parameters.keySet()) {
                    if (firstParameter) {
                        firstParameter = false;
                    } else {
                        sbAppender.append(", ");
                    }
                    sbAppender
                            .append(key)
                            .append(" = '")
                            .append(parameters.get(key)[0])
                            .append("'");
                }
                sbAppender.append(" ]\n");
            }

            logger.info(sbAppender.toString());
        }

        if (response.getCharacterEncoding() == null) {
            response.setCharacterEncoding("UTF-8");
        }

        HttpServletResponseCopier responseCopier = new HttpServletResponseCopier((HttpServletResponse) response);

        try {
            chain.doFilter(request, responseCopier);
            responseCopier.flushBuffer();
        } finally {
            byte[] copy = responseCopier.getCopy();

            if (logger.isInfoEnabled()) {
                StringBuilder sbAppender = new StringBuilder();
                sbAppender
                        .append("\n\n    Controller Execution time : [")
                        .append(System.currentTimeMillis() - (Long) request.getAttribute("WEB_EXECUTE_TIME")).append(" ms]\n\n")
                        .append("    Response ==========================================\n");
                sbAppender
                        .append("    Status")
                        .append(" : ")
                        .append(responseCopier.getStatus())
                        .append("\n");
                for (String headerName : responseCopier.getHeaderNames()) {
                    sbAppender
                            .append("    ")
                            .append(headerName)
                            .append(" : ")
                            .append(responseCopier.getHeader(headerName))
                            .append("\n");
                }
                sbAppender
                        .append("    Body : ")
                        .append(new String(copy, response.getCharacterEncoding()))
                        .append("\n\n");

                logger.info(sbAppender.toString());
            }
        }
    }


    public class HttpServletResponseCopier extends HttpServletResponseWrapper {

        private ServletOutputStream outputStream;
        private PrintWriter writer;
        private ServletOutputStreamCopier copier;

        public HttpServletResponseCopier(HttpServletResponse response) throws IOException {
            super(response);
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            if (writer != null) {
                throw new IllegalStateException("getWriter() has already been called on this response.");
            }

            if (outputStream == null) {
                outputStream = getResponse().getOutputStream();
                copier = new ServletOutputStreamCopier(outputStream);
            }

            return copier;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            if (outputStream != null) {
                throw new IllegalStateException("getOutputStream() has already been called on this response.");
            }

            if (writer == null) {
                copier = new ServletOutputStreamCopier(getResponse().getOutputStream());
                writer = new PrintWriter(new OutputStreamWriter(copier, getResponse().getCharacterEncoding()), true);
            }

            return writer;
        }

        @Override
        public void flushBuffer() throws IOException {
            if (writer != null) {
                writer.flush();
            } else if (outputStream != null) {
                copier.flush();
            }
        }

        public byte[] getCopy() {
            if (copier != null) {
                return copier.getCopy();
            } else {
                return new byte[0];
            }
        }

    }

    public class ServletOutputStreamCopier extends ServletOutputStream {

        private OutputStream outputStream;
        private ByteArrayOutputStream copy;

        public ServletOutputStreamCopier(OutputStream outputStream) {
            this.outputStream = outputStream;
            this.copy = new ByteArrayOutputStream(1024);
        }

        @Override
        public void write(int b) throws IOException {
            outputStream.write(b);
            copy.write(b);
        }

        public byte[] getCopy() {
            return copy.toByteArray();
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
        }
    }
}
