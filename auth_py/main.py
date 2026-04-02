from fastapi import FastAPI, Depends, HTTPException
from sqlmodel import SQLModel, select, Session
from database import engine, get_session
from models import Link, LinkCreate
from pydantic import BaseModel

app = FastAPI()


@app.on_event("startup")
def startup():
    SQLModel.metadata.create_all(engine)


class BindRequest(BaseModel):
    telegramId: int
    steamLink: str


def parse_steam_id(url: str) -> str:
    try:
        id = url.split("/profiles/")[1].strip("/")
        if not (id.isdigit() and len(id) == 17 and id.startswith("765611")):
            raise ValueError()
        return id
    except:
        raise HTTPException(400, "неправильная ссылка")


@app.post("/bind")
def bind(data: BindRequest, session: Session = Depends(get_session)):
    steam_id = parse_steam_id(data.steamLink)

    if session.get(Link, data.telegramId):
        raise HTTPException(409, "telegram уже привязан")

    if session.exec(select(Link).where(Link.steam_id64 == steam_id)).first():
        raise HTTPException(409, "steam уже привязан")

    link = Link(telegram_id=data.telegramId, steam_id64=steam_id)
    session.add(link)
    session.commit()

    return f"ok, steam: {steam_id}"


@app.get("/link/{telegram_id}")
def get_link(telegram_id: int, session: Session = Depends(get_session)):
    link = session.get(Link, telegram_id)
    return {"steam_id": link.steam_id64 if link else None}