package com.ssnail.test;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * @ClassName ActivitiTest
 * @Description TODO
 * @Author shnstt
 * @Date 2019/7/21 21:30
 * @Version 1.0
 **/
public class ActivitiTest {
    private ProcessEngine processEngine;
    private RepositoryService repositoryService;
    private TaskService taskService;
    private HistoryService historyService;
    private RuntimeService runtimeService;
    private final String processDefinitionKey = "cooking";

    @Before
    public void before() {
        processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        repositoryService = processEngine.getRepositoryService();
        taskService = processEngine.getTaskService();
        historyService = processEngine.getHistoryService();
        runtimeService = processEngine.getRuntimeService();
    }

    /**
     * 单个流程实例挂起与激活
     */
    @Test
    public void processInstanceSuspend() {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId("12501").singleResult();
        boolean suspended = processInstance.isSuspended();
        if (suspended) {
            runtimeService.activateProcessInstanceById("12501");
            System.out.println("流程实例激活，instanceID-12501");
        } else {
            runtimeService.suspendProcessInstanceById("12501");
            System.out.println("流程实例挂起，instance ID-12501");
        }
    }

    /**
     * 流程定义挂起与激活，针对的是流程定义下所有的流程实例
     */
    @Test
    public void processDefinitionSuspend() {
        ProcessDefinition cooking = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).singleResult();
        boolean suspended = cooking.isSuspended();
        if (suspended) {
            repositoryService.activateProcessDefinitionByKey(processDefinitionKey, true, null);
            System.out.println("所有流程实例被激活，流程定义ID-" + cooking.getId());
        } else {
            repositoryService.suspendProcessDefinitionByKey(processDefinitionKey, true, null);
            System.out.println("所有流程实例被挂起，流程定义ID-" + cooking.getId());
        }
    }

    /**
     * 下载流程定义资源
     */
    @Test
    public void downLoadDeploymentResource() throws IOException {
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("cooking").singleResult();
        // 流程bpmn
        String resourceName = definition.getResourceName();
        // 流程图
        String diagramResourceName = definition.getDiagramResourceName();
        InputStream resourceAsStream = repositoryService.getResourceAsStream(definition.getDeploymentId(), resourceName);
        InputStream resourceAsStream1 = repositoryService.getResourceAsStream(definition.getDeploymentId(), diagramResourceName);
        OutputStream outputStream = new FileOutputStream("c:/temp/" + resourceName);
        OutputStream outputStream1 = new FileOutputStream("c:/temp/" + diagramResourceName);
        IOUtils.copy(resourceAsStream, outputStream);
        IOUtils.copy(resourceAsStream1, outputStream1);

    }

    /**
     * 删除流程定义
     */
    @Test
    public void deleteDeployment() {
//        删除流程定义，如果该流程定义已有流程实例启动则删除时出错
//        repositoryService.deleteDeployment("10001");
//        设置true 级联删除流程定义，即使该流程有流程实例启动也可以删除，设 置为false非级别删除方式，如果流程启动删除报错
        repositoryService.deleteDeployment("45001", true);
    }

    // 查询流程定义
    @Test
    public void queryProcessDefinition() {
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        List<ProcessDefinition> cooking = processDefinitionQuery.processDefinitionKey("cooking").orderByProcessDefinitionVersion().desc().list();
        for (ProcessDefinition definition : cooking) {
            System.out.println("流程定义id：" + definition.getId());
            System.out.println("流程定义key：" + definition.getKey());
            System.out.println("流程定义name：" + definition.getName());
            System.out.println("流程定义version：" + definition.getVersion());
            System.out.println("流程部署id：" + definition.getDeploymentId());
        }
    }

    /**
     * 创建流程实例
     */
    @Test
    public void createInstance() {
        ProcessInstance cooking = runtimeService.startProcessInstanceByKey("cooking");
        System.out.println("流程定义id：" + cooking.getProcessDefinitionId());
        System.out.println("流程部署id：" + cooking.getDeploymentId());
        System.out.println("实例id：" + cooking.getId());
        System.out.println("实例名称：" + cooking.getName());
    }

    /**
     * 创建流程实例，带参数
     */
    @Test
    public void createInstance2() {
        Map<String, Object> param = new HashMap<>(3);
        param.put("assignee0", "zhangsan");
        param.put("assignee1", "lisi");
        param.put("assignee2", "wangwu");
        ProcessInstance cooking = runtimeService.startProcessInstanceByKey("cooking2", param);

        System.out.println("流程部署id：" + cooking.getDeploymentId());
        System.out.println("实例id：" + cooking.getId());
        System.out.println("实例名称：" + cooking.getName());

    }

    @Test
    public void buildFirst() {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("diagram/cooking.zip");
        ZipInputStream zipInputStream = new ZipInputStream(resourceAsStream);
        Deployment deploy = repositoryService.createDeployment().addZipInputStream(zipInputStream).name("做饭流程").deploy();
        System.out.println(deploy.getName() + ":" + deploy.getId());
    }

    /**
     * 流程定义
     */
    @Test
    public void processDefinition() {
        InputStream cookingBpmn = getClass().getClassLoader().getResourceAsStream("diagram/cooking.bpmn");
        InputStream cookingPng = getClass().getClassLoader().getResourceAsStream("diagram/cooking.png");

        Deployment deploy = repositoryService.createDeployment().addInputStream("cooking.bpmn", cookingBpmn).addInputStream("cooking.png", cookingPng)
                .deploy();
        System.out.println(deploy.getKey());
    }
}
