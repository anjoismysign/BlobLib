# apidiff

Diffs two versions of a JAR and migrates downstream sources against it, so a
package refactor does not have to be re-derived by hand (or by an LLM) every
time.

```bash
M=~/.m2/repository/io/github/anjoismysign/bloblib

# 1. build the map (needs javap on PATH; ~1s for a 1000-class jar)
python3 tools/apidiff.py map \
    $M/1.698.32/BlobLib-1.698.32.jar \
    $M/1.700/BlobLib-1.700.jar \
    --prefix io.github.anjoismysign.bloblib \
    -o migration-map.json

# 2. see how each downstream project is affected (read-only)
python3 tools/apidiff.py scan migration-map.json ~/IdeaProjects/*/

# 3. rewrite the safe ones in place
python3 tools/apidiff.py apply migration-map.json ~/IdeaProjects/BlobRP --dry-run
python3 tools/apidiff.py apply migration-map.json ~/IdeaProjects/BlobRP
```

`--prefix` is worth passing: it is what makes `managers.Manager` resolve to
`bloblib.manager.Manager` instead of a same-named class from a bundled
third-party dependency.

## The three tiers

The point of the tool is deciding **what a script is allowed to do by itself**.

| Tier | Meaning | Who handles it |
| --- | --- | --- |
| `AUTO` | one unambiguous target, public API byte-identical | the script — `apply` rewrites these |
| `REVIEW` | moved but the API changed, or several equally plausible targets | an LLM, or a careful read |
| `MANUAL` | gone, no class of that name anywhere in the new jar | you |

Class-name matching alone is **not** enough to justify an automatic rewrite,
which is why the tool runs `javap` over both sides and compares member
signatures. Concretely, on the 1.698.32 → 1.700 diff:

```
1014 -> 976 classes, 328 vanished from their old FQN
  AUTO    272
  REVIEW   14
  MANUAL   42
```

Name matching alone called all 286 of those "moved". Signature comparison
pulled 14 back out, including:

```
events.ProfileLoadEvent -> net.milkbowl.vault.profile.ProfileLoadEvent
    lost: Profile getProfile(), ProfileLoadEvent(Player, Profile, boolean)
```

That one compiles-then-breaks: consumers calling only `getPlayer()` migrate
silently and fine, while consumers calling `getProfile()` do not. It is exactly
the class of change a name-only mapping hands you with false confidence.

## Validation

Replayed against BlobRP's pre-migration commit and compared to the
known-good migrated tree (import order normalized):

```
identical after apply:  94 files
differ in imports only:  6 files   <- all REVIEW, deliberately not touched
differ in code body:     2 files   <- ProfileLoadEvent rework, needs judgment
```

No file was auto-rewritten incorrectly. Two of the three REVIEW classes
(`BlobPlugin`, `Manager`) turned out to be safe renames in practice — the tool
is deliberately conservative, and would rather over-escalate than silently
mis-rewrite.

## Limits — read before trusting it

- **Only matches fully-qualified names in source text.** Imports and inline
  FQNs are found; a class referenced by simple name after a wildcard import
  (`import foo.*;`) is not.
- **A class renamed *and* moved reads as `MANUAL`.** The leaf name changed, so
  there is nothing to match on. `middleman.profile.ProfileProvider` →
  `vault.profile.ElasticProfile` is this case: a real successor exists, but no
  script will find it. This is the main reason `MANUAL` means "look", not
  "delete".
- **Same name, same package, changed behavior is invisible.** Nothing vanished
  from the inventory, so it never enters the diff. Only tests catch that.
- **`AUTO` guarantees it compiles, not that it is correct.** Generics and
  modifiers are normalized away before comparison, so a `List<String>` →
  `List<T>` shift will not escalate.
- `apply` edits files in place with no backup. Commit or stash first.
