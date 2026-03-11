from langchain.agents import create_agent
from langchain_ollama import ChatOllama

from prompts import SYSTEM_PROMPT
from tools import get_current_time


def init_agent():
    llm = ChatOllama(
        model="command-r:35b",
        temperature=0.7
    )

    agent = create_agent(
        model=llm,
        tools=[get_current_time],
        system_prompt=SYSTEM_PROMPT
    )

    return agent