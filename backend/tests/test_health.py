from fastapi.testclient import TestClient
from unittest.mock import patch

from app.main import app


client = TestClient(app)


def test_health_returns_stable_contract() -> None:
    response = client.get("/health")

    assert response.status_code == 200
    assert response.json() == {
        "status": "ok",
        "service": "ruptura-infinita-backend",
    }


def test_readiness_reports_missing_integrations_without_secret_values() -> None:
    with patch.dict("os.environ", {}, clear=True):
        response = client.get("/ready")

    assert response.status_code == 200
    assert response.json() == {
        "status": "degraded",
        "service": "ruptura-infinita-backend",
        "integrations": {"comic_vine": False, "groq": False},
    }


def test_readiness_reports_configured_integrations_as_booleans_only() -> None:
    with patch.dict("os.environ", {"COMIC_VINE_API_KEY": "hidden-cv", "GROQ_API_KEY": "hidden-groq"}, clear=True):
        response = client.get("/ready")

    assert response.status_code == 200
    assert response.json()["status"] == "ok"
    assert response.json()["integrations"] == {"comic_vine": True, "groq": True}
    assert "hidden" not in response.text
