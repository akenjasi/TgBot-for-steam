from fastapi import FastAPI, Depends
from sqlmodel import SQLModel, select, Session
from database import engine, get_session
from models import Link, LinkCreate
from pydantic import BaseModel
from typing import Optional

app = FastAPI()


@app.on_event("startup")
def startup():
    SQLModel.metadata.create_all(engine)


class BindRequest(BaseModel):
    telegramId: int
    steamLink: str


class BindResponse(BaseModel):
    status: str
    message: str
    steamId: Optional[str]


def parse_steam_id(url: str) -> Optional[str]:
    try:
        id = url.split("/profiles/")[1].strip("/")
        if not (id.isdigit() and len(id) == 17 and id.startswith("765611")):
            raise ValueError()
        return id
    except:
        return None


@app.post("/bind", response_model=BindResponse)
def bind(data: BindRequest, session: Session = Depends(get_session)) -> BindResponse:
    steam_id = parse_steam_id(data.steamLink)

    if steam_id is None:
        return BindResponse(
            status="error",
            message="неправильная ссылка",
            steamId=None,
        )

    if session.get(Link, data.telegramId):
        return BindResponse(
            status="error",
            message="telegram уже привязан",
            steamId=None,
        )

    if session.exec(select(Link).where(Link.steam_id64 == steam_id)).first():
        return BindResponse(
            status="error",
            message="steam уже привязан",
            steamId=None,
        )

    link = Link(telegram_id=data.telegramId, steam_id64=steam_id)
    session.add(link)
    session.commit()

    return BindResponse(
        status="ok",
        message="привязка выполнена",
        steamId=steam_id,
    )


@app.get("/link/{telegram_id}")
def get_link(telegram_id: int, session: Session = Depends(get_session)):
    link = session.get(Link, telegram_id)
    return {"steam_id": link.steam_id64 if link else None}
