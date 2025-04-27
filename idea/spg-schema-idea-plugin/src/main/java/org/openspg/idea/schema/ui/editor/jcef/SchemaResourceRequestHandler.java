package org.openspg.idea.schema.ui.editor.jcef;

import com.intellij.util.io.HttpRequests;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandler;
import org.cef.handler.CefResourceHandlerAdapter;
import org.cef.handler.CefResourceRequestHandlerAdapter;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SchemaResourceRequestHandler extends CefResourceRequestHandlerAdapter {

    private final List<SchemaResourceSupplier> supplierList = new LinkedList<>();

    public SchemaResourceRequestHandler(SchemaResourceSupplier... suppliers) {
        supplierList.addAll(Arrays.asList(suppliers));
    }

    @Override
    public CefResourceHandler getResourceHandler(CefBrowser browser, CefFrame frame, CefRequest request) {
        try {
            return HttpRequests.request(request.getURL())
                    .throwStatusCodeException(false)
                    .connect(req -> {
                        SchemaResourceSupplier supplier = supplierList
                                .stream()
                                .filter(x -> x.isSupported(request))
                                .findFirst()
                                .orElse(null);

                        if (supplier == null) {
                            return null;
                        }

                        return createResourceHandler(supplier.getResource(request));
                    });

        } catch (IOException io) {
            return null;
        }
    }

    protected CefResourceHandler createResourceHandler(SchemaResourceSupplier.Resource resource) {
        byte[] resourceBytes = resource.getContent();

        return new CefResourceHandlerAdapter() {
            private int position = 0;

            @Override
            public boolean processRequest(CefRequest req, CefCallback callback) {
                callback.Continue();
                return true;
            }

            @Override
            public void getResponseHeaders(CefResponse response, IntRef responseLength, StringRef redirectUrl) {
                response.setMimeType(resource.getContentType());
                responseLength.set(resourceBytes.length);
            }

            @Override
            public boolean readResponse(byte[] dataOut, int bytesToRead, IntRef bytesRead, CefCallback callback) {
                if (resourceBytes.length <= position) {
                    bytesRead.set(0);
                    return false;
                }

                int chunkSize = Math.min(bytesToRead, resourceBytes.length - position);
                System.arraycopy(resourceBytes, position, dataOut, 0, chunkSize);
                position += chunkSize;
                bytesRead.set(chunkSize);
                return true;
            }
        };
    }
}
