package com.code972.elasticsearch.plugins;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.hebrew.TokenFilters.HebrewLemmatizerTokenFilter;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.settings.IndexSettingsService;

public class HebrewLemmatizerTokenFilterFactory extends AbstractTokenFilterFactory {

    private boolean LemmatizeExactHebrewWords = true;
    private boolean LemmatizeExactNonHebrewWords = true;

    @Inject
    public HebrewLemmatizerTokenFilterFactory(Index index, IndexSettingsService indexSettingsService, @Assisted String name, @Assisted Settings settings) {
        super(index, indexSettingsService.getSettings(), name, settings);
        LemmatizeExactHebrewWords = settings.getAsBoolean("lemmatize_exact_hebrew_words",false);
        LemmatizeExactNonHebrewWords = settings.getAsBoolean("lemmatize_exact_non_hebrew_words",true);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new HebrewLemmatizerTokenFilter(tokenStream, DictReceiver.getDictionary(),LemmatizeExactHebrewWords,LemmatizeExactNonHebrewWords);
    }
}
