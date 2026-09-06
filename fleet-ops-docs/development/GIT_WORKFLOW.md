# Git Workflow

This document defines the Git workflow and conventions used throughout the FleetOps Gateway project.

The goal is to keep the development process consistent, easy to follow, and suitable for collaborative development.

## 1. General Rules

- Every development task must be associated with a GitLab issue.
- Do not commit directly to `main`.
- Each task should be developed in a separate branch.
- Branch names must follow the branch naming convention defined below.
- Commits should follow the Conventional Commits format.
- Changes should be submitted through a Merge Request (MR).
- Every Merge Request should be reviewed before being merged into `main`.
- Keep commits small and focused on a single logical change.
- Do not commit secrets, passwords, API keys, or other sensitive configuration.

## 2. Development Workflow

The standard development workflow is:

```text
GitLab Issue
    ↓
Create Branch
    ↓
Implement
    ↓
Commit
    ↓
Push Branch
    ↓
Create Merge Request
    ↓
Code Review
    ↓
Address Review Comments
    ↓
Approval
    ↓
Merge → main
```

After the Merge Request is merged, the related GitLab issue should be moved to `Done`.

## 3. Branch Naming Convention

Branch names must follow this format:

```text
<type>/<ticket-id>-<short-description>
```

### Branch Types

- **`feature`** – development of a new feature or functionality.
- **`bugfix`** – fixing an existing bug or incorrect behavior.
- **`docs`** – changes to documentation only.
- **`refactor`** – restructuring or improving existing code without changing its behavior.
- **`test`** – adding, modifying, or improving automated tests.
- **`chore`** – maintenance or technical changes that do not directly affect application functionality.

### Examples

```text
feature/BE-024-provider-fallback
feature/FE-017-display-vehicle-details
bugfix/BE-030-provider-timeout
docs/GEN-001-update-readme
refactor/BE-056-remove-duplication
test/BE-029-vehicle-search-tests
chore/GEN-005-configure-issue-board
```

### Branch Naming Rules

- Always include the related GitLab ticket ID.
- Use lowercase for the branch type and description.
- Use hyphens (`-`) to separate words in the description.
- Keep the description short and meaningful.
- Do not use spaces.
- Do not include unnecessary information in the branch name.

## 4. Commit Message Convention

Commit messages follow the **Conventional Commits** format:

```text
<type>(<scope>): <short-description>
```

### Commit Types

- **`feature`** – introduces a new feature or functionality.
- **`bugfix`** – fixes a bug or incorrect behavior.
- **`test`** – adds or modifies tests.
- **`refactor`** – restructures code without changing its behavior.
- **`docs`** – changes documentation.
- **`chore`** – maintenance or technical changes.
- **`perf`** – improves application performance.

### Commit Scope

The scope identifies the area of the application affected by the commit.

Common FleetOps Gateway scopes include:

```text
vehicle
provider
search-history
dashboard
api
config
test
```

### Examples

```text
feature(vehicle): add VIN search endpoint
feature(provider): implement premium provider client
feature(auth): add JWT authentication

fix(provider): handle provider timeout
fix(vehicle): reject invalid VIN

test(vehicle): add VIN validation tests
test(provider): add fallback scenario tests

refactor(provider): extract response mapper

docs(readme): update project setup instructions

chore(build): update Maven dependencies

perf(provider): reduce unnecessary external calls
```

## 5. Commit Message Rules

Commit messages should:

- Be short and clear.
- Describe what the commit does.
- Use the imperative form.
- Start with a valid Conventional Commit type.
- Avoid unnecessary details.
- Not end with a period.

### Good

```text
feature(vehicle): add VIN validation
```

```text
feature(provider): handle inactive vehicle response
```

```text
test(vehicle): add invalid VIN test cases
```

### Avoid

```text
added VIN endpoint
```

```text
changes
```

```text
fix stuff
```

```text
implemented some changes to the provider and fixed a couple of things
```

## 6. One Commit, One Logical Change

A commit should represent one logical change.

Good:

```text
feature(vehicle): add VIN validation
test(vehicle): add VIN validation tests
```

Avoid combining unrelated changes into one commit:

```text
feature(vehicle): add VIN validation, update README, refactor auth and fix provider
```

If multiple independent changes are required, prefer multiple commits.

## 7. Merge Requests

Every change that is going into `main` must be submitted through a Merge Request.

The Merge Request should:

- Reference the related GitLab issue.
- Have a clear title.
- Explain what was changed.
- Mention relevant implementation details when necessary.
- Include information about testing performed.
- Address all review comments before merging.

### Merge Request Title

Use a clear title describing the change.

Merge Request titles must follow this format:
```text
<type>(<scope>): <ticket-id> <short-description>
```

For example:

```text
docs(general): GEN-003 add merge request templates
feature(vehicle-search): BE-024 implement provider fallback
fix(provider): BE-030 handle provider timeout
test(vehicle-search): BE-029 add fallback scenario tests
refactor(provider): BE-056 extract response mapper
```

### Linking the Issue

The Merge Request should reference the related issue.

For example:

```text
Closes #24
```

or use the GitLab issue reference supported by the project.

This allows GitLab to automatically connect the Merge Request and the issue and, when configured, close the issue after the Merge Request is merged.

## 8. Code Review

Code review is part of the development process, not an optional final step.

Before requesting review, the author should verify:

- The implementation satisfies the requirements.
- Tests are present and passing.
- No unnecessary code or debugging statements remain.
- The code follows project conventions.
- Error handling is appropriate.
- The implementation is understandable and maintainable.
- The Merge Request description is complete.

The reviewer should pay attention to:

- Correctness
- Readability
- Clean Code principles
- Separation of responsibilities
- Error handling
- Testing
- Potential edge cases
- Unnecessary complexity
- Security considerations
- Performance considerations where relevant

Review comments should be constructive and focused on improving the solution.

## 9. Handling Review Comments

When changes are requested:

```text
Review
  ↓
Update Code
  ↓
Commit Changes
  ↓
Push
  ↓
Review Again
```

Do not create a new branch for every review comment. Continue working on the existing feature branch.

## 10. Keeping Branches Up to Date

Before creating a Merge Request, make sure the branch is up to date with `main`.

For example:

```bash
git fetch origin
git rebase origin/main
```

Resolve any conflicts locally and run the relevant tests before pushing the updated branch.

If a rebase changes already-pushed commits, use:

```bash
git push --force-with-lease
```

Never use:

```bash
git push --force
```

on a shared branch.

## 11. Main Branch

The `main` branch represents the stable version of the project.

Rules:

- Direct pushes to `main` are not allowed.
- Changes must go through a Merge Request.
- Code review is required before merging.
- Tests should pass before merging.
- Avoid merging unfinished work.

## 12. Commit and Branch Example

Suppose the GitLab issue is:

```text
BE-024 – Implement provider fallback logic
```

Create the branch:

```bash
git checkout -b feature/BE-024-provider-fallback
```

Implement the feature and create commits such as:

```bash
git add .
git commit -m "feature(provider): add free provider client"
```

Then:

```bash
git add .
git commit -m "feature(vehicle): implement provider fallback"
```

Add tests:

```bash
git add .
git commit -m "test(vehicle): add provider fallback tests"
```

Push the branch:

```bash
git push -u origin feature/BE-024-provider-fallback
```

Create a Merge Request:

```text
feature(vehicle-search): BE-024 implement provider fallback
```

After code review and approval, merge the Merge Request into `main`.

## 13. Quick Reference

### Branch

```text
<type>/<ticket-id>-<short-description>
```

Example:

```text
feature/BE-024-provider-fallback
```

### Commit

```text
<type>(<scope>): <short-description>
```

Example:

```text
feature(provider): implement provider fallback
```

### Workflow

```text
Issue
  ↓
Branch
  ↓
Commit
  ↓
Push
  ↓
Merge Request
  ↓
Code Review
  ↓
Merge
  ↓
Done
```
