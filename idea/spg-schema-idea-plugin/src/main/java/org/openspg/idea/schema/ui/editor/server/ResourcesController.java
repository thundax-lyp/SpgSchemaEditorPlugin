package org.openspg.idea.schema.ui.editor.server;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtilRt;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.io.FileResponses;

import java.io.IOException;
import java.io.InputStream;

public class ResourcesController extends BaseController {

    private static final Logger logger = Logger.getInstance(ResourcesController.class);

    public static final String SERVLET_PATH = "openspg";
    public static final String STATIC_PATH = "/static";

    public ResourcesController(String servicePathPrefix) {
        super(servicePathPrefix);
    }

    @Override
    public @NotNull String getServletPath() {
        return SERVLET_PATH;
    }

    @Override
    public FullHttpResponse get(@NotNull QueryStringDecoder urlDecoder, @NotNull FullHttpRequest request, @NotNull ChannelHandlerContext context) {
        String resourceName = parseResourceName(urlDecoder);
        if (resourceName.isEmpty() || resourceName.equals("/")) {
            resourceName = "/index.html";
        }

        try (InputStream inputStream = PreviewStaticServer.class.getResourceAsStream(STATIC_PATH + resourceName)) {
            if (inputStream == null) {
                return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, Unpooled.EMPTY_BUFFER);
            }

            byte[] data = FileUtilRt.loadBytes(inputStream);

            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(data));
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, FileResponses.INSTANCE.getContentType(resourceName) + "; charset=utf-8");
            response.headers().set(HttpHeaderNames.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            response.headers().set(HttpHeaderNames.ETAG, Long.toString(LAST_MODIFIED));
            return response;

        } catch (IOException e) {
            logger.warn(e.getMessage());
            return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, Unpooled.EMPTY_BUFFER);
        }

    }
}
