package com.code972.elasticsearch.plugins.index.analysis;

import com.code972.hebmorph.datastructures.DictHebMorph;
import com.code972.hebmorph.datastructures.DictRadix;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.hebrew.HebrewTokenizer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;

public class HebrewTokenizerFactory extends AbstractTokenizerFactory {

    @Inject
    public HebrewTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings,
                                  final DictHebMorph dict) {
        super(indexSettings, name, settings);
        this.dict = dict;
    }

    private DictHebMorph dict;
    private DictRadix<Byte> SPECIAL_TOKENIZATION_CASES = null;
    private final char originalTermSuffix = '$';

    @Override
    public Tokenizer create() {
        HebrewTokenizer tokenizer =  new HebrewTokenizer(dict.getPref(), SPECIAL_TOKENIZATION_CASES);
        tokenizer.setSuffixForExactMatch(originalTermSuffix);
        return tokenizer;
    }
}