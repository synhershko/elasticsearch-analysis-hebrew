package com.code972.elasticsearch.plugins.index.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.hebrew.TokenFilters.NiqqudFilter;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;

public class NiqqudFilterTokenFilterFactory extends AbstractTokenFilterFactory {

    @Inject
    public NiqqudFilterTokenFilterFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, name, settings);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new NiqqudFilter(tokenStream);
    }
}