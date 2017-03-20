package com.code972.elasticsearch.plugins.index.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.hebrew.TokenFilters.AddSuffixTokenFilter;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;

public class AddSuffixTokenFilterFactory extends AbstractTokenFilterFactory {

    private final char suffix;

    @Inject
    public AddSuffixTokenFilterFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, name, settings);
        this.suffix = settings.get("suffix", "$").charAt(0);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new AddSuffixTokenFilter(tokenStream, suffix);
    }
}