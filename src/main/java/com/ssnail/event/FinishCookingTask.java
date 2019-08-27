package com.ssnail.event;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * @author pengdengwang
 * @description 准备食材完成事件
 * @since 2019-08-03
 */
public class FinishCookingTask implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println(delegateTask.getAssignee() + "-完成-" + delegateTask.getName());
    }
}
