package com.code972.elasticsearch.plugins;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.AbstractIndexComponent;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.TokenFilterFactory;

public abstract class AbstractTokenFilterFactory extends AbstractIndexComponent implements TokenFilterFactory {

    private final String name;


    public AbstractTokenFilterFactory(Index index, Settings indexSettings, String name, Settings settings) {
        super(index, indexSettings);
        this.name = name;
    }

    @Override
    public String name() {
        return this.name;
    }

}
