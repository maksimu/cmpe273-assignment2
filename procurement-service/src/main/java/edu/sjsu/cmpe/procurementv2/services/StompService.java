package edu.sjsu.cmpe.procurementv2.services;


import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsQueue;
import org.fusesource.stomp.jms.StompJmsTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.jms.*;

/**
 * User: maksim
 * Date: 3/31/14 - 10:35 PM
 */
@Service
@EnableConfigurationProperties
public class StompService {

    private final Logger log = LoggerFactory.getLogger(getClass());


    @Value("${apolloHost}")
    String stompHost;
    @Value("${apolloPort}")
    String stompPort;
    @Value("${apolloUser}")
    String stompAdmin;
    @Value("${apolloPassword}")
    String stompPassw;
    @Value("${stompQueueName}")
    String stompQueueName;

    Session session;
    Destination dest;


    public MessageConsumer getMessageConsumer() throws JMSException {
        init();
        MessageConsumer consumer = session.createConsumer(dest);
        return consumer;
    }

    public MessageProducer getMessageProducer() throws JMSException {
        init();
        MessageProducer producer = session.createProducer(dest);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
        return producer;
    }


    /**
     *
     * @param queueName Ex. 53730.book.orders
     * @param textMessageBody Text to send
     * @return
     * @throws JMSException
     */
    public boolean sendMessageToQueue(String queueName, String textMessageBody) throws JMSException {
        init();
        log.info(String.format("Sending message: [%s] to [%s:%s@%s:%s%s]", textMessageBody, stompAdmin, stompPassw, stompHost, stompPort, stompQueueName));
        MessageProducer producer = session.createProducer(new StompJmsQueue("/queue/", queueName));
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
        TextMessage textMessage = session.createTextMessage(textMessageBody);
        producer.send(textMessage);
        log.info("Finish sending message: [" + textMessageBody + "]");

        return true;
    }

    public String getMessageFromQueue(long waitUntil) throws JMSException {
        StompJmsQueue dest = new StompJmsQueue("/queue/", stompQueueName);

        StompJmsConnectionFactory stompJmsConnectionFactory = new StompJmsConnectionFactory();
        stompJmsConnectionFactory.setBrokerURI("tcp://" + stompHost + ":" + stompPort);
        Connection connection = stompJmsConnectionFactory.createConnection(stompAdmin, stompPassw);
        connection.start();


        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(dest);

        Message msg = consumer.receive(waitUntil);

        String body = null;

        if(msg != null) {

            if (msg instanceof TextMessage) {
                body = ((TextMessage) msg).getText();
                log.info("Got message [" + body + "]");
            } else {
                throw new JMSException("This message is not a TextMessage");
            }

        }

        consumer.close();
        session.close();
        connection.stop();
        connection.close();

        return body;
    }


    /**
     *
     * @param topicName Ex. 53730.book.computer
     * @param textMessageBody
     * @return
     * @throws JMSException
     */
    public boolean sendMessageToTopic(String topicName, String textMessageBody) throws JMSException {
        StompJmsConnectionFactory stompJmsConnectionFactory = new StompJmsConnectionFactory();
        stompJmsConnectionFactory.setBrokerURI("tcp://" + stompHost + ":" + stompPort);
        Connection connection = stompJmsConnectionFactory.createConnection(stompAdmin, stompPassw);
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        log.info(String.format("Sending message: [%s] to [%s:%s@%s:%s/topic/%s]", textMessageBody, stompAdmin, stompPassw, stompHost, stompPort, topicName));



        MessageProducer producer = session.createProducer(new StompJmsTopic("/topic/", topicName));
        producer.setTimeToLive(300000);
        TextMessage textMessage = session.createTextMessage(textMessageBody);
        producer.send(textMessage, DeliveryMode.PERSISTENT, 1, 300000);

        log.info("Finish sending message: [" + textMessageBody + "]");

        producer.close();
        session.close();
        connection.stop();
        connection.close();
        return true;
    }



    public boolean sendMessage(String textMessageBody) throws JMSException {
        init();
        log.info(String.format("Sending message: [%s] to [%s:%s@%s:%s%s]", textMessageBody, stompAdmin, stompPassw, stompHost, stompPort, stompQueueName));
        TextMessage textMessage = session.createTextMessage(textMessageBody);
        getMessageProducer().send(textMessage);
        log.info("Finish sending message: [" + textMessageBody + "]");

        return true;
    }

    public String getMessage() throws JMSException {
        init();
        log.info(String.format("Getting message: [%s:%s@%s:%s%s]", stompAdmin, stompPassw, stompHost, stompPort, stompQueueName));

        MessageConsumer messageConsumer = getMessageConsumer();
        Message msg = messageConsumer.receiveNoWait();

        if (msg == null) {
            log.info("No messages");
            return null;
        } else {
            log.info("-----> Got message: [" + msg + "] <-----");
        }

        String body;
        if (msg instanceof TextMessage) {
            body = ((TextMessage) msg).getText();
        } else {
            throw new JMSException("This message is not a TextMessage");
        }

        return body;
    }

    private void init() throws JMSException {
        if (session == null || dest == null) {
            StompJmsConnectionFactory stompJmsConnectionFactory = new StompJmsConnectionFactory();
            stompJmsConnectionFactory.setBrokerURI("tcp://" + stompHost + ":" + stompPort);
            Connection connection = stompJmsConnectionFactory.createConnection(stompAdmin, stompPassw);
            connection.start();


            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            dest = new StompJmsQueue("/queue/", "53730.book.orders");
        }
    }

}
