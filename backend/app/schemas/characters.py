"""Stable summary schema for internal playable game characters."""

from pydantic import BaseModel, ConfigDict, Field, field_validator, model_validator


class GameVariantSummary(BaseModel):
    model_config = ConfigDict(frozen=True, extra="forbid")

    id: str = Field(min_length=1)
    tier_id: str = Field(min_length=1)
    name: str = Field(min_length=1)

    @field_validator("id", "tier_id", "name")
    @classmethod
    def reject_blank_strings(cls, value: str) -> str:
        normalized = value.strip()
        if not normalized:
            raise ValueError("must contain non-whitespace characters")
        return normalized


class GameCharacterSummary(BaseModel):
    model_config = ConfigDict(frozen=True, extra="forbid")

    id: str = Field(min_length=1)
    name: str = Field(min_length=1)
    group_id: str = Field(min_length=1)
    variants: tuple[GameVariantSummary, ...]

    @field_validator("id", "name", "group_id")
    @classmethod
    def reject_blank_strings(cls, value: str) -> str:
        normalized = value.strip()
        if not normalized:
            raise ValueError("must contain non-whitespace characters")
        return normalized


class GameCharacterCatalogResponse(BaseModel):
    model_config = ConfigDict(frozen=True, extra="forbid")

    items: tuple[GameCharacterSummary, ...]

    @model_validator(mode="after")
    def require_unique_catalog_ids(self) -> "GameCharacterCatalogResponse":
        character_ids = [item.id for item in self.items]
        if len(character_ids) != len(set(character_ids)):
            raise ValueError("game character IDs must be unique")
        variant_ids = [variant.id for item in self.items for variant in item.variants]
        if len(variant_ids) != len(set(variant_ids)):
            raise ValueError("game variant IDs must be unique across the catalog")
        return self
