package com.ssnail.test;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.ResponseCache;
import java.util.List;
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

    @Before
    public void before() {
        processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        repositoryService = processEngine.getRepositoryService();
        taskService = processEngine.getTaskService();
        historyService = processEngine.getHistoryService();
        runtimeService = processEngine.getRuntimeService();
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
        OutputStream outputStream = new FileOutputStream("c:/temp/"+resourceName);
        OutputStream outputStream1 = new FileOutputStream("c:/temp/"+diagramResourceName);
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
        repositoryService.deleteDeployment("10001", true);
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
    public void createInstance(){
        ProcessInstance cooking = runtimeService.startProcessInstanceByKey("cooking");
        System.out.println("流程定义id："+cooking.getProcessDefinitionId());
        System.out.println("流程部署id："+cooking.getDeploymentId());
        System.out.println("实例id："+cooking.getId());
        System.out.println("实例名称："+cooking.getName());
    }
    @Test
    public void buildFirst() {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("diagram/cooking.zip");
        ZipInputStream zipInputStream = new ZipInputStream(resourceAsStream);
        Deployment deploy = repositoryService.createDeployment().addZipInputStream(zipInputStream).name("做饭流程").deploy();
        System.out.println(deploy.getName() + ":" + deploy.getId());
    }
}
