# 047 - Requirements

Status: approved by Erick's direct bug report.

## Objective

Fix the reported Deadpool fallback, Nexus format-string leak, and campaign reward/story visibility regressions without changing campaign rewards or battle rules.

## Scope

- Keep the client context limit of 6,000 characters and allow bounded provider adapter messages large enough for the system prompt plus that context.
- Preserve the requested Deadpool context id in the one-time compatibility retry, matching the previously working client behavior.
- Ensure the Nexus shortcut accessibility label never exposes a raw `%1$s` placeholder.
- Prevent delayed battle animation callbacks from replacing a story/reward screen opened after a victory.
- Give immediate disabled/loading feedback on reward collection and restore the action if collection fails.
- Show the authored post-battle scene for every victory, including replayed/completed missions; duplicate receipts must still not grant credits, XP, or fragments again.

## Acceptance

- A request with the maximum allowed `game_context` reaches a mocked Gemini adapter without failing the 4,000-character provider-message validator; Groq uses the same safe bound.
- The legacy retry keeps its original context id and remains a single retry.
- Nexus accessibility labels contain the destination name and no literal `%1$s`.
- A pending battle animation callback does not redraw the battle while a reward claim or story is active.
- Reward click immediately shows a disabled progress state; a storage error restores a retryable button.
- First and repeated victories both play the chapter scene before a receipt. Only first completion grants resources; repeated completion shows a prior receipt.
- Android tests/build/lint and backend tests pass.

## Edge cases

- Provider message size includes the server's system instructions, fixed game facts, and the maximum client context; it must remain below the adapter bound.
- A delayed battle timer firing during repository I/O or the story cannot replace the active screen.
- Repeated collection remains idempotent even though the authored scene is shown again.
