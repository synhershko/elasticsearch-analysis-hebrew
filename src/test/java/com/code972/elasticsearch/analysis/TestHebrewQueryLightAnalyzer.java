package com.code972.elasticsearch.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.BaseTokenStreamTestCase;

import java.io.IOException;

/**
 * Created by synhershko on 22/06/14.
 */
public class TestHebrewQueryLightAnalyzer extends BaseTokenStreamTestCase {
    public void testBasics() throws IOException {
        Analyzer a = HebrewQueryLightAnalyzer.getHebrewQueryLightAnalyzer();

        assertAnalyzesTo(a, "אימא", new String[]{"אימא"}); // recognized word, lemmatized
        assertAnalyzesTo(a, "אימא$", new String[]{"אימא$"}); // recognized word, lemmatized
        assertAnalyzesTo(a, "בדיקהבדיקה", new String[]{"בדיקהבדיקה"}); // OOV
        assertAnalyzesTo(a, "בדיקהבדיקה$", new String[]{"בדיקהבדיקה$"}); // OOV
        assertAnalyzesTo(a, "ץץץץץץץץץץץ", new String[]{}); // Invalid, treated as noise
        assertAnalyzesTo(a, "ץץץץץץץץץץץ$", new String[]{}); // Invalid, treated as noise

        // Test non-Hebrew
        assertAnalyzesTo(a, "book", new String[]{"book$", "book"});
        assertAnalyzesTo(a, "book$", new String[]{"book$"});
        assertAnalyzesTo(a, "steven's", new String[]{"steven's$", "steven's"});
        assertAnalyzesTo(a, "steven\u2019s", new String[]{"steven's$", "steven's"});
        //assertAnalyzesTo(a, "steven\uFF07s", new String[]{"steven's$", "steven's"});
        checkOneTerm(a, "3", "3");
    }
}
