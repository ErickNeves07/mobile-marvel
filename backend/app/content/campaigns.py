"""Nine authored campaign chapters in the published cosmic map order."""

from app.schemas.campaigns import CampaignCatalogResponse, CampaignSummary


CAMPAIGNS = CampaignCatalogResponse(
    items=(
        CampaignSummary(id="nova-york-ruptura", title="Nova York em Ruptura",
                        faction_id="all", boss_id="rei-do-crime", chapter=1),
        CampaignSummary(id="complexo-ultron", title="Complexo de Ultron",
                        faction_id="all", boss_id="ultron", chapter=2),
        CampaignSummary(id="wakanda-cerco", title="Wakanda sob Cerco",
                        faction_id="all", boss_id="wakanda-tech", chapter=3),
        CampaignSummary(id="dimensao-espelhada", title="Dimensão Espelhada",
                        faction_id="all", boss_id="dormammu", chapter=4),
        CampaignSummary(id="knowhere-abismo", title="Knowhere: Abismo Celestial",
                        faction_id="all", boss_id="ronan", chapter=5),
        CampaignSummary(id="xmen-ruptura-genetica", title="Instituto Xavier: Ruptura Genética",
                        faction_id="all", boss_id="magneto", chapter=6),
        CampaignSummary(id="zona-negativa", title="Zona Negativa: Horizonte Fantástico",
                        faction_id="all", boss_id="annihilus", chapter=7),
        CampaignSummary(id="latveria-cidadela", title="Latveria: Cidadela da Ordem",
                        faction_id="all", boss_id="doutor-destino", chapter=8),
        CampaignSummary(id="tita-colapso", title="Titã em Colapso",
                        faction_id="all", boss_id="thanos", chapter=9),
    )
)
