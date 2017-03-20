package com.code972.elasticsearch.plugins.index.analysis;

import com.code972.hebmorph.datastructures.DictHebMorph;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.hebrew.TokenFilters.HebrewLemmatizerTokenFilter;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;


public class HebrewLemmatizerTokenFilterFactory extends AbstractTokenFilterFactory {

    private final boolean lemmatizeExact;
    private final DictHebMorph dict;

    @Inject
    public HebrewLemmatizerTokenFilterFactory(IndexSettings indexSettings, Environment env, String name, Settings settings,
                                              final DictHebMorph dict) {
        super(indexSettings, name, settings);
        this.dict = dict;
        lemmatizeExact = settings.getAsBoolean("lemmatize_exact",false);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new HebrewLemmatizerTokenFilter(tokenStream, dict, lemmatizeExact, lemmatizeExact);
    }
}