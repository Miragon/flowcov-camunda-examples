package io.flowcov.camunda.examples.spring.boot;

import io.flowcov.camunda.junit.ProcessCoverageConfigurator;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.bpm.spring.boot.starter.configuration.Ordering;
import org.camunda.bpm.spring.boot.starter.configuration.impl.AbstractCamundaConfiguration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordering.DEFAULT_ORDER + 1)
public class ProcessEngineConfigPlugin extends AbstractCamundaConfiguration {

    @Override
    public void preInit(SpringProcessEngineConfiguration processEngineConfiguration) {
        ProcessCoverageConfigurator.initializeProcessCoverageExtensions(processEngineConfiguration);
        super.preInit(processEngineConfiguration);
    }
}
