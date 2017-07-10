package com.code972.elasticsearch.plugins.index.analysis;

import com.carrotsearch.randomizedtesting.annotations.Name;
import com.carrotsearch.randomizedtesting.annotations.ParametersFactory;
import org.elasticsearch.test.rest.yaml.ClientYamlTestCandidate;
import org.elasticsearch.test.rest.yaml.ESClientYamlSuiteTestCase;

/**
 * This IT is necessary for the gradle build to pass.
 * Test cases are yaml files residing under test resources.
 */
public class HebrewAnalysisQueryRestIT extends ESClientYamlSuiteTestCase {

    public HebrewAnalysisQueryRestIT(@Name("yaml") ClientYamlTestCandidate testCandidate) {
        super(testCandidate);
    }

    @ParametersFactory
    public static Iterable<Object[]> parameters() throws Exception {
        return ESClientYamlSuiteTestCase.createParameters();
    }
}