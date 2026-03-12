import chainlit as cl
from agent.agent import init_agent
from langchain_core.messages import HumanMessage, AIMessageChunk


@cl.on_chat_start
async def on_chat_start():
    agent = await init_agent()
    cl.user_session.set("agent", agent)
    cl.user_session.set("history", [])


@cl.on_message
async def on_message(message: cl.Message):
    agent = cl.user_session.get("agent")
    history: list = cl.user_session.get("history")

    history.append(HumanMessage(content=message.content))

    response_message = cl.Message(content="")
    await response_message.send()

    full_response = ""

    async for event in agent.astream_events(
        {"messages": history},
        version="v2",
    ):
        kind = event["event"]
        if kind == "on_chat_model_stream":
            chunk: AIMessageChunk = event["data"]["chunk"]
            token = chunk.content
            if token:
                full_response += token
                await response_message.stream_token(token)

    await response_message.update()

    history.append({"role": "assistant", "content": full_response})
    cl.user_session.set("history", history)
