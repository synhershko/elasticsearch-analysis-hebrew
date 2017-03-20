package com.code972.elasticsearch.plugins.index.analysis;

import com.code972.hebmorph.datastructures.DictHebMorph;
import org.apache.lucene.analysis.hebrew.HebrewExactAnalyzer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;

import java.io.IOException;

public class HebrewExactAnalyzerProvider extends AbstractIndexAnalyzerProvider<HebrewExactAnalyzer> {
    private final HebrewExactAnalyzer analyzer;

    @Inject
    public HebrewExactAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings,
                                       final DictHebMorph dict) throws IOException {
        super(indexSettings, name, settings);
        analyzer = new HebrewExactAnalyzer(dict);
        analyzer.setVersion(this.version);
    }

    @Override
    public HebrewExactAnalyzer get() {
        return analyzer;
    }
}