"""create link table

Revision ID: 20260403_0001
Revises:
Create Date: 2026-04-03 00:00:00
"""

from alembic import op
import sqlalchemy as sa


revision = "20260403_0001"
down_revision = None
branch_labels = None
depends_on = None


def upgrade() -> None:
    op.create_table(
        "link",
        sa.Column("id", sa.Integer(), nullable=False),
        sa.Column("telegram_id", sa.Integer(), nullable=False),
        sa.Column("steam_id64", sa.String(), nullable=False),
        sa.PrimaryKeyConstraint("id"),
    )
    op.create_index("ix_link_telegram_id", "link", ["telegram_id"], unique=True)
    op.create_index("ix_link_steam_id64", "link", ["steam_id64"], unique=True)


def downgrade() -> None:
    op.drop_index("ix_link_steam_id64", table_name="link")
    op.drop_index("ix_link_telegram_id", table_name="link")
    op.drop_table("link")
