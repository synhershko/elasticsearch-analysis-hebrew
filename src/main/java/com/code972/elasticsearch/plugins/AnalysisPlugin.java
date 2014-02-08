package com.code972.elasticsearch.plugins;

import org.elasticsearch.common.inject.Module;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.plugins.AbstractPlugin;

/**
 * Created with IntelliJ IDEA.
 * User: synhershko
 * Date: 10/25/12
 * Time: 3:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnalysisPlugin extends AbstractPlugin {
    @Override
    public String name() {
        return "elasticsearch-analysis-hebrew";
    }

    @Override
    public String description() {
        return "Hebrew analyzer powered by HebMorph";
    }

    @Override
    public void processModule(Module module) {
        if (module instanceof AnalysisModule) {
            ((AnalysisModule)module).addProcessor(new HebrewAnalysisBinderProcessor());
        }
    }
}
