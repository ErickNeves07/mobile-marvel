"""Stable public response schemas for the local campaign catalog."""

from pydantic import BaseModel, ConfigDict, Field


class CampaignSummary(BaseModel):
    model_config = ConfigDict(frozen=True, extra="forbid")

    id: str = Field(min_length=1)
    title: str = Field(min_length=1)
    faction_id: str = Field(min_length=1)
    boss_id: str | None = Field(default=None, min_length=1)
    chapter: int | None = Field(default=None, ge=1, le=9)


class CampaignCatalogResponse(BaseModel):
    model_config = ConfigDict(frozen=True, extra="forbid")

    items: tuple[CampaignSummary, ...]
