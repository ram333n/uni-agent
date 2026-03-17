package org.prokopchuk.facultymcpserver.controller.mcp;

import lombok.extern.log4j.Log4j2;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Controller;

@Log4j2
@Controller
public class FacultyMcpController {

    @McpTool(
            name = "get_weather",
            description = "Get the weather for a location"
    ) //TODO: remove later
    public String getWeather(
            @McpToolParam(description = "A location") String location
    ) {
        log.info("Invoking 'getWeather' tool. Location: {}", location);

        return String.format("The weather in %s is shiny and warm", location);
    }

}
