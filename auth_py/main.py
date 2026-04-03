from fastapi import FastAPI, Depends
from sqlalchemy.exc import IntegrityError
from sqlmodel import SQLModel, select, Session
from database import engine, get_session
from models import Link
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


BIND_SUCCESS_MESSAGE = "Аккаунт привязан"
BIND_TELEGRAM_CONFLICT_MESSAGE = "Этот Telegram уже привязан"
BIND_STEAM_CONFLICT_MESSAGE = "Этот Steam уже привязан"
BIND_INVALID_LINK_MESSAGE = "Неверная ссылка Steam"


def build_bind_response(status: str, message: str, steam_id: str | None) -> BindResponse:
    return BindResponse(status=status, message=message, steamId=steam_id)


def parse_steam_id(url: str) -> str:
    try:
        steam_id = url.split("/profiles/")[1].strip("/")
    except Exception as exc:
        raise BusinessError(BIND_INVALID_LINK_MESSAGE) from exc

    if not (steam_id.isdigit() and len(steam_id) == 17 and steam_id.startswith("765611")):
        raise BusinessError(BIND_INVALID_LINK_MESSAGE)

    return steam_id


@app.post("/bind")
def bind(data: BindRequest, session: Session = Depends(get_session)):
    try:
        steam_id = parse_steam_id(data.steamLink)

        existing_telegram_link = session.exec(
            select(Link).where(Link.telegram_id == data.telegramId)
        ).first()
        if existing_telegram_link:
            return build_bind_response("error", BIND_TELEGRAM_CONFLICT_MESSAGE, None)

        if session.exec(select(Link).where(Link.steam_id64 == steam_id)).first():
            return build_bind_response("error", BIND_STEAM_CONFLICT_MESSAGE, None)

        link = Link(telegram_id=data.telegramId, steam_id64=steam_id)
        session.add(link)
        try:
            session.commit()
        except IntegrityError:
            session.rollback()
            conflict_telegram_link = session.exec(
                select(Link).where(Link.telegram_id == data.telegramId)
            ).first()
            if conflict_telegram_link:
                return build_bind_response("error", BIND_TELEGRAM_CONFLICT_MESSAGE, None)

            conflict_steam_link = session.exec(select(Link).where(Link.steam_id64 == steam_id)).first()
            if conflict_steam_link:
                return build_bind_response("error", BIND_STEAM_CONFLICT_MESSAGE, None)

            return build_bind_response("error", BIND_STEAM_CONFLICT_MESSAGE, None)

        return build_bind_response("success", BIND_SUCCESS_MESSAGE, steam_id)
    except BusinessError as exc:
        return build_bind_response("error", exc.message, None)


@app.get("/link/{telegram_id}")
def get_link(telegram_id: int, session: Session = Depends(get_session)):
    link = session.exec(select(Link).where(Link.telegram_id == telegram_id)).first()
    return {"steamId": link.steam_id64 if link else None}
