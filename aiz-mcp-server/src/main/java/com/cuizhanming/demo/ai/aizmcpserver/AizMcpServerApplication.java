package com.cuizhanming.demo.ai.aizmcpserver;

import com.cuizhanming.demo.ai.aizmcpserver.service.WeatherService;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AizMcpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AizMcpServerApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider weatherTools(WeatherService weatherService) {
        return MethodToolCallbackProvider.builder().toolObjects(weatherService).build();
    }

//    public record TextInput(String input) {
//        return null;
//    }
//
//    @Bean
//    public ToolCallback toUpperCase() {
//        return FunctionToolCallback.builder("toUpperCase", (TextInput input) -> input.input().toUpperCase())
//                .inputType(TextInput.class)
//                .description("Put the text to upper case")
//                .build();
//    }

}
