import json
from database import execute_query
from state import AgentState

MAX_RETRIES = 2

FATAL_ERRORS = ["401", "invalid_api_key", "invalid api key", "authentication", "unauthorized"]


def execute_sql_node(state: AgentState) -> dict:
    sql = state.get("sql_query")
    count = state.get("iteration_count", 0) + 1  # always increment

    if not sql:
        error = state.get("error") or "No SQL query to execute."
        return {"error": error, "query_result": None, "iteration_count": count}

    rows, error = execute_query(sql)

    if error:
        return {"error": error, "query_result": None, "iteration_count": count}

    return {
        "query_result": json.dumps(rows, default=str),
        "error": None,
        "iteration_count": count,
    }


def should_retry(state: AgentState) -> str:
    error = state.get("error") or ""
    count = state.get("iteration_count", 0)

    if not error:
        return "continue"

    # Don't retry on API key / auth errors — retrying won't help
    if any(f in error.lower() for f in FATAL_ERRORS):
        return "give_up"

    if count < MAX_RETRIES:
        return "retry"

    return "give_up"
