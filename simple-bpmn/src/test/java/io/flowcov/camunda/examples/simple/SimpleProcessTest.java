package io.flowcov.camunda.examples.simple;

import io.flowcov.camunda.junit.rules.FlowCovProcessEngineRuleBuilder;
import lombok.val;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

@Deployment(resources = {"bpmn/simpleProcess.bpmn", "dmn/simpleDmn.dmn"})
public class SimpleProcessTest {

    @Rule
    @ClassRule
    public static ProcessEngineRule rule = FlowCovProcessEngineRuleBuilder.create().withDetailedCoverageLogging().build();

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

    @Test
    public void test_complete_tsk() {
        val instance = runtimeService().startProcessInstanceByKey(SimpleProcessConstants.PROCESS_KEY);
        assertThat(instance).isWaitingAt(SimpleProcessConstants.USER_TASK_DO_SOMETHING);
        complete(task(), withVariables("testVar", "test"));

    }

}
