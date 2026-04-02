from sqlmodel import SQLModel, Field

class Link(SQLModel, table=True):
    id: int | None = Field(default=None, primary_key=True)
    telegram_id: int = Field(unique=True, index=True)
    steam_id64: str = Field(unique=True, index=True)

# создания новой записи
class LinkCreate(SQLModel):
    telegram_id: int
    steam_link: str  #ссылку, не id