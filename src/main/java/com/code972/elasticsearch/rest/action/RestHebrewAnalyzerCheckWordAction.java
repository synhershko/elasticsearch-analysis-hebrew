package com.code972.elasticsearch.rest.action;

import org.apache.lucene.analysis.hebrew.HebrewQueryLightAnalyzer;
import org.apache.lucene.analysis.hebrew.HebrewAnalyzer;

import com.code972.hebmorph.MorphData;
import com.code972.hebmorph.datastructures.DictHebMorph;
import com.code972.hebmorph.datastructures.DictRadix;
import com.code972.hebmorph.hspell.HSpellLoader;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.elasticsearch.rest.RestRequest.Method.GET;

/**
 * Created by synhershko on 14/02/14.
 */
public class RestHebrewAnalyzerCheckWordAction extends BaseRestHandler {
    HebrewAnalyzer analyzer;

    @Inject
    public RestHebrewAnalyzerCheckWordAction(Settings settings, Client client, RestController controller) throws IOException {
        super(settings, client);
        controller.registerHandler(GET, "/_hebrew/check-word/{word}", this);
        DictRadix<MorphData> radix = new HSpellLoader(new File(HSpellLoader.getHspellPath()), true).loadDictionaryFromHSpellData();
        HashMap<String, Integer> prefs = HSpellLoader.readPrefixesFromFile(HSpellLoader.getHspellPath() + HSpellLoader.PREFIX_NOH);
        analyzer = new HebrewQueryLightAnalyzer(new DictHebMorph(radix,prefs),null); //since this used to used as a static, this makes sense
    }

    @Override
    protected void handleRequest(RestRequest request, RestChannel channel, Client client) throws Exception {
        final String word = request.param("word");
        final boolean tolerate = request.paramAsBoolean("tolerate", true);
        HebrewAnalyzer.WordType wordType = analyzer.isRecognizedWord(word, tolerate);
        XContentBuilder builder = channel.newBuilder().startObject();
        builder.field("word", word);
        builder.field("wordType", wordType);
        if (wordType != HebrewAnalyzer.WordType.UNRECOGNIZED && wordType != HebrewAnalyzer.WordType.NON_HEBREW) {
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
        TokenStream ts = analyzer.tokenStream("foo", word);
        ts.reset();
        while (ts.incrementToken()) {
            CharTermAttribute cta = ts.getAttribute(CharTermAttribute.class);
            ret.add(new String(cta.buffer(), 0, cta.length()));
        }
        ts.close();
        return ret;
    }
}

