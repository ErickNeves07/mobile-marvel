# 047 - Design

## Deadpool request size and compatibility

- `DeadpoolLineRequest.game_context` remains bounded at 6,000 characters.
- Increase the provider-side per-message validation bound to 8,000 characters. The largest route system message is the bounded game facts plus fixed prompt instructions, so tests verify its actual computed length and provider validation.
- Keep one HTTP 422 compatibility retry, but retain the original `context_id` as in the pre-regression implementation. The app still sends the compact legacy prompt on retry.
- Test route and adapter behavior with mocked transports/providers only; no paid provider calls.

## Nexus label

- Remove the format token from the string resource and compose the accessibility label as "Abrir: <destino>" in code.

## Battle reward and lore

- Extract the delayed battle-render condition into a small method. The callback always clears `battleAnimating`, but only rerenders when the same battle is current and neither a story nor a reward claim is active.
- Disable the collect button and change its label immediately. Restore it on the UI thread if persistence throws.
- Always play the authored victory scene after a valid collection click; pass the idempotent `granted` result to the reward screen. First claim grants and shows resources. Duplicate claim shows the existing receipt without adding resources.

## Verification

- Backend route test submits exactly 6,000 characters and runs the actual provider message validator through a mocked Gemini adapter.
- Android UI tests inspect the plain Nexus label, duplicate victory scene/receipt flow, and render guard for a pending animation.
- Run Android unit tests, instrumentation compile, lint, assemble, and backend pytest/pip check.
