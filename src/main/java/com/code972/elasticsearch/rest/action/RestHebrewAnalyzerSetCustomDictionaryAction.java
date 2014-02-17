package com.code972.elasticsearch.rest.action;

import com.code972.elasticsearch.analysis.HebrewAnalyzer;
import com.code972.elasticsearch.analysis.HebrewQueryLightAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import static org.elasticsearch.rest.RestRequest.Method.GET;
import static org.elasticsearch.rest.RestRequest.Method.POST;
import static org.elasticsearch.rest.action.support.RestXContentBuilder.restContentBuilder;

/**
 * NOTE: This is a pure hack - it won't work for a multi-node cluster, and won't survive restarts
 * Added it for the sake of making demos more awesome
 * Use at your own risk
 */
public class RestHebrewAnalyzerSetCustomDictionaryAction extends BaseRestHandler {
    @Inject
    public RestHebrewAnalyzerSetCustomDictionaryAction(Settings settings, Client client, RestController controller) {
        super(settings, client);
        controller.registerHandler(POST, "/_hebrew/set-custom-dictionary", this);
    }

    @Override
    public void handleRequest(final RestRequest request, final RestChannel channel) {
        try {
            HebrewAnalyzer.setCustomWords(new ByteArrayInputStream(request.param("customWords").getBytes("UTF-8")));

            XContentBuilder builder = restContentBuilder(request);
            builder.startObject();
            builder.field("status", "ok");
            builder.endObject();

            channel.sendResponse(new XContentRestResponse(request, RestStatus.OK, builder));
        } catch (Exception e) {
            try {
                channel.sendResponse(new XContentThrowableRestResponse(request, e));
            } catch (IOException e1) {
                logger.error("Failed to send failure response", e1);
            }
        }
    }
}

