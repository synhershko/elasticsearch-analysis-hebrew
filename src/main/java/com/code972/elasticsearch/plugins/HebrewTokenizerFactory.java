package com.code972.elasticsearch.plugins;

import com.code972.hebmorph.datastructures.DictHebMorph;
import com.code972.hebmorph.datastructures.DictRadix;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.hebrew.HebrewTokenizer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;

public class HebrewTokenizerFactory extends AbstractTokenizerFactory {

    @Inject
    public HebrewTokenizerFactory(Index index, Settings indexSettings, @Assisted String name,  @Assisted Settings settings) {
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
