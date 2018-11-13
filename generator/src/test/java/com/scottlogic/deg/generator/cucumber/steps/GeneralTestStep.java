package com.scottlogic.deg.generator.cucumber.steps;

import com.scottlogic.deg.generator.cucumber.utils.DegTestHelper;
import com.scottlogic.deg.generator.cucumber.utils.DegTestState;
import com.scottlogic.deg.generator.cucumber.utils.GeneratorTestUtilities;
import com.scottlogic.deg.generator.generation.GenerationConfig;
import cucumber.api.java.Before;
import cucumber.api.java.en.*;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assert;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;

public class GeneralTestStep {

    private DegTestState state;
    private DegTestHelper testHelper;

    public GeneralTestStep(DegTestState state){
        this.state = state;
    }

    @Before
    public void BeforeEach() {
        this.state.clearState();
        this.testHelper = new DegTestHelper(state);
    }

    @Given("there is a field (.+)$")
    public void thereIsAField(String fieldName) {
        this.state.addField(fieldName);
    }

    @Given("^the following fields exist:$")
    public void thereAreFields(List<String> fields) {
        fields.forEach(this::thereIsAField);
    }

    @When("the generation strategy is {generationStrategy}")
    public void setTheGenerationStrategy(GenerationConfig.DataGenerationType strategy) {
        this.state.generationStrategy = strategy;
    }

    @And("^(.+) is null$")
    public void fieldIsNull(String fieldName) throws Exception{
        this.state.addConstraint(fieldName, "null", null);
    }

    @And("^(.+) is anything but null$")
    public void fieldIsNotNull(String fieldName) throws Exception{
        this.state.addNotConstraint(fieldName, "null", null);
    }

    @But("the profile is invalid as (.+) can't be ([a-z ]+) (((\".*\")|([0-9]+(.[0-9]+){1}))+)")
    public void fieldIsInvalid(String fieldName, String constraint, String value) {
        Object parsedValue;
        if (value.startsWith("\"") && value.endsWith("\"")) {
            parsedValue = value.substring(1, value.length() - 1);
        }  else if (value.contains(".")){
            parsedValue = Double.parseDouble(value);
        } else {
            parsedValue = Integer.parseInt(value);
        }

        try {
            this.state.addConstraint(fieldName, constraint, parsedValue);
            Assert.fail("Expected invalid profile");
        } catch (Exception e) {
            this.state.addException(e);
        }
    }

    @Then("^I am presented with an error message$")
    public void dataGeneratorShouldError() {
        testHelper.generateAndGetData();
        Assert.assertThat(testHelper.generatorHasThrownException(), is(true));
    }

    @And("^no data is created$")
    public void noDataIsCreated() {
        testHelper.generateAndGetData();
        Assert.assertFalse(testHelper.hasDataBeenGenerated());
    }

    @Then("^the following data should be generated:$")
    public void theFollowingDataShouldBeGenerated(List<Map<String, String>> expectedResultsTable) {
        GeneratedTestData data = getExpectedAndGeneratedData(expectedResultsTable);
        Assert.assertThat(data.generatedData, new RowsMatchAnyOrderMatcher(data.expectedData));
    }

    @Then("^the following data should be generated in order:$")
    public void theFollowingDataShouldBeGeneratedInOrder(List<Map<String, String>> expectedResultsTable) {
        GeneratedTestData data = getExpectedAndGeneratedData(expectedResultsTable);
        Assert.assertThat(data.generatedData, contains(data.expectedData));
    }

    @Then("^the following data should be included in what is generated:$")
    public void theFollowingDataShouldBeContainedInActual(List<Map<String, String>> expectedResultsTable) {
        GeneratedTestData data = getExpectedAndGeneratedData(expectedResultsTable);

        Assert.assertThat(data.generatedData, new RowsPresentMatcher(data.expectedData));
    }

    @Then("^the following data should not be included in what is generated:$")
    public void theFollowingDataShouldNotBeContainedInActual(List<Map<String, String>> expectedResultsTable) {
        GeneratedTestData data = getExpectedAndGeneratedData(expectedResultsTable);

        Assert.assertThat(data.generatedData, new RowsAbsentMatcher(data.expectedData));
    }

    private List <List<Object>> getComparableExpectedResults(List<Map<String, String>> expectedResultsTable){
        return expectedResultsTable
            .stream()
            .map(row -> new ArrayList<>(row.values()))
            .map(row -> row.stream().map(GeneratorTestUtilities::parseInput).collect(Collectors.toList()))
            .collect(Collectors.toList());
    }

    private GeneratedTestData getExpectedAndGeneratedData(List<Map<String, String>> expectedResultsTable){
        List <List<Object>> expectedRowsOfResults = getComparableExpectedResults(expectedResultsTable);
        List <List<Object>> data = testHelper.generateAndGetData();
        return new GeneratedTestData(expectedRowsOfResults, data);
    }

    class GeneratedTestData {
        List <List<Object>> expectedData;
        List <List<Object>> generatedData;

        GeneratedTestData(List <List<Object>> expectedData, List <List<Object>> generatedData){
            this.expectedData = expectedData;
            this.generatedData = generatedData;
        }
    }

    class RowsMatchAnyOrderMatcher extends BaseMatcher<List<List<Object>>>{
        private final List<List<Object>> expectedRows;

        public RowsMatchAnyOrderMatcher(List<List<Object>> expectedRows) {
            if (expectedRows == null)
                expectedRows = new ArrayList<>();
            this.expectedRows = expectedRows;
        }

        @Override
        public boolean matches(Object o) {
            List<List<Object>> actualRows = (List<List<Object>>) o;

            if (expectedRows.size() != actualRows.size())
                return false;

            //check that every expected row exists in the actual rows
            Collection<RowMatcher> expectedMatchers = getExpectedMatchers();
            for (List<Object> actualRow : actualRows){
                if (!expectedMatchers.stream().anyMatch(matcher -> matcher.matches(actualRow))){
                    return false;
                }
            }

            return true;
        }

        @Override
        public void describeTo(Description description) {
            Collection<RowMatcher> expectedMatchers = getExpectedMatchers();
            description.appendText(Objects.toString(expectedMatchers));
        }

        private List<RowMatcher> getExpectedMatchers() {
            return expectedRows
                .stream()
                .map(expectedRow -> new RowMatcher(expectedRow))
                .collect(Collectors.toList());
        }
    }

    class RowsPresentMatcher extends BaseMatcher<List<List<Object>>>{
        private final List<List<Object>> expectedRows;

        public RowsPresentMatcher(List<List<Object>> expectedRows) {
            if (expectedRows == null)
                expectedRows = new ArrayList<>();
            this.expectedRows = expectedRows;
        }

        @Override
        public boolean matches(Object o) {
            Collection<RowMatcher> expectedMatchers = getExpectedMatchers();

            for (List<Object> actualRow : (List<List<Object>>) o){
                if (!expectedMatchers.stream().anyMatch(matcher -> matcher.matches(actualRow))){
                    return false;
                }
            }

            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(Objects.toString(getExpectedMatchers()));
        }

        private List<RowMatcher> getExpectedMatchers() {
            return expectedRows
                .stream()
                .map(expectedRow -> new RowMatcher(expectedRow))
                .collect(Collectors.toList());
        }
    }

    class RowsAbsentMatcher extends BaseMatcher<List<List<Object>>>{
        private final List<List<Object>> expectedRows;

        public RowsAbsentMatcher(List<List<Object>> expectedRows) {
            if (expectedRows == null)
                expectedRows = new ArrayList<>();
            this.expectedRows = expectedRows;
        }

        @Override
        public boolean matches(Object o) {
            Collection<RowMatcher> expectedMatches = getExpectedMatchers();

            for (List<Object> actualRow : (List<List<Object>>) o){
                if (expectedMatches.stream().anyMatch(matcher -> matcher.matches(actualRow))){
                    return false;
                }
            }

            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(Objects.toString(getExpectedMatchers()));
        }

        private List<RowMatcher> getExpectedMatchers() {
            return expectedRows
                .stream()
                .map(expectedRow -> new RowMatcher(expectedRow))
                .collect(Collectors.toList());
        }
    }

    class RowMatcher extends BaseMatcher<List<Object>> {
        private final List<Object> expectedRow;

        public RowMatcher(List<Object> expectedRow) {
            this.expectedRow = expectedRow;
        }

        @Override
        public boolean matches(Object o) {
            List<Object> actualRow = (List<Object>) o;

            if (actualRow == null && expectedRow == null)
                return true;

            if (actualRow == null || expectedRow == null)
                return false;

            Iterator<Object> actualRowIterator = actualRow.iterator();
            Iterator<Object> expectedRowIterator = expectedRow.iterator();

            while (actualRowIterator.hasNext()){
                Object actualColumnValue = actualRowIterator.next();

                if (!expectedRowIterator.hasNext())
                    return false; //different lengths

                Object expectedColumnValue = expectedRowIterator.next();

                if (!objectsEquals(actualColumnValue, expectedColumnValue))
                    return false;
            }

            return true;
        }

        private boolean objectsEquals(Object actual, Object expected) {
            if (actual == null && expected == null)
                return true;

            if (actual == null || expected == null)
                return false;

            return actual.equals(expected);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(Objects.toString(this.expectedRow));
        }
    }
}
