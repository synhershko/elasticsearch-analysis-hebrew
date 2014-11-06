package com.code972.elasticsearch.rest.action;

import com.code972.elasticsearch.analysis.HebrewAnalyzer;
import com.code972.elasticsearch.analysis.HebrewQueryLightAnalyzer;
import com.code972.hebmorph.MorphData;
import com.code972.hebmorph.datastructures.DictHebMorph;
import com.code972.hebmorph.datastructures.DictRadix;
import com.code972.hebmorph.hspell.HSpellLoader;
import org.elasticsearch.ElasticsearchIllegalArgumentException;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static org.elasticsearch.rest.RestRequest.Method.POST;

/**
 * NOTE: This is a pure hack - it won't work for a multi-node cluster, and won't survive restarts
 * Added it for the sake of making demos more awesome
 * Use at your own risk
 * See https://github.com/elasticsearch/elasticsearch/issues/5124
 */
public class RestHebrewAnalyzerSetCustomDictionaryAction extends BaseRestHandler {
    HebrewAnalyzer analyzer;


    @Inject
    public RestHebrewAnalyzerSetCustomDictionaryAction(Settings settings, Client client, RestController controller) throws IOException {
        super(settings, client);
        controller.registerHandler(POST, "/_hebrew/set-custom-dictionary", this);
        DictRadix<MorphData> radix = new HSpellLoader(new File(HSpellLoader.getHspellPath()), true).loadDictionaryFromHSpellData();
        HashMap<String, Integer> prefs = HSpellLoader.readPrefixesFromFile(HSpellLoader.getHspellPath() + HSpellLoader.PREFIX_NOH);
        analyzer = new HebrewQueryLightAnalyzer(new DictHebMorph(radix,prefs),null); //since this used to used as a static, this makes sense
    }

    @Override
    protected void handleRequest(RestRequest request, RestChannel channel, Client client) throws Exception {
        if (!request.hasContent()) {
            throw new ElasticsearchIllegalArgumentException("Error: please provide a list of words to populate the dictionary with");
        }

        analyzer.setCustomWords(request.content().streamInput());

        XContentBuilder builder = channel.newBuilder().startObject();
        builder.field("status", "ok");
        builder.endObject();

        channel.sendResponse(new BytesRestResponse(RestStatus.OK, builder));
    }
}

