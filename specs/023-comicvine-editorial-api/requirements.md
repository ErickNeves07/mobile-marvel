# 023 — Comic Vine editorial API — Requirements

Status: implementation active; local key supplied 2026-10-02, live provider validation still pending.

## Scope

- Backend gateway reads `COMIC_VINE_API_KEY` at runtime; never return/log/package it.
- Character search and detail over official Comic Vine API; bound queries/pagination.
- Set `api_key`, `format=json`, descriptive `User-Agent`, timeout and minimal `field_list`.
- Validate Comic Vine publisher ID 31 or normalized publisher name `Marvel`/`Marvel Comics`; `4010` is the publisher resource type prefix, not the Marvel publisher ID. Do not rely on direct publisher filtering.
- At most 100 calls per upstream resource/hour and global spacing of one second. Cache results, surface 429 safely, credit/link Comic Vine and do not bulk redistribute.
- Editorial facts remain separate from authored game stats.

## Acceptance

- [x] Mock contract, missing key, Marvel filter, cache, quota, 429 and error-sanitization tests (12 pass).
- [ ] Live search/detail with valid key and no secret logging.
- [x] Android requests editorial data only through the backend and identifies the source.
- [ ] Full FastAPI route tests run under compatible Python 3.13 runtime.
