import datetime
from langchain_core.tools import tool

@tool(description="Returns current time")
def get_current_time() -> datetime:
    return datetime.datetime.now()