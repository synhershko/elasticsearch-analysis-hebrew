package com.code972.elasticsearch.plugins;

import com.code972.hebmorph.DictionaryLoader;
import com.code972.hebmorph.hspell.HSpellDictionaryLoader;
import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;


public class HebMorphAnalysisModule extends AbstractModule {

    protected final ESLogger log = Loggers.getLogger(this.getClass());

    @Override
    protected void configure() {
        try {
            Class hebMorphDictLoader;
            if ((hebMorphDictLoader = Class
                    .forName("com.code972.hebmorph.HebMorphDictionaryLoader")) != null) {
                bind(DictionaryLoader.class).to(hebMorphDictLoader).asEagerSingleton();
                log.info("Auditlog available ({})", hebMorphDictLoader.getSimpleName());
            } else {
                throw new ClassNotFoundException();
            }
        } catch (ClassNotFoundException e) {
            bind(DictionaryLoader.class).to(HSpellDictionaryLoader.class).asEagerSingleton();
            log.info("Using hspell dictionary loader");
        }
    }
}
