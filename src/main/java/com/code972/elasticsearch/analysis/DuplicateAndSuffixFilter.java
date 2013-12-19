package com.code972.elasticsearch.analysis;

import org.apache.lucene.analysis.CommonGramsFilter;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.hebrew.HebrewTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.IOException;

public class DuplicateAndSuffixFilter extends TokenFilter {
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);
    private final PositionIncrementAttribute posIncAtt = addAttribute(PositionIncrementAttribute.class);
    private final KeywordAttribute keywordAtt = addAttribute(KeywordAttribute.class);

    private final Character suffix;

    public DuplicateAndSuffixFilter(final TokenStream input, final Character suffixToAdd) {
        super(input);
        this.suffix = suffixToAdd;
    }

    private char[] tokenBuffer = new char[Byte.MAX_VALUE];
    private int tokenLen = 0;

    @Override
    public final boolean incrementToken() throws IOException {
        if (tokenLen > 0) {
            termAtt.resizeBuffer(tokenLen);
            System.arraycopy(tokenBuffer, 0, termAtt.buffer(), 0, tokenLen);
            termAtt.setLength(tokenLen);
            tokenLen = 0;
            posIncAtt.setPositionIncrement(0); // since we are just putting the original now
            keywordAtt.setKeyword(false);
            return true;
        }

        if (!input.incrementToken()) { // reached EOS -- return null
            return false;
        }

        // If the filter is disabled, skip
        if (suffix == null) {
            return true;
        }

        // Don't suffix special token types returned from
        if (CommonGramsFilter.GRAM_TYPE.equals(typeAtt.type()) ||
                HebrewTokenizer.tokenTypeSignature(HebrewTokenizer.TOKEN_TYPES.Numeric).equals(typeAtt.type()) ||
                HebrewTokenizer.tokenTypeSignature(HebrewTokenizer.TOKEN_TYPES.Mixed).equals(typeAtt.type()) )
        {
            keywordAtt.setKeyword(true);
            return true;
        }

        // keepOrigin
        tokenLen = termAtt.length();
        if (tokenBuffer == null || tokenBuffer.length < tokenLen)
            tokenBuffer = termAtt.buffer().clone();
        else
            System.arraycopy(termAtt.buffer(), 0, tokenBuffer, 0, tokenLen);

        termAtt.append(suffix);
        keywordAtt.setKeyword(true);

        return true;
    }
}
