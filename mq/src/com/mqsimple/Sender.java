package com.mqsimple;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Sender {
    private static final int SEND_NUMBER = 5;

    public static void main(String[] args) {

        /**
         * 连接工厂 ，JMS 用它创建连接
         */
        ConnectionFactory connectionFactory;
        /**
         * JMS 客户端到 JMS provider 的连接
         */
        Connection connection = null;

        /**
         * 一个发送或接受消息的线程
         */
        Session session;
        /**
         * 消息接受者
         */
        Destination destination;
        /**
         * 消息发送者
         */
        MessageProducer producer;

        connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,ActiveMQConnection.DEFAULT_PASSWORD,"tcp://localhost:61616");
        try {
            //创建连接对象
            connection = connectionFactory.createConnection();
            //启动
            connection.start();
            //获取操作连接
            session = connection.createSession(Boolean.TRUE,Session.AUTO_ACKNOWLEDGE);
            //指定接受者和服务
            destination = session.createQueue("FirstQueue");
            //创建生产者
            producer = session.createProducer(destination);
            //设置不持久
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            sendMessage(session,producer);
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Throwable ingore){

            }
        }
    }

    public static void sendMessage(Session session,MessageProducer producer) throws Exception{
        for (int i=1;i <= SEND_NUMBER;i++) {
            TextMessage message = session.createTextMessage("ActiveMq 发送的消息" + i);
            System.out.println("生产者发送次数：" + i);
            producer.send(message);
        }
    }
}
