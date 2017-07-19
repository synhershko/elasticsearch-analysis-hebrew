/*
 * HebMorph's elasticsearch-analysis-hebrew
 * Copyright (C) 2010-2017 Itamar Syn-Hershko
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

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
