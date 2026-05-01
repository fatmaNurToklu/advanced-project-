import json
from langchain_groq import ChatGroq
from langchain.schema import SystemMessage, HumanMessage
from state import AgentState

_llm = ChatGroq(model="llama-3.3-70b-versatile", temperature=0)

SYSTEM_PROMPT = """You are a data visualization expert. Given data and a question, decide if a chart adds value.

If YES, return a valid Plotly JSON object with this exact structure:
{
  "data": [ { "type": "bar", "x": [...], "y": [...], "name": "..." } ],
  "layout": {
    "title": "...",
    "paper_bgcolor": "#080818",
    "plot_bgcolor": "#080818",
    "font": { "color": "#e2e8f0" },
    "xaxis": { "gridcolor": "#1e1e4a" },
    "yaxis": { "gridcolor": "#1e1e4a" }
  }
}

Chart type rules:
- Comparisons between categories → "bar"
- Time series / trends → "scatter" with mode "lines+markers"
- Part-of-whole / distributions → "pie" (use "labels" and "values" instead of x/y)
- Ranking → horizontal bar: add "orientation": "h", swap x and y

Use actual values from the data. Keep titles concise.

If the data doesn't suit visualization (single value, text-only results), return exactly: NO_CHART

Return ONLY the JSON object or NO_CHART. No markdown, no explanation, no extra text.
"""


def visualization_node(state: AgentState) -> dict:
    question = state["question"]
    query_result = state.get("query_result")

    if not query_result:
        return {"visualization_code": None}

    try:
        rows = json.loads(query_result)
    except Exception:
        return {"visualization_code": None}

    if not rows or len(rows) < 2:
        return {"visualization_code": None}

    preview = rows[:100]
    data_str = json.dumps(preview, default=str, ensure_ascii=False)

    messages = [
        SystemMessage(content=SYSTEM_PROMPT),
        HumanMessage(content=f"User question: {question}\n\nData (list of dicts):\n{data_str}"),
    ]

    try:
        response = _llm.invoke(messages)
        code = response.content.strip()

        if code == "NO_CHART" or not code:
            return {"visualization_code": None}

        # Strip accidental markdown fences
        if code.startswith("```"):
            lines = code.split("\n")
            code = "\n".join(lines[1:-1] if lines[-1].strip() == "```" else lines[1:])

        # Validate it's proper JSON
        parsed = json.loads(code)
        if "data" not in parsed or "layout" not in parsed:
            return {"visualization_code": None}

        return {"visualization_code": json.dumps(parsed)}
    except Exception:
        return {"visualization_code": None}
