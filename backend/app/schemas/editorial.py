"""Editorial Comic Vine DTOs kept separate from game catalog data."""

from pydantic import BaseModel, ConfigDict, Field, field_validator


class EditorialCharacter(BaseModel):
    model_config = ConfigDict(frozen=True, extra="forbid")

    id: int = Field(gt=0)
    name: str = Field(min_length=1, max_length=200)
    deck: str | None = Field(default=None, max_length=2_000)
    description: str | None = Field(default=None, max_length=20_000)
    image_url: str | None = Field(default=None, max_length=2_000)
    site_url: str = Field(min_length=1, max_length=2_000)
    real_name: str | None = Field(default=None, max_length=200)
    publisher_id: int
    publisher_name: str = Field(min_length=1, max_length=200)
    powers: tuple[str, ...] = ()
    teams: tuple[str, ...] = ()
    issue_count: int | None = Field(default=None, ge=0)
    first_appearance: str | None = Field(default=None, max_length=40)

    @field_validator("image_url")
    @classmethod
    def image_uses_https(cls, value: str | None) -> str | None:
        if value is None:
            return None
        from urllib.parse import urlparse
        if urlparse(value).scheme != "https" or urlparse(value).username is not None:
            raise ValueError("image_url must use HTTPS")
        return value

    @field_validator("site_url")
    @classmethod
    def comic_vine_attribution_link(cls, value: str) -> str:
        from urllib.parse import urlparse
        parsed = urlparse(value)
        if (parsed.scheme != "https" or parsed.hostname != "comicvine.gamespot.com"
                or parsed.username is not None or parsed.password is not None):
            raise ValueError("site_url must link to Comic Vine over HTTPS")
        return value


class EditorialCharacterPage(BaseModel):
    model_config = ConfigDict(frozen=True, extra="forbid")

    items: tuple[EditorialCharacter, ...]
    total: int = Field(ge=0)
    limit: int = Field(ge=1, le=10)
    offset: int = Field(ge=0)
    source_name: str = "Comic Vine"
    source_url: str = "https://comicvine.gamespot.com/api/"


class EditorialGamePortrait(BaseModel):
    model_config = ConfigDict(frozen=True, extra="forbid")

    game_id: str = Field(min_length=1, max_length=100)
    character_id: int | None = Field(default=None, gt=0)
    character_name: str = Field(min_length=1, max_length=200)
    image_url: str | None = Field(default=None, max_length=2_000)
    site_url: str = Field(min_length=1, max_length=2_000)
    source_name: str = "Comic Vine"
    image_credit: str | None = Field(default=None, max_length=200)

    _https_image = field_validator("image_url")(EditorialCharacter.image_uses_https.__func__)
    _site_attribution = field_validator("site_url")(EditorialCharacter.comic_vine_attribution_link.__func__)


class DeadpoolLineRequest(BaseModel):
    model_config = ConfigDict(extra="forbid", str_strip_whitespace=True)

    context_id: str = Field(min_length=1, max_length=40, pattern=r"^[a-z_]+$")
    prompt: str = Field(default="", max_length=300)
    game_context: str = Field(default="", max_length=6_000)



class DeadpoolLineResponse(BaseModel):
    model_config = ConfigDict(frozen=True, extra="forbid")

    text: str = Field(min_length=1, max_length=500)
    fallback: bool
