package org.prokopchuk.facultymcpserver.controller;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Controller;

@Controller
public class FacultyController {

    @McpTool(description = "Get the weather for a location") //TODO: remove later
    public String getWeather(
            @McpToolParam(description = "A location") String location
    ) {
        return String.format("The weather in %s is shiny and warm", location);
    }

}
