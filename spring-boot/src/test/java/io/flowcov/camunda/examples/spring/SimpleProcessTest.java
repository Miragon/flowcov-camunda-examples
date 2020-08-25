package io.flowcov.camunda.examples.spring;

import io.flowcov.camunda.junit.rules.FlowCovProcessEngineRuleBuilder;
import lombok.val;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PostConstruct;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;

@RunWith(SpringJUnit4ClassRunner.class)
@Deployment(resources = "bpmn/simpleProcess.bpmn")
@ContextConfiguration(classes = {InMemProcessEngineConfiguration.class})
public class SimpleProcessTest {

    @Autowired
    private ProcessEngine processEngine;

    @Rule
    @ClassRule
    public static ProcessEngineRule rule;

    @PostConstruct
    public void init() {
        if (rule == null) {
            rule = FlowCovProcessEngineRuleBuilder.create(processEngine).handleClassCoverage(true).withDetailedCoverageLogging().build();
        }
    }

    @Test
    public void test_deploy_process() {
        // just deployment
        System.out.println("TEST");
    }

    @Test
    public void test_start_process() {
        val instance = runtimeService().startProcessInstanceByKey(SimpleProcessConstants.PROCESS_KEY);
        assertThat(instance).isWaitingAt(SimpleProcessConstants.USER_TASK_DO_SOMETHING);

    }

}
