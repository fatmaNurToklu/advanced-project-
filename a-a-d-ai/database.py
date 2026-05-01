from __future__ import annotations
import os
import psycopg2
import psycopg2.extras
from dotenv import load_dotenv

load_dotenv()

DB_CONFIG = {
    "host":     os.getenv("DB_HOST", "localhost"),
    "port":     int(os.getenv("DB_PORT", 5432)),
    "dbname":   os.getenv("DB_NAME", "ecommerce_db"),
    "user":     os.getenv("DB_USER", "postgres"),
    "password": os.getenv("DB_PASSWORD", "admin123"),
}


def get_connection():
    return psycopg2.connect(**DB_CONFIG)


def execute_query(sql: str, limit: int = 200) -> tuple[list[dict], str | None]:
    """
    Execute a SELECT query safely.
    Returns (rows, error_message). rows is a list of dicts.
    Rejects non-SELECT statements.
    """
    clean = sql.strip().lstrip(";").strip()
    if not clean.upper().startswith("SELECT"):
        return [], "Only SELECT queries are allowed."

    # Enforce a row limit
    if "LIMIT" not in clean.upper():
        clean = f"{clean} LIMIT {limit}"

    try:
        conn = get_connection()
        cur = conn.cursor(cursor_factory=psycopg2.extras.RealDictCursor)
        cur.execute(clean)
        rows = [dict(r) for r in cur.fetchall()]
        cur.close()
        conn.close()
        return rows, None
    except Exception as e:
        return [], str(e)


def get_user_role(user_id: str) -> tuple[str, str | None]:
    """Returns (role, store_id). store_id is None for non-corporate users."""
    try:
        conn = get_connection()
        cur = conn.cursor()
        cur.execute("SELECT role FROM users WHERE user_id = %s", (user_id,))
        row = cur.fetchone()
        if not row:
            cur.close(); conn.close()
            return "CUSTOMER", None

        role = row[0]
        store_id = None
        if role == "CORPORATE":
            cur.execute("SELECT store_id FROM stores WHERE owner_id = %s", (user_id,))
            s = cur.fetchone()
            if s:
                store_id = s[0]

        cur.close(); conn.close()
        return role, store_id
    except Exception:
        return "CUSTOMER", None
