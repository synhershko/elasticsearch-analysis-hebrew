package com.code972.elasticsearch.rest.action;

import com.code972.elasticsearch.analysis.HebrewAnalyzer;
import org.elasticsearch.ElasticsearchIllegalArgumentException;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.*;

import java.io.IOException;

import static org.elasticsearch.rest.RestRequest.Method.POST;

/**
 * NOTE: This is a pure hack - it won't work for a multi-node cluster, and won't survive restarts
 * Added it for the sake of making demos more awesome
 * Use at your own risk
 * See https://github.com/elasticsearch/elasticsearch/issues/5124
 */
public class RestHebrewAnalyzerSetCustomDictionaryAction extends BaseRestHandler {
    @Inject
    public RestHebrewAnalyzerSetCustomDictionaryAction(Settings settings, Client client, RestController controller) {
        super(settings, client);
        controller.registerHandler(POST, "/_hebrew/set-custom-dictionary", this);
    }

    @Override
    protected void handleRequest(RestRequest request, RestChannel channel, Client client) throws Exception {
        if (!request.hasContent()) {
            throw new ElasticsearchIllegalArgumentException("Error: please provide a list of words to populate the dictionary with");
        }

        HebrewAnalyzer.setCustomWords(request.content().streamInput());

        XContentBuilder builder = channel.newBuilder().startObject();
        builder.field("status", "ok");
        builder.endObject();

        channel.sendResponse(new BytesRestResponse(RestStatus.OK, builder));
    }
}

