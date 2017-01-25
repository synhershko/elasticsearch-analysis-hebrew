package com.code972.elasticsearch.plugins;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.hebrew.TokenFilters.NiqqudFilter;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;

public class NiqqudFilterTokenFilterFactory extends AbstractTokenFilterFactory {


    public NiqqudFilterTokenFilterFactory(Index index, Settings indexSettings, String name, Settings settings) {
        super(index, indexSettings, name, settings);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new NiqqudFilter(tokenStream);
    }
}
