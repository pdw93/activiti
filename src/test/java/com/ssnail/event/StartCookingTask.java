package com.ssnail.event;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * @author pengdengwang
 * @description 开始准备食材
 * @since 2019-08-03
 */
public class StartCookingTask implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println(delegateTask.getAssignee() + "-开始-" + delegateTask.getName());
    }
}
