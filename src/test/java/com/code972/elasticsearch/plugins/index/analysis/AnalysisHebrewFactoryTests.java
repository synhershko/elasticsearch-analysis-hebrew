package com.code972.elasticsearch.plugins.index.analysis;

import org.elasticsearch.AnalysisFactoryTestCase;

import java.util.HashMap;
import java.util.Map;

public class AnalysisHebrewFactoryTests extends AnalysisFactoryTestCase {
    @Override
    protected Map<String, Class<?>> getTokenizers() {
        Map<String, Class<?>> tokenizers = new HashMap<>(super.getTokenizers());
        tokenizers.put("hebrew", HebrewTokenizerFactory.class);
        return tokenizers;
    }

    @Override
    protected Map<String, Class<?>> getTokenFilters() {
        Map<String, Class<?>> filters = new HashMap<>(super.getTokenFilters());
        filters.put("hebrew_lemmatizer", HebrewLemmatizerTokenFilterFactory.class);
        filters.put("niqqud", NiqqudFilterTokenFilterFactory.class);
        filters.put("add_suffix", AddSuffixTokenFilterFactory.class);
        return filters;
    }
}
