---
name: selenium-framework
description: >-
  Build, refactor, and maintain Java Selenium test automation frameworks and Page Objects with TestNG, Maven, Allure, JSON configuration, and parallel execution.
  Use when implementing or updating automated tests (e.g. TC_01, TC_02,...), Page Objects, element wrappers, driver lifecycle, assertions, and test data.
---

# Selenium framework

Implement the requested work within its scope. Communicate in the user's language. This is a coding skill, not a standalone review workflow.

## Load and resolve instructions

Before coding, read BOTH files completely:

- [core-rules.md](references/core-rules.md): correctness constraints and baseline requirements/mistakes to avoid.
- [preferences.md](references/preferences.md): active personal choices, alternatives, and defaults.

Refer to [examples/](examples/) for reference implementations:
- [SamplePage.java](examples/SamplePage.java): canonical Page Object pattern.
- [SampleTest.java](examples/SampleTest.java): canonical TestNG test pattern.

Explicit task instructions take precedence over skill guidance. Otherwise use active preferences; for `project`, preserve a consistent existing design or use the documented fallback. Examples are not active selections. Follow applicable repository instructions and surface material conflicts instead of silently applying contradictory designs.

Preferences customize design, not correctness. If a combination introduces races or lost assertions, explain the concrete conflict and implement a compatible solution within scope. Ask only when a consequential choice remains unresolved. A temporary task override does not authorize updating persistent preferences.

## Workflow

1. Inspect repository instructions, relevant code, build/suite configuration, driver ownership, and existing changes. Full-framework requirements do not require rebuilding unrelated code for a small task.
2. State the effective lifecycle and assertion placement when relevant to the change, then implement using the resolved choices.
3. Apply core rules and relevant features. Preserve unrelated changes, remove confirmed unused code in scope, and format consistently.
4. Verify meaningful changed behavior with appropriate compile/tests. For lifecycle changes inspect setup, scheduling, listeners, teardown, and session ownership together. Do not claim browser/Grid coverage without running it.
5. For authorized existing-PR updates, verify the head branch/remote and push to that same PR, not a replacement PR. Do not merge or force-push merely because this skill is active. Report blocked pushes accurately.
6. Hand off changes, material preferences applied, checks actually run, and limitations.

## Maintain this skill

Keep workflow here, stable requirements in core-rules.md, active editable choices in preferences.md, and concrete patterns in examples/. Do not duplicate active values. Use Markdown; no parser or review script is needed. If a baseline design convention becomes configurable, move that choice to preferences rather than leaving contradictory instructions in both files.
