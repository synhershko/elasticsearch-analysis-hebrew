package com.code972.elasticsearch.plugins.index.analysis;

import com.code972.hebmorph.datastructures.DictHebMorph;
import org.apache.lucene.analysis.hebrew.HebrewIndexingAnalyzer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;

import java.io.IOException;

public class HebrewIndexingAnalyzerProvider extends AbstractIndexAnalyzerProvider<HebrewIndexingAnalyzer> {
    private final HebrewIndexingAnalyzer analyzer;

    @Inject
    public HebrewIndexingAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings,
                                          final DictHebMorph dict) throws IOException {
        super(indexSettings, name, settings);
        analyzer = new HebrewIndexingAnalyzer(dict);
        analyzer.setVersion(this.version);
    }

    @Override
    public HebrewIndexingAnalyzer get() {
        return analyzer;
    }
}