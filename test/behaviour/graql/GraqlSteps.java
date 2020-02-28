/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package grakn.client.test.behaviour.graql;

import com.google.common.collect.Iterators;
import grakn.client.GraknClient;
import grakn.client.answer.ConceptMap;
import grakn.client.test.behaviour.connection.ConnectionSteps;
import graql.lang.Graql;
import graql.lang.query.GraqlGet;
import graql.lang.query.GraqlInsert;
import graql.lang.query.GraqlQuery;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class GraqlSteps {

    private static List<ConceptMap> answers;
    private static GraknClient.Session session;
    private static GraknClient.Transaction tx;

    @Before
    public void open_transaction() {
        session = Iterators.getOnlyElement(ConnectionSteps.sessions.iterator());
        tx = session.transaction().write();
    }

    @After
    public void close_transaction() {
        tx.close();
    }

    @Given("the integrity is validated")
    public void check_kb_is_valid() {

        // TODO

    }

    @Given("graql define")
    public void graql_define(List<String> defineQueryStatements) {
        GraqlQuery graqlQuery = Graql.parse(String.join("\n", defineQueryStatements));tx.execute(graqlQuery);
        tx.commit();
        tx = session.transaction().write();
    }

    @Given("graql insert")
    public void graql_insert(List<String> insertQueryStatements) {
        GraqlQuery graqlQuery = Graql.parse(String.join("\n", insertQueryStatements));
        tx.execute(graqlQuery);
        tx.commit();
        tx = session.transaction().write();
    }

    @When("get answers of graql query")
    public void graql_query(List<String> graqlQueryStatements) {
        GraqlQuery graqlQuery = Graql.parse(String.join("\n", graqlQueryStatements));
        if (graqlQuery instanceof GraqlGet) {
            answers = tx.execute(graqlQuery.asGet());
        } else if (graqlQuery instanceof GraqlInsert) {
            answers = tx.execute(graqlQuery.asInsert());
        } else {
            // TODO specialise exception
            throw new RuntimeException("Only match-get and inserted supported for now");
        }
    }

    @Then("the answer size is: {number}")
    public void answer_size_is(int expectedAnswers) {
        assertEquals(expectedAnswers, answers.size());
    }

    @Then("answers have concepts labeled")
    public void answers_satisfy_labels(List<Map<String, String>> conceptLabels) {
        assertEquals(conceptLabels.size(), answers.size());

        for (ConceptMap answer : answers) {

            // convert the concept map into a map from variable to type label
            Map<String, String> answerAsLabels = new HashMap<>();
            answer.map().forEach((var, concept) -> answerAsLabels.put(var.name(), concept.asSchemaConcept().label().toString()));

            int matchingAnswers = 0;
            for (Map<String, String> expectedLabels : conceptLabels) {
                if (expectedLabels.equals(answerAsLabels)) {
                    matchingAnswers++;
                }
            }

            // we expect exactly one matching answer from the expected answer set
            assertEquals(1, matchingAnswers);
        }
    }
}
