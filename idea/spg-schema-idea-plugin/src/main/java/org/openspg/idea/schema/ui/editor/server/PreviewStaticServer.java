package org.openspg.idea.schema.ui.editor.server;

import com.intellij.openapi.diagnostic.Logger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.ide.HttpRequestHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PreviewStaticServer extends HttpRequestHandler {

    private static final Logger logger = Logger.getInstance(PreviewStaticServer.class);

    public static final String SERVLET_CONTEXT_PATH = "/";

    private static final Set<HttpMethod> SUPPORTED_METHODS = Set.of(
            HttpMethod.GET, HttpMethod.POST, HttpMethod.HEAD
    );

    private final Map<String, BaseController> routes = new HashMap<>();

    public PreviewStaticServer() {
        registerController(new ResourcesController(SERVLET_CONTEXT_PATH));
    }

    @Override
    public boolean isAccessible(@NotNull HttpRequest request) {
        return true;
    }

    @Override
    public boolean isSupported(@NotNull FullHttpRequest request) {
        return request.uri().startsWith(SERVLET_CONTEXT_PATH) && SUPPORTED_METHODS.contains(request.method());
    }

    @Override
    public boolean process(@NotNull QueryStringDecoder urlDecoder, @NotNull FullHttpRequest request, @NotNull ChannelHandlerContext context) throws IOException {
        String path = urlDecoder.path();
        logger.info("Processing path: " + path);

        if (!path.startsWith(SERVLET_CONTEXT_PATH)) {
            logger.warn("Invalid path: " + path);
            throw new IOException("prefix should have been checked by #isSupported");
        }

        for (String fullServletPath : routes.keySet()) {
            if (path.startsWith(fullServletPath)) {
                routes.get(fullServletPath).process(urlDecoder, request, context);
                return true;
            }
        }

        logger.warn("Unsupported path: " + path);
        return false;
    }


    private void registerController(BaseController controller) {
        routes.put(controller.getFullServletPath(), controller);
    }

    public static PreviewStaticServer getInstance() {
        return HttpRequestHandler.Companion.getEP_NAME().findExtension(PreviewStaticServer.class);
    }

}
