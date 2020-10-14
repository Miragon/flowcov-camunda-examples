/*
 * Copyright 2020 FlowSquad GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.flowcov.camunda.examples.dmn;

import io.flowcov.camunda.junit.FlowCovProcessEngineRuleBuilder;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.decisionService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.withVariables;

@Deployment(resources = "dmn/simulation.dmn")
public class DmnTest {

    @Rule
    @ClassRule
    public static ProcessEngineRule rule = FlowCovProcessEngineRuleBuilder.create().build();

    private static final String BEVERGES_KEY = "beverages";

    @Test
    public void deploy_dmn() {
        // just deployment
    }

    @Test
    public void beverages_with_children_in_winter() {
        final boolean children = true;
        final String season = "Winter";

        Integer guestCount = 10;
        List<String> result = evaluateTable(children, season, guestCount);
        assertThat("Apple Juice").isIn(result);
        assertThat("Guiness").isIn(result);

        guestCount = 8;
        result = evaluateTable(children, season, guestCount);
        assertThat("Apple Juice").isIn(result);
        assertThat("Bordeaux").isIn(result);
    }

    private List<String> evaluateTable(boolean children, String season, Integer guestCount) {
        List<String> result = decisionService().evaluateDecisionTableByKey(BEVERGES_KEY,
                withVariables(
                        "guestCount", guestCount,
                        "season", season,
                        "guestsWithChildren", children
                )).collectEntries("beverages");
        return result;
    }
}
