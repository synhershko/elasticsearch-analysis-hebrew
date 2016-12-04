package com.code972.elasticsearch.plugins;

import com.code972.elasticsearch.rest.action.RestHebrewAnalyzerCheckWordAction;
import com.code972.hebmorph.DictionaryLoader;
import com.code972.hebmorph.hspell.HSpellDictionaryLoader;
import com.google.common.reflect.Reflection;
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
        final DictionaryLoader loader;

        // TODO try locating HebMorphDictionaryLoader
        loader = new HSpellDictionaryLoader();

        Class dictLoader;


        // TODO log dictionary loader used

        // If a specific dictionary path was specified, force to use that
        final String path = settings.get("hebrew.dict.path");
        if (path != null && !path.isEmpty()) {
            DictReceiver.setDictionary(loader, path);
        } else {
            // Default to loading from default locations
            DictReceiver.setDictionary(loader);
        }

        if (DictReceiver.getDictionary() == null) {
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
