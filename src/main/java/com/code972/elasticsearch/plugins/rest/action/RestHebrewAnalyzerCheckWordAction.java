/*
 * HebMorph's elasticsearch-analysis-hebrew
 * Copyright (C) 2010-2017 Itamar Syn-Hershko
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.code972.elasticsearch.plugins.rest.action;

import com.code972.elasticsearch.HebrewAnalysisPlugin;
import com.code972.hebmorph.WordType;
import com.code972.hebmorph.datastructures.DictHebMorph;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.hebrew.HebrewAnalyzer;
import org.apache.lucene.analysis.hebrew.HebrewQueryLightAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.elasticsearch.rest.RestRequest.Method.GET;

/**
 * REST endpoint for getting lemmas for a given word
 */
public class RestHebrewAnalyzerCheckWordAction extends BaseRestHandler {
    @Inject
    public RestHebrewAnalyzerCheckWordAction(Settings settings, RestController controller) {
        super(settings);
        controller.registerHandler(GET, "/_hebrew/check-word/{word}", this);
    }

    @Override
    protected RestChannelConsumer prepareRequest(final RestRequest request, final NodeClient nodeClient) throws IOException {
        final String word = request.param("word");
        final boolean tolerate = request.paramAsBoolean("tolerate", true);

        return channel -> {
            final DictHebMorph dict;
            if ((dict = HebrewAnalysisPlugin.getDictionary()) == null)
                throw new IllegalStateException("Dictionary was not initialized");

            final XContentBuilder builder = channel.newBuilder().startObject();
            final WordType wordType = HebrewAnalyzer.isRecognizedWord(word, tolerate, dict);
            builder.field("word", word);
            builder.field("wordType", wordType);
            if (wordType != WordType.UNRECOGNIZED && wordType != WordType.NON_HEBREW) {
                builder.startArray("lemmas");
                for (String lemma : getLemmas(word, dict)) {
                    builder.value(lemma);
                }
                builder.endArray();
            }
            builder.endObject();
            channel.sendResponse(new BytesRestResponse(RestStatus.OK, builder));
        };
    }

    private List<String> getLemmas(final String word, final DictHebMorph dict) throws IOException {
        final List<String> ret = new ArrayList<>();
        final Analyzer a = new HebrewQueryLightAnalyzer(dict);
        final TokenStream ts = a.tokenStream("foo", word);
        ts.reset();

        while (ts.incrementToken()) {
            CharTermAttribute cta = ts.getAttribute(CharTermAttribute.class);
            ret.add(new String(cta.buffer(), 0, cta.length()));
        }
        ts.close();
        return ret;
    }
}

