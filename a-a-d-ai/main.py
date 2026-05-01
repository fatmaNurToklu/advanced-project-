import os
import uuid
from typing import Optional
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from dotenv import load_dotenv

load_dotenv()

from database import get_user_role
from graph import compiled_graph

app = FastAPI(title="DataPulse AI Chatbot", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)


class AskRequest(BaseModel):
    question: str
    user_id: str
    session_id: str = ""


class AskResponse(BaseModel):
    answer: str
    sql_query: Optional[str] = None
    visualization_code: Optional[str] = None
    has_visualization: bool = False


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/ask", response_model=AskResponse)
def ask(req: AskRequest):
    if not req.question.strip():
        raise HTTPException(status_code=400, detail="Question cannot be empty.")

    role, store_id = get_user_role(req.user_id)

    initial_state = {
        "question": req.question.strip(),
        "user_id": req.user_id,
        "session_id": req.session_id or str(uuid.uuid4()),
        "role": role,
        "store_id": store_id,
        "sql_query": None,
        "query_result": None,
        "error": None,
        "final_answer": None,
        "visualization_code": None,
        "is_in_scope": None,
        "is_greeting": None,
        "iteration_count": 0,
    }

    if not os.getenv("GROQ_API_KEY"):
        return AskResponse(
            answer="Groq API key is not configured. Please add your GROQ_API_KEY to the .env file and restart the server.",
        )

    try:
        result = compiled_graph.invoke(initial_state, config={"recursion_limit": 12})
    except Exception as e:
        err = str(e)
        if "Recursion limit" in err:
            return AskResponse(answer="Sorry, I couldn't process that query. Please try rephrasing your question.")
        if "insufficient_quota" in err or "429" in err:
            return AskResponse(answer="OpenAI quota exceeded. Please add credits to your OpenAI account at platform.openai.com/settings/billing and restart the server.")
        if "RateLimitError" in err:
            return AskResponse(answer="OpenAI rate limit reached. Please wait a moment and try again.")
        raise HTTPException(status_code=500, detail=f"Agent error: {err}")

    answer = result.get("final_answer") or "Bir hata oluştu, lütfen tekrar deneyin."
    viz_code = result.get("visualization_code")

    return AskResponse(
        answer=answer,
        sql_query=result.get("sql_query"),
        visualization_code=viz_code,
        has_visualization=bool(viz_code),
    )
