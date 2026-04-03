from pathlib import Path

from sqlmodel import SQLModel, create_engine, Session

BASE_DIR = Path(__file__).resolve().parent
DATABASE_PATH = BASE_DIR / "database.db"

engine = create_engine(f"sqlite:///{DATABASE_PATH}")

def get_session():
    with Session(engine) as session:
        yield session
