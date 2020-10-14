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

package io.flowcov.camunda.examples.spring.boot;

import io.flowcov.camunda.junit.FlowCovProcessEngineRuleBuilder;
import lombok.val;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PostConstruct;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

@SpringBootTest(classes = {Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@Deployment(resources = {"bpmn/StartWithFlowCovAndSpringBoot.bpmn", "dmn/DecideOnUsage.dmn"})
public class SpringBootProcessTest {

    @Rule
    @ClassRule
    public static ProcessEngineRule rule;

    @Autowired
    private ProcessEngine processEngine;

    private static final String PROCESS_KEY = "StartWithFlowCovAndSpringBoot";
    private static final String TASK_REGISTER = "Task_RegisterOnFlowCov";
    private static final String TASK_INSTALL = "Task_InstallOnPremise";
    private static final String TASK_CREATE_REPOSITORY = "Task_CreateRepository";
    private static final String TASK_START_USING = "Task_StartUsingFlowCov";
    private static final String TASK_DISCUSS_FEEDBACK = "Task_DiscussFeedback";

    private static final String EVENT_FLOWCOV_USED = "EndEvent_FlowCovIsUsed";
    private static final String EVENT_FEEDBACK_PROCESSED = "EndEvent_FeedbackProcessed";

    private static final String MESSAGE_FEEDBACK_RECEIVED = "feedbackReceived";

    @PostConstruct
    public void init() {
        if (rule == null) {
            rule = FlowCovProcessEngineRuleBuilder.create(processEngine).build();
        }
    }

    @Test
    public void deploy_process() {
        // just deployment
    }

    @Test
    public void start_process() {
        val instance = runtimeService().startProcessInstanceByKey(
                PROCESS_KEY,
                withVariables("cloudAccess", true, "openSource", true));
        assertThat(instance).isWaitingAt(TASK_REGISTER);
    }

    @Test
    public void register_and_use() {
        val instance = runtimeService().startProcessInstanceByKey(
                PROCESS_KEY,
                withVariables("cloudAccess", true, "openSource", true));

        assertThat(instance).isWaitingAt(TASK_REGISTER);
        complete(task());

        assertThat(instance).isWaitingAt(TASK_CREATE_REPOSITORY);
        complete(task());

        assertThat(instance).isWaitingAt(TASK_START_USING);
        complete(task());

        assertThat(instance).isEnded().hasPassed(EVENT_FLOWCOV_USED);
    }

    @Test
    public void install_and_use() {
        val instance = runtimeService().startProcessInstanceByKey(
                PROCESS_KEY,
                withVariables("cloudAccess", false, "openSource", false));

        assertThat(instance).isWaitingAt(TASK_INSTALL);
        complete(task());

        assertThat(instance).isWaitingAt(TASK_CREATE_REPOSITORY);
        complete(task());

        assertThat(instance).isWaitingAt(TASK_START_USING);
        complete(task());

        assertThat(instance).isEnded().hasPassed(EVENT_FLOWCOV_USED);
    }

    @Test
    public void register_and_give_feedback() {
        val instance = runtimeService().startProcessInstanceByKey(
                PROCESS_KEY,
                withVariables("cloudAccess", true, "openSource", true));

        assertThat(instance).isWaitingAt(TASK_REGISTER);
        complete(task());

        assertThat(instance).isWaitingAt(TASK_CREATE_REPOSITORY);
        complete(task());

        assertThat(instance).isWaitingAt(TASK_START_USING);

        runtimeService().correlateMessage(MESSAGE_FEEDBACK_RECEIVED);
        assertThat(instance).isWaitingAt(TASK_DISCUSS_FEEDBACK, TASK_START_USING);
        complete(task(TASK_DISCUSS_FEEDBACK));

        assertThat(instance).hasPassed(EVENT_FEEDBACK_PROCESSED);

        complete(task(TASK_START_USING));
        assertThat(instance).isEnded().hasPassed(EVENT_FLOWCOV_USED);

    }

}
