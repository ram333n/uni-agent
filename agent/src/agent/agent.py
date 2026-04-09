import os

from langchain.agents import create_agent
from langchain_groq import ChatGroq
from langchain_mcp_adapters.client import MultiServerMCPClient

from agent.prompts import SYSTEM_PROMPT
from agent.tools import get_current_time


def create_multi_server_mcp_client():
    return MultiServerMCPClient(
        {
            "faculty": {
                "transport": "http",
                "url": os.environ["FACULTY_MCP_SERVER_URL"],
            }
        }
    )


async def init_agent():
    llm = ChatGroq(
        model="llama-3.3-70b-versatile", # TODO: replace with qwen3.5:9b
        api_key=os.environ["GROQ_API_KEY"],
    )

    mcp_client = create_multi_server_mcp_client()
    mcp_tools = await mcp_client.get_tools() # TODO: add handling MCP init context in agent

    host_tools = [get_current_time]
    all_tools = mcp_tools + host_tools

    print(all_tools)

    agent = create_agent(
        model=llm,
        tools=all_tools,
        system_prompt=SYSTEM_PROMPT
    )

    return agent