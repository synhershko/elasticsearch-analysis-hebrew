package com.code972.elasticsearch.plugins;

import org.apache.lucene.analysis.hebrew.HebrewQueryLightAnalyzer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import java.io.IOException;

public class HebrewQueryLightAnalyzerProvider extends AbstractIndexAnalyzerProvider<HebrewQueryLightAnalyzer> {
    private final HebrewQueryLightAnalyzer hebrewAnalyzer;

    @Inject
    public HebrewQueryLightAnalyzerProvider(Index index, Settings indexSettings, Environment env, @Assisted String name, @Assisted Settings settings) throws IOException {
        super(index, indexSettings, name, settings);
        hebrewAnalyzer = new HebrewQueryLightAnalyzer(DictReceiver.getDictionary());
    }

    @Override
    public HebrewQueryLightAnalyzer get() {
        return hebrewAnalyzer;
    }
}