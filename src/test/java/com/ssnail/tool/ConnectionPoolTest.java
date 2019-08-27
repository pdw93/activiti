package com.ssnail.tool;

import com.ssnail.tool.ConnectionDriver;
import java.sql.Connection;
import java.util.LinkedList;

/**
 * @author pengdengwang
 * @description 模拟连接池
 * @since 2019-08-27
 */
public class ConnectionPoolTest {
    private LinkedList<Connection> pool = new LinkedList<>();

    public ConnectionPoolTest(int size) {
        for (int i = 0; i < size; i++) {
            Connection connection = ConnectionDriver.createConnect();
            pool.add(connection);
        }
    }

    /**
     * 释放链接
     *
     * @param con
     */
    public void releaseConnection(Connection con) {
        if (con != null) {
            synchronized (pool) {
                pool.addLast(con);
                // 通知其它消费者连接池中归还了一个🔗
                pool.notifyAll();
            }
        }
    }

    /**
     * 获取链接，超时未获取到返回null
     *
     * @param mills 超时时间<=0 可能出现获取不到持续等待的情况
     * @return
     * @throws InterruptedException
     */
    public Connection fetchConnection(long mills) throws InterruptedException {
        synchronized (pool) {
            // 完全超时直接取
            if (mills <= 0) {
                while (pool.isEmpty()) {
                    pool.wait();
                }
                return pool.removeFirst();
            } else {
                long future = System.currentTimeMillis() + mills;
                while (pool.isEmpty() && mills > 0) {
                    pool.wait(mills);
                    mills = future - System.currentTimeMillis();
                }
                Connection result = null;
                if (!pool.isEmpty()) {
                    result = pool.removeFirst();
                }
                return result;
            }
        }
    }

}
