# Release Guide

Step-by-step instructions for releasing a new version to Sonatype Maven Central.

---

## Prerequisites

Ensure the following GitHub Actions secrets are configured in the repository:
- `CENTRAL_USERNAME` — Sonatype Central Portal username
- `CENTRAL_PASSWORD` — Sonatype Central Portal password
- `GPG_PRIVATE_KEY` — GPG private key for signing artifacts
- `GPG_PASSPHRASE` — Passphrase for the GPG key

---

## Release Steps

### 1. Create a release branch

Create a new branch from `master` named after the version, e.g. `1_4_0`:

```bash
git checkout master
git pull origin master
git checkout -b 1_4_0
```

### 2. Update the version in all POM files

Run the Maven versions plugin to update all 6 `pom.xml` files at once:

```bash
mvn versions:set -DnewVersion=1.4.0 -DgenerateBackupPoms=false
```

Verify the change looks correct:

```bash
grep -r "1.4.0" --include="pom.xml"
```

### 3. Commit and push the branch

```bash
git add -A
git commit -m "Release version 1.4.0"
git push origin 1_4_0
```

### 4. Trigger the GitHub Actions release workflow

1. Go to the repository on GitHub
2. Navigate to **Actions** → **Publish to Maven Central**
3. Click **Run workflow**
4. Select the release branch (e.g. `1_4_0`) from the branch dropdown
5. Click **Run workflow**

### 5. Monitor the deployment

Once the workflow completes successfully, Login to:
- https://central.sonatype.com/publishing
(use your userid/password for Sonatype Central Portal)

You should see a deployment entry with status **PUBLISHING**. Wait for it to transition to **PUBLISHED** (usually 5–30 minutes).

### 6. Verify the published artifact

After status shows **PUBLISHED**, confirm the artifact is available:

e.g.
https://central.sonatype.com/artifact/org.jsmart/zerocode-tdd/1.4.0

```
https://central.sonatype.com/artifact/org.jsmart/zerocode-tdd/<VERSION>
```

> Note: It took 10 min to publish, but may take up to 2 hours before the artifact is resolvable via standard Maven build tools.

You can see the last 3 months stats(publisher insight) here:
- https://central.sonatype.com/publishing/insights <--- use same userid/password as above

---

## Post-Release Steps

### 7. Merge the release branch into master

Open a Pull Request from the release branch (e.g. `1_4_0`) into `master` on GitHub and merge it.

### 8. Bump to next development snapshot (best practice, not mandatory)

After merging, update `master` to the next development version:

```bash
git checkout master
git pull origin master
mvn versions:set -DnewVersion=1.4.1-SNAPSHOT -DgenerateBackupPoms=false
git add -A
git commit -m "Prepare next development version 1.4.1-SNAPSHOT"
git push origin master
```

> You can keep `1.4.0` (without `-SNAPSHOT`) on master if you prefer not to bump yet. 
> The snapshot bump is a best practice that signals active development is continuing toward the next release.
