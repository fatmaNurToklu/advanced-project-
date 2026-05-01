import json
from langchain_groq import ChatGroq
from langchain.schema import SystemMessage, HumanMessage
from state import AgentState

_llm = ChatGroq(model="llama-3.3-70b-versatile", temperature=0)

SYSTEM_PROMPT = """You are a helpful e-commerce data analyst. The user asked a question and you ran a SQL query that returned results.

Your job: write a clear, concise, human-readable answer to the user's question based on the data.
- Summarize key findings in 1-3 sentences.
- Highlight important numbers, trends, or anomalies.
- If the result is empty, say so politely and suggest why.
- Do NOT mention SQL, databases, or technical details.
- Respond in the same language the user used (Turkish or English).
"""


def analysis_node(state: AgentState) -> dict:
    question = state["question"]
    query_result = state.get("query_result")
    error = state.get("error")

    if error and not query_result:
        return {
            "final_answer": "Üzgünüm, bu soruyu yanıtlamak için gerekli veriyi çekemedim. Lütfen sorunuzu farklı bir şekilde sormayı deneyin." if _is_turkish(question)
            else "Sorry, I couldn't retrieve the data needed to answer this question. Please try rephrasing."
        }

    try:
        rows = json.loads(query_result) if query_result else []
    except Exception:
        rows = []

    if not rows:
        return {
            "final_answer": "Bu kriterlere uyan sonuç bulunamadı." if _is_turkish(question)
            else "No results found matching your criteria."
        }

    # Truncate for context if very large
    preview = rows[:50]
    data_str = json.dumps(preview, default=str, ensure_ascii=False, indent=2)
    total = len(rows)
    note = f"\n(Showing first 50 of {total} rows)" if total > 50 else ""

    messages = [
        SystemMessage(content=SYSTEM_PROMPT),
        HumanMessage(content=f"User question: {question}\n\nData:{note}\n{data_str}"),
    ]

    try:
        response = _llm.invoke(messages)
        return {"final_answer": response.content.strip()}
    except Exception as e:
        return {"final_answer": f"Analiz sırasında bir hata oluştu: {e}"}


def _is_turkish(text: str) -> bool:
    turkish_markers = ["ı", "ğ", "ş", "ç", "ö", "ü", "mi", "mı", "mu", "mü", "nedir", "kaç", "hangi", "toplam"]
    lower = text.lower()
    return any(m in lower for m in turkish_markers)
