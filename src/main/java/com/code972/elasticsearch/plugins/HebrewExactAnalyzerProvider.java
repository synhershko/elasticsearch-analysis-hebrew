package com.code972.elasticsearch.plugins;

import org.apache.lucene.analysis.hebrew.HebrewExactAnalyzer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.elasticsearch.index.settings.IndexSettings;

import java.io.IOException;

public class HebrewExactAnalyzerProvider extends AbstractIndexAnalyzerProvider<HebrewExactAnalyzer> {
    private final HebrewExactAnalyzer analyzer;

    @Inject
    public HebrewExactAnalyzerProvider(Index index, @IndexSettings Settings indexSettings, Environment env, @Assisted String name, @Assisted Settings settings) throws IOException {
        super(index, indexSettings, name, settings);
        analyzer = new HebrewExactAnalyzer(DictReceiver.getDictionary());
    }

    @Override
    public HebrewExactAnalyzer get() {
        return analyzer;
    }
}