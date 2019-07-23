package com.ssnail.test;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
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

    @Before
    public void before(){
        processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        repositoryService = processEngine.getRepositoryService();
        taskService = processEngine.getTaskService();
        historyService = processEngine.getHistoryService();
    }

    @Test
    public void buildFirst(){
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("diagram/cooking.zip");
        ZipInputStream zipInputStream = new ZipInputStream(resourceAsStream);
        Deployment deploy = repositoryService.createDeployment().addZipInputStream(zipInputStream).name("做饭流程").deploy();
        System.out.println(deploy.getName()+":"+deploy.getId());
    }
}
