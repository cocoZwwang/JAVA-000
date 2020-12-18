package pers.cocoadel.learning.http.handler;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;

import java.util.concurrent.TimeUnit;

public class MetricHandler {

    public MetricHandler(HttpChannelPool channelPool) {
        MetricRegistry metricRegistry = new MetricRegistry();
        metricRegistry.register("totalConnectionNumber", (Gauge<Integer>) channelPool::channelCount);
        metricRegistry.register("idleConnectionNumber", (Gauge<Integer>) channelPool::idleChannelCount);

        //控制台展示
        ConsoleReporter consoleReporter = ConsoleReporter.forRegistry(metricRegistry).build();
        //每5秒打印一次
        consoleReporter.start(5, TimeUnit.SECONDS);
        //Jmx展示
        JmxReporter jmxReporter = JmxReporter.forRegistry(metricRegistry).build();
        jmxReporter.start();
    }
}
