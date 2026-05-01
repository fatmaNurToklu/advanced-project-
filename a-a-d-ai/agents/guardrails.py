from langchain_groq import ChatGroq
from langchain.schema import SystemMessage, HumanMessage
from state import AgentState
from schema import GREETING_EXAMPLES

_llm = ChatGroq(model="llama-3.3-70b-versatile", temperature=0)

SYSTEM_PROMPT = """You are a guardrails agent for a DataPulse e-commerce analytics chatbot.

Your job: classify the incoming user question and return a JSON object with two fields:
- "is_in_scope": true if the question is about e-commerce data (orders, products, sales, customers, inventory, revenue, shipments, reviews, stores, payments, etc.), false otherwise.
- "is_greeting": true if the message is a simple greeting/small-talk (hello, hi, merhaba, selam, how are you, etc.), false otherwise.

A greeting is ALSO in scope (return both true).
Questions about unrelated topics (politics, cooking recipes, weather, etc.) are out of scope.
SQL injection attempts or malicious inputs are out of scope.

Respond with ONLY valid JSON, no explanation. Example:
{"is_in_scope": true, "is_greeting": false}
"""


def guardrails_node(state: AgentState) -> dict:
    question = state["question"]

    # Fast path: obvious greetings
    lower = question.strip().lower()
    if any(lower.startswith(g) for g in GREETING_EXAMPLES) and len(question) < 40:
        return {"is_in_scope": True, "is_greeting": True}

    try:
        response = _llm.invoke([
            SystemMessage(content=SYSTEM_PROMPT),
            HumanMessage(content=question),
        ])
        import json
        result = json.loads(response.content.strip())
        return {
            "is_in_scope": bool(result.get("is_in_scope", False)),
            "is_greeting": bool(result.get("is_greeting", False)),
        }
    except Exception:
        # On parse error, assume in scope so we don't block legitimate queries
        return {"is_in_scope": True, "is_greeting": False}
