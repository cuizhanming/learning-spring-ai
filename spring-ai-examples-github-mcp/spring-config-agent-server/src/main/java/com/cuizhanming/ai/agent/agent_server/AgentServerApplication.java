package com.cuizhanming.ai.agent.agent_server;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.List;

@SpringBootApplication
public class AgentServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgentServerApplication.class, args);
	}

//	@Bean
//	McpSyncClient mcpClient() {
//		var mcp = McpClient
//				.sync(HttpClientSseClientTransport.builder("http://localhost:8888").build())
//				.build();
//		mcp.initialize();
//		return mcp;
//	}
}

@Controller
@ResponseBody
class AgentController {

	private final Counter alerted, received;
	private final ChatClient ai;
	private final MetricsEndpoint endpoint;

	AgentController(MeterRegistry meterRegistry,
                    ChatClient.Builder ai,
					//McpSyncClient mcpClient,
					ToolCallbackProvider githubMcp,
                    Environment environment,
					MetricsEndpoint endpoint) {
		this.alerted = Counter.builder("agent.alerted").register(meterRegistry);
		this.alerted.increment(1);
		this.received = Counter.builder("agent.received").register(meterRegistry);
		this.received.increment(2000);
        this.endpoint = endpoint;
		var threshold = environment.getProperty("agent.alert-threshold", Float.class);
		System.out.printf("Alert threshold: %f%n", threshold);

		this.ai = ai
				//.defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpClient))
				.defaultToolCallbacks(githubMcp)
				.defaultTools(this)
				.build();
	}

	@GetMapping("/chat")
	String chat(@RequestParam String message) {
		return this.ai.prompt(message).call().content();
	}

	@Tool(description = "return all the metrics names for the agent-server application")
	Collection<String> metricsNames() {
		return endpoint.listNames().getNames();
	}

	@Tool(description = "return the value of a metric for the agent-server application")
	Collection<MetricsEndpoint.Sample> metricsValues(@ToolParam(description = "the name of the metrics") String metricsName) {
		return endpoint.metric(metricsName, List.of()).getMeasurements();
	}

}

