package com.cuizhanming.demo.ai.aizmcpserver;

import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;

public class ClientSse {

	public static void main(String[] args) {
		var transport = HttpClientSseClientTransport.builder("http://localhost:8080").build();
		new SampleClient(transport).run();
	}

}