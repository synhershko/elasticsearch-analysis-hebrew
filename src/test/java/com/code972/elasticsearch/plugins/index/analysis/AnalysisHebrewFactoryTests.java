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

import org.elasticsearch.AnalysisFactoryTestCase;

import java.util.HashMap;
import java.util.Map;

public class AnalysisHebrewFactoryTests extends AnalysisFactoryTestCase {
    @Override
    protected Map<String, Class<?>> getTokenizers() {
        Map<String, Class<?>> tokenizers = new HashMap<>(super.getTokenizers());
        tokenizers.put("hebrew", HebrewTokenizerFactory.class);
        return tokenizers;
    }

    @Override
    protected Map<String, Class<?>> getTokenFilters() {
        Map<String, Class<?>> filters = new HashMap<>(super.getTokenFilters());
        filters.put("hebrew_lemmatizer", HebrewLemmatizerTokenFilterFactory.class);
        filters.put("niqqud", NiqqudFilterTokenFilterFactory.class);
        filters.put("add_suffix", AddSuffixTokenFilterFactory.class);
        return filters;
    }
}
