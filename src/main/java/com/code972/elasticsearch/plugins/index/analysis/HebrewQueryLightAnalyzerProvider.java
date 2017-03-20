package com.code972.elasticsearch.plugins.index.analysis;

import com.code972.hebmorph.datastructures.DictHebMorph;
import org.apache.lucene.analysis.hebrew.HebrewQueryLightAnalyzer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;

import java.io.IOException;

public class HebrewQueryLightAnalyzerProvider extends AbstractIndexAnalyzerProvider<HebrewQueryLightAnalyzer> {
    private final HebrewQueryLightAnalyzer hebrewAnalyzer;

    @Inject
    public HebrewQueryLightAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings,
                                            final DictHebMorph dict) throws IOException {
        super(indexSettings, name, settings);
        hebrewAnalyzer = new HebrewQueryLightAnalyzer(dict);
        hebrewAnalyzer.setVersion(this.version);
    }

    @Override
    public HebrewQueryLightAnalyzer get() {
        return hebrewAnalyzer;
    }
}