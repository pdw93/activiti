package com.ssnail.test;

import com.ssnail.tool.ConnectionPoolTest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据库链接测试
 *
 * @author pengdengwang
 * @since 2019-08-27
 */
public class ConnectTest {
    static ConnectionPoolTest pool = new ConnectionPoolTest(10);
    // 保证所有线程同时开始
    static CountDownLatch start = new CountDownLatch(1);
    // 保证所有线程结束后main才继续运行
    static CountDownLatch end;

    public static void main(String[] args) throws InterruptedException {
        int threadCount = 50;
        int count = 20;
        // 获取到线程的次数
        AtomicInteger got = new AtomicInteger();
        // 未获取到线程的次数
        AtomicInteger noGot = new AtomicInteger();
        end = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(new ConnectionRunner(count, got, noGot), "Thread-" + i);
            thread.start();
        }
        start.countDown();
        end.await();
        System.out.println("total invoke -> " + threadCount * count);
        System.out.println("got count -> " + got.get());
        System.out.println("noGot count -> " + noGot.get());
    }

    static class ConnectionRunner implements Runnable {
        private int count;
        private AtomicInteger got;
        private AtomicInteger noGot;

        public ConnectionRunner(int count, AtomicInteger got, AtomicInteger noGot) {
            this.count = count;
            this.got = got;
            this.noGot = noGot;
        }

        @Override
        public void run() {
            try {
                start.await();
            } catch (InterruptedException e) {

            }
            while (count > 0) {
                try {
                    Connection connection = pool.fetchConnection(500);
                    if (connection != null) {
                        try {
                            connection.createStatement();
                            connection.commit();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } finally {
                            pool.releaseConnection(connection);
                            got.incrementAndGet();
                        }
                    } else {
                        noGot.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    count--;
                }
            }
            end.countDown();
        }
    }

}
