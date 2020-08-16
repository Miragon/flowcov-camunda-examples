package io.flowcov.camunda.examples.simple;

import io.flowcov.camunda.junit.rules.TestCoverageProcessEngineRuleBuilder;
import lombok.val;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;

@Deployment(resources = "bpmn/simpleProcess.bpmn")
public class SimpleProcessTest2 {

    @Rule
    @ClassRule
    public static ProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().withDetailedCoverageLogging().build();

    @Before
    public void init() {
        AbstractAssertions.init(rule.getProcessEngine());
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
//
//    @test.Test
//    public void test_start_process() {
//        val instance = runtimeService().startProcessInstanceByKey(SimpleProcessConstants.PROCESS_KEY);
//        assertThat(instance).isWaitingAt(SimpleProcessConstants.USER_TASK_DO_SOMETHING);
//
//    }

}
