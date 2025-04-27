package org.openspg.idea.schema.ui.editor.server;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.io.Responses;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class BaseController {

    protected static final long LAST_MODIFIED = System.currentTimeMillis();

    protected final String servletContextPath;

    public BaseController(String servletContextPath) {
        this.servletContextPath = servletContextPath;
    }

    public final void process(
            @NotNull QueryStringDecoder urlDecoder,
            @NotNull FullHttpRequest request,
            @NotNull ChannelHandlerContext context
    ) {
        FullHttpResponse response;
        try {
            if (request.method() == HttpMethod.POST) {
                response = post(urlDecoder, request, context);
            } else if (request.method() == HttpMethod.GET) {
                response = get(urlDecoder, request, context);
            } else if (request.method() == HttpMethod.OPTIONS) {
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
            } else {
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
            }
        } catch (Throwable t) {
            response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.INTERNAL_SERVER_ERROR,
                    Unpooled.wrappedBuffer(t.getMessage().getBytes(StandardCharsets.UTF_8))
            );
        }
        Responses.send(response, context.channel(), request);
        if (response.content() != Unpooled.EMPTY_BUFFER) {
            try {
                response.release();
            } catch (Exception ignore) {
            }
        }
    }

    public FullHttpResponse get(
            @NotNull QueryStringDecoder urlDecoder,
            @NotNull FullHttpRequest request,
            @NotNull ChannelHandlerContext context
    ) {
        throw new IllegalArgumentException("Not implemented");
    }

    public FullHttpResponse post(
            @NotNull QueryStringDecoder urlDecoder,
            @NotNull FullHttpRequest request,
            @NotNull ChannelHandlerContext context
    ) {
        throw new IllegalArgumentException("Not implemented");
    }

    public abstract @NotNull String getServletPath();

    public String getFullServletPath() {
        return servletContextPath + getServletPath();
    }

    protected String parseResourceName(QueryStringDecoder urlDecoder) {
        return urlDecoder.path().substring(this.servletContextPath.length() + getServletPath().length());
    }

    protected String getParameter(@NotNull QueryStringDecoder urlDecoder, @NotNull String parameter) {
        List<String> parameters = urlDecoder.parameters().get(parameter);
        if (parameters == null || parameters.size() != 1) {
            return null;
        }
        return URLDecoder.decode(parameters.get(0), StandardCharsets.UTF_8);
    }

    protected FullHttpResponse createHtmlResponse(String content) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(content.getBytes(StandardCharsets.UTF_8))
        );
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        response.headers().set("Referrer-Policy", "no-referrer");
        return response;
    }

    protected FullHttpResponse createJsonResponse(String content) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(content.getBytes(StandardCharsets.UTF_8))
        );
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        response.headers().set("Referrer-Policy", "no-referrer");
        return response;
    }

    protected Project getProject(String projectNameParameter, String projectUrlParameter) {
        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
            if (projectNameParameter != null && projectNameParameter.equals(project.getName())) {
                return project;
            }
            if (projectUrlParameter != null && projectUrlParameter.equals(project.getPresentableUrl())) {
                return project;
            }
        }
        return null;
    }


}
