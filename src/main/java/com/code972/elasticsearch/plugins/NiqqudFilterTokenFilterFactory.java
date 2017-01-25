package com.code972.elasticsearch.plugins;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.hebrew.TokenFilters.NiqqudFilter;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;

public class NiqqudFilterTokenFilterFactory extends AbstractTokenFilterFactory {

    @Inject
    public NiqqudFilterTokenFilterFactory(Index index, Settings indexSettings,  @Assisted String name,  @Assisted Settings settings) {
        super(index, indexSettings, name, settings);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new NiqqudFilter(tokenStream);
    }
}
