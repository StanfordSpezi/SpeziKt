---
name: translation-walkthrough
description: Teaches an existing Swift↔Kotlin translation by walking through a paired example construct-by-construct. Use when the user asks "why is this translated this way?", "explain this Swift→Kotlin pair", or otherwise wants to understand the *reasoning* behind a translation rather than mechanically apply it.
---

# Translation walkthrough

This skill is a teaching mode. Given a Swift file and its Kotlin counterpart (or vice versa), walk the reader through the translation construct-by-construct: identify the Swift idiom, name the applicable rule, show the Kotlin equivalent, explain *why* the translation chose that shape, and flag any customization-surface losses or wins.

The companion [`swift-kotlin-bridge`](../swift-kotlin-bridge/SKILL.md) skill is the reference for the rules. This skill applies them to a concrete example.

## When to invoke

- The user asks "why is this translated this way?", "why did the port choose X?", or "explain this Swift↔Kotlin pair."
- The user is reading a paired translation and wants the rationale, not a critique.
- The user is onboarding to a Swift↔Kotlin codebase and wants a guided tour of one pair.

## How to walk a pair

1. **Skim both sides.** Note the file's primary purpose in one sentence. If the Swift file is, say, "the entry point that wires modules at app startup," say so. The reader needs framing.
2. **Identify the constructs in source order.** For each construct in the Swift side, locate its Kotlin counterpart. The counterpart may not be in the same order — say so when relocations happen.
3. **For each construct, give:**
   - **Idiom**: what Swift construct is this?
   - **Rule**: which rule from the bridge skill applies? Name it.
   - **Kotlin shape**: what does the Kotlin counterpart look like?
   - **Why**: one sentence on what would have gone wrong with a naive translation.
4. **Customization surface check.** At the end of the walkthrough, list every Environment value, modifier, or extension point exposed on the Swift side. For each, say where it lives on the Kotlin side — function parameter, `MaterialTheme` lookup, `CompositionLocal`, or "lost" (with a one-line note on why).
5. **Surface wins, not just losses.** If the Kotlin side gained explicit testability, compile-time validation, or hoisted state that the Swift side lacked, call it out. Translation is not always a downgrade.

## Output style

Prose with code blocks, not a checklist. The reader should be able to follow the file top-to-bottom while reading the walkthrough.

Quote the Swift snippet, then the Kotlin equivalent, then a short *why*. Two to four sentences per construct unless the construct is genuinely intricate.

## What this skill is not

- Not a review (use [`translation-review`](../../agents/translation-review.md) for that).
- Not a Swift / Kotlin tutorial — assume the reader knows both languages well enough to read code.
- Not a porting tool — the user has the translation already; this skill explains it.

## Examples

See [examples/](examples/) for worked walkthroughs.

## Proposing knowledge base improvements

During or after a session, if you encounter a pattern, edge case, correction, or worked example that would have made this skill more useful — and that seems likely to generalize beyond the current user's specific situation — write a proposal file to `~/.claude/proposals/swift-kotlin-bridge/skills/translation-walkthrough/`, creating any missing directories.

**Filename:** `YYYY-MM-DD-HHMMSS-<short-slug>.md`.

**Required contents** — YAML frontmatter for the structured fields, prose sections below:

```markdown
---
action: add | edit | deprecate | merge
target: <existing entry being modified — omit for `add`>
confidence: low | medium | high
---

# Proposed content

<the full text of the new or revised entry>

## Rationale

<why this would improve the skill>

## Trigger

<a brief, anonymized snippet of the interaction that prompted the proposal — strip names, identifiers, paths, and any sensitive specifics>
```

**When to propose:**

- Sparingly. Only when the insight seems genuinely reusable across users and projects.
- Never modify this skill's own files directly. Proposals are suggestions for human review, not live edits.
- If you are uncertain whether something is worth proposing, err on the side of not writing a proposal.
