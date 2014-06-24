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

import java.io.IOException;

import static org.elasticsearch.rest.RestRequest.Method.GET;

/**
 * Created by synhershko on 14/02/14.
 */
public class RestHebrewAnalyzerCheckWordAction extends BaseRestHandler {
    @Inject
    public RestHebrewAnalyzerCheckWordAction(Settings settings, Client client, RestController controller) {
        super(settings, client);
        controller.registerHandler(GET, "/_hebrew/check-word/{word}", this);
    }

    @Override
    public void handleRequest(final RestRequest request, final RestChannel channel) throws IOException {
        final String word = request.param("word");
        final boolean tolerate = request.paramAsBoolean("tolerate", true);
        HebrewAnalyzer.WordType wordType = HebrewAnalyzer.isRecognizedWord(word, tolerate);

        XContentBuilder builder = channel.newBuilder().startObject();
        builder.startObject();
        builder.field("word", word);
        builder.field("wordType", wordType);
        if (wordType != HebrewAnalyzer.WordType.UNRECOGNIZED && wordType != HebrewAnalyzer.WordType.NON_HEBREW) {
            builder.startArray("lemmas");
            Analyzer a = new HebrewQueryLightAnalyzer();
            TokenStream ts = a.tokenStream("foo", word);
            ts.reset();
            while (ts.incrementToken()) {
                CharTermAttribute cta = ts.getAttribute(CharTermAttribute.class);
                builder.value(new String(cta.buffer(), 0, cta.length()));
            }
            ts.close();
            a.close();
            builder.endArray();
        }
        builder.endObject();

        channel.sendResponse(new BytesRestResponse(RestStatus.OK, builder));
    }
}

