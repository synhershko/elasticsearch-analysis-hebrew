package com.code972.elasticsearch.plugins;

import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.index.analysis.AnalyzerProvider;

import java.util.HashMap;
import java.util.Map;

public class HebrewAnalysisBinderProcessor extends AnalysisModule.AnalysisBinderProcessor {

    private final static HashMap<String, Class<? extends AnalyzerProvider>> languageAnalyzers = new HashMap<>();

    static {
        languageAnalyzers.put("hebrew", HebrewIndexingAnalyzerProvider.class);
        languageAnalyzers.put("hebrew_query", HebrewQueryAnalyzerProvider.class);
        languageAnalyzers.put("hebrew_query_light", HebrewQueryLightAnalyzerProvider.class);
        languageAnalyzers.put("hebrew_exact", HebrewExactAnalyzerProvider.class);
    }

    public static boolean analyzerExists(final String analyzerName) {
        return languageAnalyzers.containsKey(analyzerName);
    }

    @Override
    public void processAnalyzers(final AnalyzersBindings analyzersBindings) {
        for (Map.Entry<String, Class<? extends AnalyzerProvider>> entry : languageAnalyzers.entrySet()) {
            analyzersBindings.processAnalyzer(entry.getKey(), entry.getValue());
        }
    }
}
