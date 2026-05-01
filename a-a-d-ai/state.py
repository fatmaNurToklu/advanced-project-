from typing import Optional
from typing_extensions import TypedDict


class AgentState(TypedDict):
    question: str
    user_id: str
    session_id: str
    role: str               # CUSTOMER | CORPORATE | ADMIN
    store_id: Optional[str]
    sql_query: Optional[str]
    query_result: Optional[str]
    error: Optional[str]
    final_answer: Optional[str]
    visualization_code: Optional[str]
    is_in_scope: Optional[bool]
    is_greeting: Optional[bool]
    iteration_count: int
