package com.code972.elasticsearch.plugins;

import org.elasticsearch.index.AbstractIndexComponent;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;

public abstract class AbstractTokenizerFactory extends AbstractIndexComponent implements TokenizerFactory {

    private final String name;

    public AbstractTokenizerFactory(Index index, Settings indexSettings, String name, Settings settings) {
        super(index, indexSettings);
        this.name = name;
    }

    @Override
    public String name() {
        return this.name;
    }

}

