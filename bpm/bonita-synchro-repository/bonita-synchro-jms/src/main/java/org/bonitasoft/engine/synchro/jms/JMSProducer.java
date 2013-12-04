package org.bonitasoft.engine.synchro.jms;

import java.io.Serializable;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;

public class JMSProducer {

    public static final String TOPIC_CONNECTION_FACTORY = "bonita/jms/TopicConnectionFactory";

    public static final String TOPIC_NAME = "synchroServiceTopic";

    private final TopicConnectionFactory topicConnectionFactory;

    private final TopicConnection topicConnection;

    private final Session session;

    private final Topic topic;

    private final MessageProducer producer;

    private final long timeout;

    private static JMSProducer jmsProducer;

    private JMSProducer(final long timeout) throws JMSException {
        final String brokerURL = System.getProperty("broker.url");

        // Create a ConnectionFactory
        this.topicConnectionFactory = new ActiveMQConnectionFactory(brokerURL);
        this.topicConnection = topicConnectionFactory.createTopicConnection();
        this.session = topicConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        this.topic = session.createTopic(TOPIC_NAME);

        topicConnection.start();

        this.producer = session.createProducer(topic);

        this.timeout = timeout;
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                try {
                    topicConnection.stop();
                    producer.close();
                    session.close();
                    topicConnection.close();

                } catch (JMSException e) {
                    System.err.println("Cannot stop the sychro service, probably already stopped?");
                }
            }
        });
    }

    public static JMSProducer getInstance(final long messageTimeout) {
        if (jmsProducer == null) {
            try {
                jmsProducer = new JMSProducer(messageTimeout);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return jmsProducer;
    }

    public void sendMessage(final Map<String, Serializable> properties, final String bodyId) throws JMSException {
        final MapMessage message = session.createMapMessage();

        for (final Map.Entry<String, Serializable> property : properties.entrySet()) {
            message.setObjectProperty(property.getKey(), property.getValue());;
        }
        message.setString("body-id", bodyId);
        message.setJMSExpiration(System.currentTimeMillis() + timeout);

        producer.send(message);
        // System.err.println("Message sent= " + message);
    }
}
