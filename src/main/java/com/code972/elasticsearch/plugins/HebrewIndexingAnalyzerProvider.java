package com.code972.elasticsearch.plugins;

import org.apache.lucene.analysis.hebrew.HebrewIndexingAnalyzer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;

import java.io.IOException;

public class HebrewIndexingAnalyzerProvider extends AbstractIndexAnalyzerProvider<HebrewIndexingAnalyzer> {
    private final HebrewIndexingAnalyzer analyzer;

    @Inject
    public HebrewIndexingAnalyzerProvider(Index index, Settings indexSettings, Environment env, @Assisted String name, @Assisted Settings settings) throws IOException {
        super(index, indexSettings, name, settings);
        analyzer = new HebrewIndexingAnalyzer(DictReceiver.getDictionary());
    }

    @Override
    public HebrewIndexingAnalyzer get() {
        return analyzer;
    }
}