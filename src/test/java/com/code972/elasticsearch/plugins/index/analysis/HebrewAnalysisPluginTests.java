package com.code972.elasticsearch.plugins.index.analysis;

import com.code972.elasticsearch.HebrewAnalysisPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.test.ESIntegTestCase;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by synhershko on 10/07/2017.
 */
public class HebrewAnalysisPluginTests extends ESIntegTestCase {
    /**
     * Returns a collection of plugins that should be loaded on each node.
     */
    @Override
    protected Collection<Class<? extends Plugin>> nodePlugins() {
        return Collections.singletonList(HebrewAnalysisPlugin.class);
    }

    public void testPlugin() {
        assert(true);
    }
}
