package com.code972.elasticsearch.plugins;

import org.apache.lucene.analysis.hebrew.HebrewQueryAnalyzer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;

import java.io.IOException;

public class HebrewQueryAnalyzerProvider extends AbstractIndexAnalyzerProvider<HebrewQueryAnalyzer> {
    private final HebrewQueryAnalyzer hebrewAnalyzer;

    @Inject
    public HebrewQueryAnalyzerProvider(Index index, Settings indexSettings, Environment env, @Assisted String name, @Assisted Settings settings) throws IOException {
        super(index, indexSettings, name, settings);
        hebrewAnalyzer = new HebrewQueryAnalyzer(DictReceiver.getDictionary());
    }

    @Override
    public HebrewQueryAnalyzer get() {
        return hebrewAnalyzer;
    }
}