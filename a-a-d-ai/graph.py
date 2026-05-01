from langgraph.graph import StateGraph, END
from state import AgentState
from agents.guardrails import guardrails_node
from agents.sql_agent import sql_agent_node
from agents.error_agent import execute_sql_node, should_retry
from agents.analysis import analysis_node
from agents.visualization import visualization_node


def greeting_node(state: AgentState) -> dict:
    question = state["question"].lower()
    turkish = any(c in question for c in ["ı", "ğ", "ş", "ç", "ö", "ü"])
    if turkish:
        answer = "Merhaba! Ben DataPulse analitik asistanıyım. Siparişleriniz, ürünleriniz, satışlarınız veya mağaza verileriniz hakkında sorular sorabilirsiniz."
    else:
        answer = "Hello! I'm the DataPulse analytics assistant. You can ask me about your orders, products, sales, or store data."
    return {"final_answer": answer, "visualization_code": None}


def out_of_scope_node(state: AgentState) -> dict:
    question = state["question"]
    turkish = any(c in question for c in ["ı", "ğ", "ş", "ç", "ö", "ü"])
    if turkish:
        answer = "Üzgünüm, bu konu DataPulse e-ticaret verileriyle ilgili değil. Sipariş, ürün, satış veya mağaza verileriniz hakkında sorular sorabilirsiniz."
    else:
        answer = "Sorry, that topic is outside my scope. I can only help with DataPulse e-commerce data — orders, products, sales, store analytics, etc."
    return {"final_answer": answer, "visualization_code": None}


def route_after_guardrails(state: AgentState) -> str:
    if state.get("is_greeting"):
        return "greeting"
    if not state.get("is_in_scope"):
        return "out_of_scope"
    return "sql_agent"


def build_graph() -> StateGraph:
    graph = StateGraph(AgentState)

    graph.add_node("guardrails", guardrails_node)
    graph.add_node("greeting", greeting_node)
    graph.add_node("out_of_scope", out_of_scope_node)
    graph.add_node("sql_agent", sql_agent_node)
    graph.add_node("execute_sql", execute_sql_node)
    graph.add_node("analysis", analysis_node)
    graph.add_node("visualization", visualization_node)

    graph.set_entry_point("guardrails")

    graph.add_conditional_edges(
        "guardrails",
        route_after_guardrails,
        {
            "greeting": "greeting",
            "out_of_scope": "out_of_scope",
            "sql_agent": "sql_agent",
        },
    )

    graph.add_edge("greeting", END)
    graph.add_edge("out_of_scope", END)
    graph.add_edge("sql_agent", "execute_sql")

    graph.add_conditional_edges(
        "execute_sql",
        should_retry,
        {
            "retry": "sql_agent",
            "give_up": "analysis",
            "continue": "analysis",
        },
    )

    graph.add_edge("analysis", "visualization")
    graph.add_edge("visualization", END)

    return graph.compile()


compiled_graph = build_graph()
