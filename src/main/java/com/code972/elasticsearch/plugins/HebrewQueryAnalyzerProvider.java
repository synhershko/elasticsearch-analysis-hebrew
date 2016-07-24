package com.code972.elasticsearch.plugins;

import org.apache.lucene.analysis.hebrew.HebrewQueryAnalyzer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.elasticsearch.index.settings.IndexSettingsService;

import java.io.IOException;

public class HebrewQueryAnalyzerProvider extends AbstractIndexAnalyzerProvider<HebrewQueryAnalyzer> {
    private final HebrewQueryAnalyzer hebrewAnalyzer;

    @Inject
    public HebrewQueryAnalyzerProvider(Index index, IndexSettingsService indexSettingsService, Environment env, @Assisted String name, @Assisted Settings settings) throws IOException {
        super(index, indexSettingsService.getSettings(), name, settings);
        hebrewAnalyzer = new HebrewQueryAnalyzer(DictReceiver.getDictionary());
        hebrewAnalyzer.setVersion(this.version);
    }

    @Override
    public HebrewQueryAnalyzer get() {
        return hebrewAnalyzer;
    }
}