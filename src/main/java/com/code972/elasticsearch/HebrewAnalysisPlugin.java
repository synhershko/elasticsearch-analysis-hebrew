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

package com.code972.elasticsearch;


import com.code972.elasticsearch.plugins.index.analysis.AddSuffixTokenFilterFactory;
import com.code972.elasticsearch.plugins.index.analysis.HebrewExactAnalyzerProvider;
import com.code972.elasticsearch.plugins.index.analysis.HebrewIndexingAnalyzerProvider;
import com.code972.elasticsearch.plugins.index.analysis.HebrewLemmatizerTokenFilterFactory;
import com.code972.elasticsearch.plugins.index.analysis.HebrewQueryAnalyzerProvider;
import com.code972.elasticsearch.plugins.index.analysis.HebrewQueryLightAnalyzerProvider;
import com.code972.elasticsearch.plugins.index.analysis.HebrewTokenizerFactory;
import com.code972.elasticsearch.plugins.index.analysis.NiqqudFilterTokenFilterFactory;
import com.code972.elasticsearch.plugins.rest.action.RestHebrewAnalyzerCheckWordAction;
import com.code972.hebmorph.DictionaryLoader;
import com.code972.hebmorph.datastructures.DictHebMorph;
import com.code972.hebmorph.hspell.HSpellDictionaryLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.SpecialPermission;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.common.SuppressForbidden;
import org.elasticsearch.common.settings.ClusterSettings;
import org.elasticsearch.common.settings.IndexScopedSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsFilter;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.analysis.AnalyzerProvider;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.indices.analysis.AnalysisModule;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestHandler;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.Collections.unmodifiableMap;


/**
 * The Hebrew analysis plugin entry point, locating and loading the dictionary and configuring
 * the tokenizer, token filters and analyzers
 */
public final class HebrewAnalysisPlugin extends Plugin implements ActionPlugin, AnalysisPlugin {

    private final Logger log = LogManager.getLogger(this.getClass());

    private final String commercialDictionaryLoaderClass = "com.code972.hebmorph.dictionary.impl.HebMorphDictionaryLoader";

    private static DictHebMorph dict;
    public static DictHebMorph getDictionary() {
        return dict;
    }

    /**
     * Attempts to load a dictionary from paths specified in elasticsearch.yml.
     * If hebrew.dict.path is defined, try loading that first.
     *
     * @param settings settings
     */
    public HebrewAnalysisPlugin(final Settings settings) {
        super();

        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            // unprivileged code such as scripts do not have SpecialPermission
            sm.checkPermission(new SpecialPermission());
        }

        // Figure out which DictionaryLoader class to use for loading the dictionary
        DictionaryLoader dictLoader = (DictionaryLoader) AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            try {
                final Class<?> clz;
                if ((clz = Class.forName(commercialDictionaryLoaderClass)) != null) {
                    log.info("Dictionary loader available ({})", clz.getSimpleName());
                    try {
                        Constructor<?> ctor = Class.forName(commercialDictionaryLoaderClass).getConstructor();
                        return  (DictionaryLoader) ctor.newInstance();
                    } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                        log.error("Unable to load the HebMorph dictionary", e);
                    }
                }
            } catch (ClassNotFoundException ignored) {
                // If external dictionary loaders are not present, we default to the one provided with OSS HebMorph
            }
            return null;
        });

        if (dictLoader == null) {
            log.info("Defaulting to HSpell dictionary loader");
            dictLoader = new HSpellDictionaryLoader();
        }

        // If path was specified in settings, try that path first
        final String pathFromSettings = settings.get("hebrew.dict.path");
        if (pathFromSettings != null && !pathFromSettings.isEmpty()) {
            log.info("Trying to load {} dictionary from path {}", dictLoader.dictionaryLoaderName(), pathFromSettings);
            final DictHebMorph tmp = AccessController.doPrivileged(new LoadDictAction(pathFromSettings, dictLoader));
            if (tmp != null) {
                dict = tmp;
                log.info("Dictionary '{}' loaded successfully from path {}", dictLoader.dictionaryLoaderName(), pathFromSettings);
                return;
            }
        }

        final Environment env = new Environment(settings);
        for (final String path : dictLoader.getPossiblePaths(env.pluginsFile().resolve("analysis-hebrew").toAbsolutePath().toString())) {
            log.info("Trying to load {} from path {}", dictLoader.dictionaryLoaderName(), path);
            final DictHebMorph tmp = AccessController.doPrivileged(new LoadDictAction(path, dictLoader));
            if (tmp != null) {
                dict = tmp;
                log.info("Dictionary '{}' loaded successfully from path {}", dictLoader.dictionaryLoaderName(), path);
                return;
            }
        }

        log.error("Could not load any dictionary. Hebrew analysis plugin is essentially disabled.");
    }

    private class LoadDictAction implements PrivilegedAction<DictHebMorph> {

        private final String path;
        private final DictionaryLoader loader;

        LoadDictAction(final String path, DictionaryLoader dictLoader) {
            this.path = path;
            this.loader = dictLoader;
        }

        @Override
        @SuppressForbidden(reason = "Paths are loaded using Environment so are safe to use")
        public DictHebMorph run() {
            try {
                if (Files.exists(Paths.get(path))) {
                    try {
                        return loader.loadDictionaryFromPath(path);
                    } catch (IOException e) {
                        log.warn(e);
                    }
                }
            } catch (java.security.AccessControlException e) {
                log.warn(e);
            }
            return null;
        }
    }

    @Override
    public List<RestHandler> getRestHandlers(Settings settings, RestController restController,
                                             ClusterSettings clusterSettings, IndexScopedSettings indexScopedSettings,
                                             SettingsFilter settingsFilter,
                                             IndexNameExpressionResolver indexNameExpressionResolver,
                                             Supplier<DiscoveryNodes> nodesInCluster) {
        return singletonList(new RestHebrewAnalyzerCheckWordAction(settings, restController));
    }

    @Override
    public Map<String, AnalysisModule.AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
        final Map<String, AnalysisModule.AnalysisProvider<TokenFilterFactory>> extra = new HashMap<>();
        extra.put("hebrew_lemmatizer", (indexSettings, env, name, settings) ->
                new HebrewLemmatizerTokenFilterFactory(indexSettings, env, name, settings, dict));
        extra.put("niqqud", NiqqudFilterTokenFilterFactory::new);
        extra.put("add_suffix", AddSuffixTokenFilterFactory::new);
        return unmodifiableMap(extra);
    }

    @Override
    public Map<String, AnalysisModule.AnalysisProvider<TokenizerFactory>> getTokenizers() {
        return singletonMap("hebrew", (indexSettings, env, name, settings) ->
                new HebrewTokenizerFactory(indexSettings, env, name, settings, dict));
    }

    @Override
    public Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> getAnalyzers() {
        final Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> extra =
                new HashMap<>();
        extra.put("hebrew", (indexSettings, env, name, settings) ->
                new HebrewIndexingAnalyzerProvider(indexSettings, env, name, settings, dict));
        extra.put("hebrew_query", (indexSettings, env, name, settings) ->
                new HebrewQueryAnalyzerProvider(indexSettings, env, name, settings, dict));
        extra.put("hebrew_query_light", (indexSettings, env, name, settings) ->
                new HebrewQueryLightAnalyzerProvider(indexSettings, env, name, settings, dict));
        extra.put("hebrew_exact", (indexSettings, env, name, settings) ->
                new HebrewExactAnalyzerProvider(indexSettings, env, name, settings, dict));
        return unmodifiableMap(extra);
    }
}
