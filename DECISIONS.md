# DECISIONS — {{PROJECT_NAME}}

> Decision log (short ADR). **Always record** when you choose between real alternatives, especially when *you* decided instead of asking.
> Read before reopening a topic. To change a decision: create a new one that **replaces it** (do not delete the old one).
> Status: `Accepted` · `Provisional` (assumed without the user's confirmation — revisit if it matters) · `Superseded by D-NNN`.

## Template

```
### D-NNN — Title  (YYYY-MM-DD) — Status
- **Context:** the problem and the constraints.
- **Options:** A) … B) … C) …
- **Analysis (5 angles):** precedent · intent · external evidence · risk/reversibility · simplicity — 1 line each.
- **Decision:** the chosen one and why.
- **Assumptions:** what I assumed without confirming.
- **How to revert:** steps/cost.
- **Consequences:** what changes; what becomes harder.
```

## Decisions

### D-003 — Client-only (Phase 1) first, server packet only as an optional enhancement  (2026-10-20) — Accepted
- **Context:** use a firework rocket while the trident is charging. Vanilla blocks new "use" client-side (`Minecraft#startUseItem` returns early on `isHandsBusy()`), but the server path (`ServerGamePacketListenerImpl#handleUseItem` → `ServerPlayerGameMode#useItem`) has **no** `isUsingItem` check.
- **Options:** A) client-only, call `MultiPlayerGameMode#useItem` directly. B) client + server custom payload only. C) server-only.
- **Analysis (5 angles):** precedent — vanilla accepts the packet; intent — works on vanilla servers without installing anything; external evidence — decompiled 26.3 bytecode confirms no server-side `isUsingItem` gate; risk — anti-cheat plugins may still flag it (acceptable); simplicity — A is minimal.
- **Decision:** implement A as the primary path; keep B behind `ClientPlayNetworking.canSend` so it only activates when the server also has the mod. Same jar, `environment: "*"`, no registries/items/blocks.
- **Assumptions:** default keybind `V`; server validation mirrors vanilla.
- **How to revert:** delete `network/` and the `main` entrypoint; keep `environment: "client"`.
- **Consequences:** vanilla clients/servers still work; the mod never forces installation.

### D-001 — Use the repository template with management documents  ({{DATE}}) — Accepted
- **Context:** a new project, developed with AI agents during long sessions.
- **Decision:** keep the project state in versioned `.md` files (`PROGRESS`, `TODO`, `DECISIONS`…) to survive context compression and session handoffs.
- **How to revert:** delete the management files and simplify `AGENTS.md`.

### D-002 — Modular, reusable, documented code by default  ({{DATE}}) — Accepted
- **Context:** much code is written by agents in long sessions; without rules, duplication, `utils` junk drawers and modules nobody knows how to use appear.
- **Decision:** reuse before writing (`docs/MODULES.md` + `scripts/find-reuse.sh`); pure functions and modules with a minimal public API; structure by feature; everything public documented (docstring + module README). Skills `reusable-modules`, `project-structure`, `code-documentation`.
- **How to revert:** remove the three skills and the corresponding rules in `AGENTS.md` §5 and `docs/CONVENTIONS.md`.
