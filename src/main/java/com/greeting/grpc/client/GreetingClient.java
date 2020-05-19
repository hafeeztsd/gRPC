package com.greeting.grpc.client;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.greeting.grpc.server.stub.GreeterGrpc;
import com.greeting.grpc.server.stub.HelloReply;
import com.greeting.grpc.server.stub.HelloRequest;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class GreetingClient {

	private static final Logger logger = Logger.getLogger(GreetingClient.class.getName());

	private final GreeterGrpc.GreeterBlockingStub blockingStub;

	public GreetingClient(Channel channel) {
		blockingStub = GreeterGrpc.newBlockingStub(channel);
	}

	public void greet(String name) {
		logger.info("Will try to greet " + name + " ...");
		HelloRequest request = HelloRequest.newBuilder().setName(name).build();
		HelloReply response;
		try {
			response = blockingStub.sayHello(request);
		} catch (StatusRuntimeException e) {
			logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
			return;
		}
		logger.info("Greeting: " + response.getMessage());
	}

	public static void main(String[] args) throws Exception {
		String user = "world";
		String target = "localhost:50051";
		ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
		try {
			GreetingClient client = new GreetingClient(channel);
			client.greet(user);
		} finally {
			channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
		}
	}
}
