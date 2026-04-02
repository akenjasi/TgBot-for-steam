from fastapi import FastAPI, Depends
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


class BindResponse(BaseModel):
    status: str
    message: str
    steamId: str | None


class BusinessError(Exception):
    def __init__(self, message: str):
        self.message = message


def build_bind_response(status: str, message: str, steam_id: str | None) -> BindResponse:
    return BindResponse(status=status, message=message, steamId=steam_id)


def parse_steam_id(url: str) -> str:
    try:
        steam_id = url.split("/profiles/")[1].strip("/")
    except Exception as exc:
        raise BusinessError("неправильная ссылка") from exc

    if not (steam_id.isdigit() and len(steam_id) == 17 and steam_id.startswith("765611")):
        raise BusinessError("неправильная ссылка")

    return steam_id


@app.post("/bind")
def bind(data: BindRequest, session: Session = Depends(get_session)):
    try:
        steam_id = parse_steam_id(data.steamLink)

        if session.get(Link, data.telegramId):
            return build_bind_response("error", "telegram уже привязан", None)

        if session.exec(select(Link).where(Link.steam_id64 == steam_id)).first():
            return build_bind_response("error", "steam уже привязан", None)

        link = Link(telegram_id=data.telegramId, steam_id64=steam_id)
        session.add(link)
        session.commit()

        return build_bind_response("success", "привязка выполнена", steam_id)
    except BusinessError as exc:
        return build_bind_response("error", exc.message, None)


@app.get("/link/{telegram_id}")
def get_link(telegram_id: int, session: Session = Depends(get_session)):
    link = session.get(Link, telegram_id)
    return {"steam_id": link.steam_id64 if link else None}
