package com.code972.elasticsearch.plugins;

import com.code972.elasticsearch.analysis.HebrewQueryAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.elasticsearch.common.collect.Maps;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.elasticsearch.index.settings.IndexSettings;

import java.io.IOException;
import java.util.Map;

public class HebrewQueryAnalyzerProvider extends AbstractIndexAnalyzerProvider<PerFieldAnalyzerWrapper> {
    private final HebrewQueryAnalyzer hebrewAnalyzer;
    private final PerFieldAnalyzerWrapper perFieldAnalyzerWrapper;

    @Inject
    public HebrewQueryAnalyzerProvider(Index index, @IndexSettings Settings indexSettings, Environment env, @Assisted String name, @Assisted Settings settings) throws IOException {
        super(index, indexSettings, name, settings);
        hebrewAnalyzer = new HebrewQueryAnalyzer();

        final Map<String, Analyzer> analyzerMap = Maps.newHashMap();
        analyzerMap.put("title", hebrewAnalyzer);
        analyzerMap.put("topic", hebrewAnalyzer);
        analyzerMap.put("parent_title", hebrewAnalyzer);
        analyzerMap.put("replies.text", hebrewAnalyzer);
        perFieldAnalyzerWrapper = new PerFieldAnalyzerWrapper(new KeywordAnalyzer(), analyzerMap);
    }

    @Override
    public PerFieldAnalyzerWrapper get() {
        return perFieldAnalyzerWrapper;
    }
}