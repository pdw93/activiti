package com.ssnail;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pengdengwang
 * @description 测试
 * @since 2019-08-03
 */
public class ActivitiTest {
    public static void main(String[] args) {
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        Map<String, Object> param = new HashMap<>(3);
        param.put("assignee0", "zhangsan");
        param.put("assignee1", "lisi");
        param.put("assignee2", "wangwu");
        ProcessInstance cooking = runtimeService.startProcessInstanceByKey("cooking2", param);

        System.out.println("流程部署id：" + cooking.getDeploymentId());
        System.out.println("实例id：" + cooking.getId());
        System.out.println("实例名称：" + cooking.getName());
    }
}
