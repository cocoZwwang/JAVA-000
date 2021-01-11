package pers.cocoadel.learning.activemq.producer;

public class ProducerApplication {
    public static void main(String[] args) {
        ProducerService producerService = new ProducerService();
        producerService.send();
    }
}
