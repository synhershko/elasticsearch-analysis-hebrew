package com.code972.elasticsearch.rest.action;

import com.code972.elasticsearch.analysis.HebrewAnalyzer;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.*;

import java.io.IOException;

import static org.elasticsearch.rest.RestRequest.Method.GET;
import static org.elasticsearch.rest.action.support.RestXContentBuilder.restContentBuilder;

/**
 * Created by user on 14/02/14.
 */
public class RestHebrewAnalyzerCheckWordAction extends BaseRestHandler {
    @Inject
    public RestHebrewAnalyzerCheckWordAction(Settings settings, Client client, RestController controller) {
        super(settings, client);
        controller.registerHandler(GET, "/_hebrew/check-word/{word}", this);
    }

    @Override
    public void handleRequest(final RestRequest request, final RestChannel channel) {
        try {
            final String word = request.param("word");
            final boolean tolerate = request.paramAsBooleanOptional("tolerate", true);
            HebrewAnalyzer.WordType wordType = HebrewAnalyzer.isRecognizedWord(word, tolerate);

            XContentBuilder builder = restContentBuilder(request);
            builder.startObject();
            builder.field("word", word);
            builder.field("isRecognized", wordType != HebrewAnalyzer.WordType.UNRECOGNIZED);
            builder.field("wordType", wordType);
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

