import os

from langchain.agents import create_agent
from langchain_groq import ChatGroq

from prompts import SYSTEM_PROMPT
from tools import get_current_time


def init_agent():
    llm = ChatGroq(
        model="llama-3.3-70b-versatile",
        api_key=os.environ["GROQ_API_KEY"],
    )

    agent = create_agent(
        model=llm,
        tools=[get_current_time],
        system_prompt=SYSTEM_PROMPT
    )

    return agent