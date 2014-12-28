package com.code972.elasticsearch.rest.action;

import com.code972.elasticsearch.plugins.DictReceiver;
import com.code972.hebmorph.WordType;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.hebrew.HebrewAnalyzer;
import org.apache.lucene.analysis.hebrew.HebrewQueryLightAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.rest.RestRequest.Method.GET;

/**
 * Created by synhershko on 14/02/14.
 */
public class RestHebrewAnalyzerCheckWordAction extends BaseRestHandler {
    @Inject
    public RestHebrewAnalyzerCheckWordAction(Settings settings, Client client, RestController controller) throws IOException {
        super(settings,controller, client);
        controller.registerHandler(GET, "/_hebrew/check-word/{word}", this);
    }

    @Override
    protected void handleRequest(RestRequest request, RestChannel channel, Client client) throws Exception {
        final String word = request.param("word");
        final boolean tolerate = request.paramAsBoolean("tolerate", true);
        WordType wordType = HebrewAnalyzer.isRecognizedWord(word, tolerate, DictReceiver.getDictionary());
        XContentBuilder builder = channel.newBuilder().startObject();
        builder.field("word", word);
        builder.field("wordType", wordType);
        if (wordType != WordType.UNRECOGNIZED && wordType != WordType.NON_HEBREW) {
            builder.startArray("lemmas");
            for (String lemma : getLemmas(word)) {
                builder.value(lemma);
            }
            builder.endArray();
        }
        builder.endObject();
        channel.sendResponse(new BytesRestResponse(RestStatus.OK, builder));
    }

    public List<String> getLemmas(String word) throws IOException {
        List<String> ret = new ArrayList<>();
        Analyzer a = new HebrewQueryLightAnalyzer(DictReceiver.getDictionary());
        TokenStream ts = a.tokenStream("foo", word);
        ts.reset();
        while (ts.incrementToken()) {
            CharTermAttribute cta = ts.getAttribute(CharTermAttribute.class);
            ret.add(new String(cta.buffer(), 0, cta.length()));
        }
        ts.close();
        return ret;
    }
}

