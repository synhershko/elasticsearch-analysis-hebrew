package com.code972.elasticsearch.plugins;

import org.elasticsearch.index.analysis.AnalyzerProvider;
import org.elasticsearch.index.analysis.CharFilterFactory;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.indices.analysis.AnalysisModule;
import org.elasticsearch.plugins.*;

import java.util.HashMap;
import java.util.Map;

public class AnalysisHebrewPlugin extends Plugin implements AnalysisPlugin {

    private final static HashMap<String, Class<? extends AnalyzerProvider>> languageAnalyzers = new HashMap<>();

    static {
        languageAnalyzers.put("hebrew", HebrewIndexingAnalyzerProvider.class);
        languageAnalyzers.put("hebrew_query", HebrewQueryAnalyzerProvider.class);
        languageAnalyzers.put("hebrew_query_light", HebrewQueryLightAnalyzerProvider.class);
        languageAnalyzers.put("hebrew_exact", HebrewExactAnalyzerProvider.class);
    }



    @Override
    public Map<String, AnalysisModule.AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
        final Map<String, AnalysisModule.AnalysisProvider<TokenFilterFactory>> extra = new HashMap<>();

        return extra;
    }

//    @Override
//    public Map<String, AnalysisModule.AnalysisProvider<TokenizerFactory>> getTokenizers() {
//        return singletonMap("kuromoji_tokenizer", KuromojiTokenizerFactory::new);
//    }
//
//    @Override
//    public Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> getAnalyzers() {
//        return singletonMap("kuromoji", KuromojiAnalyzerProvider::new);
//    }
}
