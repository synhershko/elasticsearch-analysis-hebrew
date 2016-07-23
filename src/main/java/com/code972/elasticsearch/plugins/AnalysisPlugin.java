package com.code972.elasticsearch.plugins;

import com.code972.elasticsearch.rest.action.RestHebrewAnalyzerCheckWordAction;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestModule;

public class AnalysisPlugin extends Plugin {

    /**
     * Attempts to load a dictionary from paths specified in elasticsearch.yml.
     * If hebrew.dict.path is defined, try loading that.
     */
    public AnalysisPlugin(Settings settings) {
        final String path = settings.get("hebrew.dict.path");
        if (path != null && !path.isEmpty()) {
            DictReceiver.setDictionary(path);
        } else if (DictReceiver.getDictionary() == null) {
            throw new IllegalArgumentException("Could not load any dictionary. Aborting!");
        }
    }

    @Override
    public String name() {
        return "elasticsearch-analysis-hebrew";
    }

    @Override
    public String description() {
        return "Hebrew analyzer powered by HebMorph";
    }

    /* Invoked on component assembly. */
    public void onModule(AnalysisModule analysisModule) {
        analysisModule.addProcessor(new HebrewAnalysisBinderProcessor());
    }

    /* Invoked on component assembly. */
    public void onModule(RestModule restModule) {
        restModule.addRestAction(RestHebrewAnalyzerCheckWordAction.class);
    }
}
