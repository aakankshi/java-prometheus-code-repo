package com.example.helloworld;
package com.github.kubernauts.prometheus_example.springboot.instrumented;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;
import io.prometheus.client.spring.boot.EnablePrometheusEndpoint;
import io.prometheus.client.spring.boot.EnableSpringBootMetricsCollector;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
@SpringBootApplication
@RestController
@EnablePrometheusEndpoint
@EnableSpringBootMetricsCollector
public class HelloWorldController {

	@RequestMapping(value = { "/hello" }, method = RequestMethod.GET)
	public String sayHelloWorld() {
		String responseMsg = new StringBuilder("<h1>").append("Hey...Hello World !!").append("</h1>").toString();
		return responseMsg;
	}

	static final Counter requests = Counter.build()
    	.name("requests_total").help("Total number of requests.").register();
	// Define a histogram metric for /prometheus
	static final Histogram requestLatency = Histogram.build()
		.name("requests_latency_seconds").help("Request latency in seconds.").register();
	@RequestMapping("/")
	String home() {
		// Increase the counter metric
		requests.inc();
		// Start the histogram timer
		Histogram.Timer requestTimer = requestLatency.startTimer();
		try {
			return "Hello World!";
		} finally {
			// Stop the histogram timer
			requestTimer.observeDuration();
		}
	}

	@RequestMapping(value = { "/hello/{msg}" }, method = RequestMethod.GET)
	public String sayHelloMsg(@PathVariable String msg) {
		String responseMsg = "Hello World";
		try {
			responseMsg = new StringBuilder("<h1>").append("Hello ").append(msg).append("</h1>").toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseMsg;
	}
}
