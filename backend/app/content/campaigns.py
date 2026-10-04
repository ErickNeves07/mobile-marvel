"""Approved campaign summaries used by the local catalog endpoint."""

from app.schemas.campaigns import CampaignCatalogResponse, CampaignSummary


CAMPAIGNS = CampaignCatalogResponse(
    items=(
        CampaignSummary(
            id="x-men",
            title="X-Men",
            faction_id="x-men",
            boss_id="magneto",
        ),
        CampaignSummary(
            id="fantastic-four",
            title="Quarteto Fantástico",
            faction_id="fantastic-four",
        ),
    )
)
