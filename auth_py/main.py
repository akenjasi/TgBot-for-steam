from urllib.parse import urlparse

from fastapi import Depends, FastAPI
from fastapi.responses import JSONResponse
from pydantic import BaseModel
from sqlalchemy.exc import IntegrityError
from sqlmodel import SQLModel, Session, select

from database import engine, get_session
from models import Link

app = FastAPI()


@app.on_event("startup")
def startup():
    SQLModel.metadata.create_all(engine)


class BindRequest(BaseModel):
    telegramId: int
    steamLink: str


class BusinessError(Exception):
    def __init__(self, message: str):
        self.message = message


def parse_steam_id(url: str) -> str:
    parsed = urlparse(url)
    path = parsed.path.strip("/")
    parts = path.split("/")

    if len(parts) != 2 or parts[0] != "profiles":
        raise BusinessError("Неверная ссылка Steam")

    steam_id = parts[1]
    if not (steam_id.isdigit() and len(steam_id) == 17 and steam_id.startswith("765611")):
        raise BusinessError("Неверная ссылка Steam")

    return steam_id


@app.post("/bind")
def bind(data: BindRequest, session: Session = Depends(get_session)):
    try:
        steam_id = parse_steam_id(data.steamLink)

        existing_telegram_link = session.exec(
            select(Link).where(Link.telegram_id == data.telegramId)
        ).first()
        if existing_telegram_link:
            return JSONResponse(
                status_code=409,
                content={
                    "status": "error",
                    "message": "Этот Telegram уже привязан",
                    "steamId": None,
                },
            )

        existing_steam_link = session.exec(select(Link).where(Link.steam_id64 == steam_id)).first()
        if existing_steam_link:
            return JSONResponse(
                status_code=409,
                content={
                    "status": "error",
                    "message": "Этот Steam уже привязан",
                    "steamId": None,
                },
            )

        link = Link(telegram_id=data.telegramId, steam_id64=steam_id)
        session.add(link)
        try:
            session.commit()
        except IntegrityError:
            session.rollback()
            return JSONResponse(
                status_code=409,
                content={
                    "status": "error",
                    "message": "Конфликт привязки",
                    "steamId": None,
                },
            )

        return JSONResponse(
            status_code=200,
            content={
                "status": "success",
                "message": "Аккаунт привязан",
                "steamId": steam_id,
            },
        )
    except BusinessError as exc:
        return JSONResponse(
            status_code=400,
            content={
                "status": "error",
                "message": exc.message,
                "steamId": None,
            },
        )


@app.get("/link/{telegramId}")
def get_link(telegramId: int, session: Session = Depends(get_session)):
    link = session.exec(select(Link).where(Link.telegram_id == telegramId)).first()
    return {"status": "success", "steamId": link.steam_id64 if link else None}


@app.delete("/link/{telegramId}")
def delete_link(telegramId: int, session: Session = Depends(get_session)):
    link = session.exec(select(Link).where(Link.telegram_id == telegramId)).first()
    if not link:
        return JSONResponse(
            status_code=404,
            content={"status": "error", "message": "Привязка не найдена"},
        )

    session.delete(link)
    session.commit()
    return {"status": "success", "message": "Привязка удалена"}
