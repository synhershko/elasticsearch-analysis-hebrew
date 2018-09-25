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

import com.code972.hebmorph.datastructures.DictHebMorph;
import org.apache.lucene.analysis.hebrew.HebrewExactAnalyzer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;

public class HebrewExactAnalyzerProvider extends AbstractIndexAnalyzerProvider<HebrewExactAnalyzer> {
    private final HebrewExactAnalyzer analyzer;

    @Inject
    public HebrewExactAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings,
                                       final DictHebMorph dict) {
        super(indexSettings, name, settings);
        analyzer = new HebrewExactAnalyzer(dict);
        analyzer.setVersion(this.version);
    }

    @Override
    public HebrewExactAnalyzer get() {
        return analyzer;
    }
}