package com.code972.elasticsearch.plugins;

import com.code972.hebmorph.datastructures.DictHebMorph;
import com.code972.hebmorph.datastructures.DictRadix;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.hebrew.HebrewTokenizer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;

public class HebrewTokenizerFactory extends AbstractTokenizerFactory {

    public HebrewTokenizerFactory(Index index, Settings indexSettings, String name, Settings settings) {
        super(index, indexSettings, name, settings);
        this.dict = DictReceiver.getDictionary();
    }

    protected DictHebMorph dict;
    protected DictRadix<Byte> SPECIAL_TOKENIZATION_CASES = null;
    protected final char originalTermSuffix = '$';

    @Override
    public Tokenizer create() {
        HebrewTokenizer tokenizer =  new HebrewTokenizer(dict.getPref(),SPECIAL_TOKENIZATION_CASES);
        tokenizer.setSuffixForExactMatch(originalTermSuffix);
        return tokenizer;
    }
}
