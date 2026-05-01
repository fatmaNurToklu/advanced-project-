from langchain_groq import ChatGroq
from langchain.schema import SystemMessage, HumanMessage
from state import AgentState
from schema import DB_SCHEMA

_llm = ChatGroq(model="llama-3.3-70b-versatile", temperature=0)

SYSTEM_TEMPLATE = """You are a PostgreSQL expert for a DataPulse e-commerce analytics platform.

DATABASE SCHEMA:
{schema}

USER CONTEXT:
- Role: {role}
- User ID: {user_id}
{store_line}

ROLE FILTER INSTRUCTIONS (follow exactly):
{role_instruction}

YOUR TASK:
Generate a single valid PostgreSQL SELECT query that answers the user's question.
Return ONLY the raw SQL query — no markdown, no explanation, no code fences.

Rules:
1. Only SELECT statements are allowed. Never generate INSERT, UPDATE, DELETE, DROP, etc.
2. Do NOT select or expose the password_hash column.
3. ALL IDs are VARCHAR — never use ::uuid cast.
4. Always add LIMIT 200 if query might return many rows.
5. Use meaningful column aliases for readability.
"""

ROLE_INSTRUCTIONS = {
    "ADMIN": "ADMIN has FULL ACCESS to all data. Do NOT add any user_id or store_id filter. Query all rows freely.",
    "CORPORATE": "CORPORATE can only see their own store's data. Always add WHERE store_id = '{store_id}' when querying orders, products, inventory. For reviews: JOIN products ON reviews.product_id = products.product_id WHERE products.store_id = '{store_id}'.",
    "CUSTOMER": "CUSTOMER can only see their own data. Always add WHERE user_id = '{user_id}' when querying orders, reviews, cart_items (via carts join).",
}


def sql_agent_node(state: AgentState) -> dict:
    role = state["role"]
    user_id = state["user_id"]
    store_id = state.get("store_id")
    question = state["question"]
    previous_error = state.get("error")

    store_line = f"- Store ID: {store_id}" if store_id else ""

    role_instr = ROLE_INSTRUCTIONS.get(role, ROLE_INSTRUCTIONS["CUSTOMER"])
    role_instr = role_instr.format(user_id=user_id, store_id=store_id or "")

    system_content = SYSTEM_TEMPLATE.format(
        schema=DB_SCHEMA,
        role=role,
        user_id=user_id,
        store_line=store_line,
        role_instruction=role_instr,
    )

    messages = [SystemMessage(content=system_content)]

    if previous_error:
        messages.append(HumanMessage(
            content=f"Previous query failed with error: {previous_error}\n\nOriginal question: {question}\nPrevious query: {state.get('sql_query', '')}\n\nFix the SQL query."
        ))
    else:
        messages.append(HumanMessage(content=question))

    try:
        response = _llm.invoke(messages)
        sql = response.content.strip()
        # Strip markdown fences if model adds them anyway
        if sql.startswith("```"):
            lines = sql.split("\n")
            sql = "\n".join(lines[1:-1] if lines[-1].strip() == "```" else lines[1:])
        return {"sql_query": sql, "error": None}
    except Exception as e:
        return {"sql_query": None, "error": str(e)}
