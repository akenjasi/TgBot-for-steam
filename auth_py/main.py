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


class BindResponse(BaseModel):
    status: str
    message: str
    steamId: str | None


class LinkResponse(BaseModel):
    status: str
    steamId: str | None


class BusinessError(Exception):
    def __init__(self, message: str):
        self.message = message


def build_bind_response(status: str, message: str, steam_id: str | None) -> BindResponse:
    return BindResponse(status=status, message=message, steamId=steam_id)


def parse_steam_id(url: str) -> str:
    parsed = urlparse(url)

    if parsed.scheme not in {"http", "https"}:
        raise BusinessError("Неверная ссылка Steam")

    if parsed.netloc not in {"steamcommunity.com", "www.steamcommunity.com"}:
        raise BusinessError("Неверная ссылка Steam")

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
                content=build_bind_response(
                    "error", "Этот Telegram уже привязан", None
                ).model_dump(),
            )

        if session.exec(select(Link).where(Link.steam_id64 == steam_id)).first():
            return JSONResponse(
                status_code=409,
                content=build_bind_response(
                    "error", "Этот Steam уже привязан", None
                ).model_dump(),
            )

        link = Link(telegram_id=data.telegramId, steam_id64=steam_id)
        session.add(link)
        try:
            session.commit()
        except IntegrityError:
            session.rollback()
            return JSONResponse(
                status_code=409,
                content=build_bind_response(
                    "error", "Конфликт привязки", None
                ).model_dump(),
            )

        return JSONResponse(
            status_code=200,
            content=build_bind_response(
                "success", "Аккаунт привязан", steam_id
            ).model_dump(),
        )
    except BusinessError as exc:
        return JSONResponse(
            status_code=400,
            content=build_bind_response("error", exc.message, None).model_dump(),
        )


@app.get("/link/{telegramId}")
def get_link(telegramId: int, session: Session = Depends(get_session)):
    link = session.exec(select(Link).where(Link.telegram_id == telegramId)).first()
    return LinkResponse(status="success", steamId=link.steam_id64 if link else None).model_dump()


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
    return JSONResponse(
        status_code=200,
        content={"status": "success", "message": "Привязка удалена"},
    )
