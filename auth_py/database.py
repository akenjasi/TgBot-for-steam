from pathlib import Path

from sqlmodel import Session, create_engine

BASE_DIR = Path(__file__).resolve().parent
DATABASE_PATH = BASE_DIR / "database.db"
DATABASE_URL = f"sqlite:///{DATABASE_PATH}"

engine = create_engine(DATABASE_URL)


def get_session():
    with Session(engine) as session:
        yield session
