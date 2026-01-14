# Version Control & Tools - Complete Guide

## Overview
A practical guide to Git version control and common tooling for Java projects. Covers Git basics and workflows, branching strategies, pull requests, merge vs rebase, resolving conflicts, tagging/releases, CI/CD, build tools (Maven/Gradle), and IDE tips. Includes common interview Q&A.

---

## 1) Git Basics

### Summary
- Distributed version control: full history on every clone.
- Key concepts: repository, commit, branch, tag, remote, HEAD, staging area (index).
- Common remotes: origin (default remote name).

### Core Commands
```bash
# Config
git config --global user.name "Your Name"
git config --global user.email "you@example.com"

# Create or clone
git init
git clone <repo-url>

# Inspect
git status
git log --oneline --graph --decorate

# Stage & commit
git add .
git commit -m "Add feature X"

# Branching
git branch feature/login
git checkout -b feature/login

# Remotes
git remote -v
git fetch origin

# Push/pull
git push origin feature/login
git pull --rebase
```

---

## 2) Branching Strategies

### Summary
- main/master: stable branch.
- develop: integration branch (Git Flow).
- feature/*: new features; short-lived.
- release/*: prepare release.
- hotfix/*: urgent fixes off main.

### Popular Workflows
- Trunk-based: small frequent merges to main; feature flags for incomplete work.
- Git Flow: structured branches for features, releases, hotfixes.

### Tips
- Keep branches small and focused.
- Rebase feature branches to keep history clean.

---

## 3) Merge vs Rebase

### Summary
- merge: combines histories with a merge commit; preserves graph.
- rebase: moves commits on top of new base; linear history.

### When to Use
- Merge: when you want to keep context of divergent history; shared branches.
- Rebase: for local feature branches to maintain linear history before PR.

### Commands
```bash
# Merge main into feature
git checkout feature/login
git merge main

# Rebase feature onto main
git checkout feature/login
git fetch origin
git rebase origin/main

# Resolve conflicts ? add files ? continue
git rebase --continue
```

---

## 4) Pull Requests (PRs)

### Summary
- Code review mechanism before merging.
- Include description, rationale, screenshots/tests, and checklist.
- Address comments; keep changes small.

### Best Practices
- Write clear titles and commit messages.
- Link issues; add test coverage.
- Avoid unrelated changes.

---

## 5) Resolving Conflicts

### Summary
- Occur when changes overlap.
- Steps: fetch/rebase or merge ? fix conflicts in files ? add ? continue.

### Example Flow
```bash
git fetch origin
git rebase origin/main
# Edit conflict markers <<<<<<<, =======, >>>>>>>
git add <file>
git rebase --continue
```

### Tips
- Resolve logically; run tests; ensure both sides’ intent is preserved.

---

## 6) Tags & Releases

### Summary
- Lightweight vs annotated tags.
- Use semantic versioning (MAJOR.MINOR.PATCH).

### Commands
```bash
# Create annotated tag
git tag -a v1.2.0 -m "Release 1.2.0"
# Push tags
git push origin --tags
```

### Release Notes
- Summarize changes, breaking changes, migrations, and known issues.

---

## 7) CI/CD Basics

### Summary
- Continuous Integration: build, test on every change.
- Continuous Delivery/Deployment: automated release pipelines.

### Common Tools
- GitHub Actions, GitLab CI, Jenkins, CircleCI.

### Typical Pipeline
- Steps: checkout ? setup JDK ? cache deps ? build ? test ? package ? publish artifact ? deploy.

---

## 8) Build Tools

### Maven
- Declarative: `pom.xml` defines dependencies/plugins.
- Lifecycle: validate ? compile ? test ? package ? verify ? install ? deploy.
- Common plugins: surefire (tests), failsafe (IT), jacoco (coverage), spring-boot.

### Gradle
- Groovy/Kotlin DSL; incremental builds, flexible configuration.
- Tasks: `build`, `test`, `assemble`, `check`.

### Commands (Maven)
```bash
mvn clean test
mvn clean package
mvn versions:set -DnewVersion=1.3.0
```

---

## 9) Dependency Management

### Summary
- Use lockfiles or reproducible builds (Maven: versions plugin, dependency management).
- Keep dependencies updated; scan for vulnerabilities.
- Avoid version conflicts; use BOMs (Bill of Materials) for compatibility.

### Example BOM
```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-dependencies</artifactId>
      <version>3.2.2</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

---

## 10) IDE & Productivity Tips

### IntelliJ IDEA
- Code inspections, refactorings, live templates.
- Debugger: breakpoints, watches, evaluate expression.
- Version control integration: commit tool window, diff viewer.

### Shortcuts (Windows)
- Ctrl+Shift+A: Find action
- Alt+Enter: Quick fix
- Ctrl+Alt+L: Reformat code
- Ctrl+Shift+F10: Run
- Shift+Shift: Search everywhere

---

## 11) Conventional Commits & Changelogs

### Summary
- Standardizes commit messages: `feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:`.
- Enables automated changelog generation.

### Example
```
feat(auth): add JWT login endpoint
fix(order): prevent NPE when address missing
docs(readme): clarify setup steps
```

---

## 12) Git Hooks

### Summary
- Automate actions on git events (pre-commit, pre-push).
- Use for linting, tests, or commit message checks.

### Example
- `.git/hooks/pre-commit`: run `mvn -q -DskipTests=false test` before allowing commit.

---

## 13) Common Pitfalls

- Committing secrets; use `.gitignore` and secret scanners.
- Large binary files in repo; prefer artifact storage.
- Long-lived branches causing massive conflicts.
- Force-pushing shared branches; avoid unless necessary.
- Inconsistent commit messages; adopt conventions.

---

## Common Interview Questions & Answers

### Q1: What’s the difference between merge and rebase?
- Merge creates a merge commit and preserves branch history; rebase moves commits onto a new base, creating a linear history. Use merge for shared branches, rebase for local feature branches before PR.

### Q2: How do you resolve merge conflicts?
- Identify conflicted files, edit to reconcile changes, stage, and continue (rebase --continue or complete merge). Run tests and validate behavior.

### Q3: What is trunk-based development vs Git Flow?
- Trunk-based: small, frequent merges to main; feature flags; fast feedback.
- Git Flow: structured branches (develop, feature, release, hotfix); useful for complex release processes.

### Q4: How do you manage releases with tags?
- Use annotated tags with semantic versions; generate release notes; CI pipelines build and publish artifacts for each tag.

### Q5: What are common CI/CD steps for a Java project?
- Checkout, setup JDK, cache dependencies, build with Maven/Gradle, run unit/integration tests, produce artifacts, optionally deploy.

### Q6: Maven vs Gradle?
- Maven: simpler, declarative POM; strong conventions; wide adoption.
- Gradle: more flexible, faster incremental builds; Kotlin/Groovy DSL; better for complex multi-project setups.

### Q7: How to avoid committing secrets?
- Use .gitignore, environment variables, secret managers; pre-commit hooks and scanners; encrypt configs where necessary.

### Q8: What is a pull request and why is it important?
- A request to merge changes, enabling code review, automated checks, and discussion; ensures quality and knowledge sharing.

### Q9: What’s a BOM and why use it?
- Bill of Materials: centralizes compatible dependency versions; avoids conflicts and ensures consistent builds.

### Q10: How do you handle large binary files?
- Use Git LFS or external artifact storage (S3, Nexus); avoid storing in repo.

---

## Quick Reference
- Branching: feature/*, release/*, hotfix/*.
- Merge for shared branches; rebase for local cleanup.
- PRs: small, focused, well-documented.
- Tags: annotated + semantic versions; push tags.
- CI/CD: build, test, package, deploy.
- Maven/Gradle: choose based on team needs.
- Secrets: never commit; use managers.
- Conventional commits for readable history.

---

*Last Updated: January 2026*

