package com.code972.elasticsearch.plugins;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.hebrew.TokenFilters.AddSuffixTokenFilter;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.settings.IndexSettingsService;

public class AddSuffixTokenFilterFactory extends AbstractTokenFilterFactory {

    @Inject
    public AddSuffixTokenFilterFactory(Index index, IndexSettingsService indexSettingsService, @Assisted String name, @Assisted Settings settings) {
        super(index, indexSettingsService.getSettings(), name, settings);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new AddSuffixTokenFilter(tokenStream, '$');
    }

}
