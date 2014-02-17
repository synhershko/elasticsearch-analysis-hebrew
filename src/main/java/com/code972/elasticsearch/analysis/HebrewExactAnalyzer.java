package com.code972.elasticsearch.analysis;

import org.apache.lucene.analysis.CommonGramsFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.hebrew.HebrewTokenizer;
import org.apache.lucene.analysis.hebrew.NiqqudFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by synhershko on 12/25/13.
 */
public class HebrewExactAnalyzer extends HebrewAnalyzer {
    public HebrewExactAnalyzer() throws IOException {
    }

    @Override
    protected TokenStreamComponents createComponents(final String fieldName, final Reader reader) {
        // on exact - we don't care about suffixes at all, we always output original word with suffix only
        final HebrewTokenizer src = new HebrewTokenizer(reader, prefixesTree, SPECIAL_TOKENIZATION_CASES);
        TokenStream tok = new NiqqudFilter(src);
        tok = new ASCIIFoldingFilter(tok);
        tok = new LowerCaseFilter(matchVersion, tok);
        tok = new AlwaysAddSuffixFilter(tok, '$', false) {
            @Override
            protected boolean possiblySkipFilter() {
                if (CommonGramsFilter.GRAM_TYPE.equals(typeAtt.type()) ||
                        HebrewTokenizer.tokenTypeSignature(HebrewTokenizer.TOKEN_TYPES.Numeric).equals(typeAtt.type()) ||
                        HebrewTokenizer.tokenTypeSignature(HebrewTokenizer.TOKEN_TYPES.Mixed).equals(typeAtt.type()))
                {
                    keywordAtt.setKeyword(true);
                    return true;
                }
                return false;
            }
        };
        return new TokenStreamComponents(src, tok);
    }
}
