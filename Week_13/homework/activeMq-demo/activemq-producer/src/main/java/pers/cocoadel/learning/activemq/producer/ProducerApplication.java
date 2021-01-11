package pers.cocoadel.learning.activemq.producer;

public class ProducerApplication {
    public static void main(String[] args) {
//        TopicProducerService producerService = new TopicProducerService();
//        producerService.send();

        QueueProducerService queueProducerService = new QueueProducerService();
        queueProducerService.send();
    }
}
