# GitHub Actions Workflows

## Maven Build, Test, and Deploy

The `maven-deploy.yml` workflow automates the build, test, and deployment process for the thymeleaf-spring project.

### Workflow Triggers

- **Push to master branches**: Triggers build, test, and deploy
  - `master`
  - `main`
  - Any branch ending with `-master` (e.g., `3.1-master`)
- **Pull requests**: Triggers build and test only (no deployment)

### Jobs

#### 1. Build and Test
- Runs on all push and pull request events
- Builds both `thymeleaf-spring5` and `thymeleaf-spring6` modules
- Executes all tests
- Uploads build artifacts for review

#### 2. Deploy
- **Only runs on push to master branches** (not on pull requests)
- Depends on successful completion of build-and-test job
- Deploys artifacts to GitLab Maven Registry

### Required GitHub Secrets

To enable deployment to the GitLab Maven Registry, you need to configure the following secret in your GitHub repository:

#### `GITLAB_DEPLOY_TOKEN`

This is a GitLab Deploy Token or Personal Access Token with write access to the package registry.

**How to create a GitLab Deploy Token:**

1. Go to your GitLab project: `https://gitlab.com/api/v4/projects/28917591`
2. Navigate to **Settings** → **Repository** → **Deploy tokens**
3. Create a new deploy token with:
   - **Name**: `github-actions-deploy`
   - **Scopes**: Select `write_package_registry`
4. Copy the generated token

**How to add the secret to GitHub:**

1. Go to your GitHub repository settings
2. Navigate to **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Name: `GITLAB_DEPLOY_TOKEN`
5. Value: Paste the GitLab deploy token
6. Click **Add secret**

### Maven Configuration

The workflow automatically configures Maven's `settings.xml` with the GitLab credentials during the deploy job. No manual Maven configuration is required.

### Deployment Targets

Both modules are deployed to:
- **Repository ID**: `kedos-maven`
- **URL**: `https://gitlab.com/api/v4/projects/28917591/packages/maven`

### Testing Locally

To test the deployment locally, you can create a `~/.m2/settings.xml` file with:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                              http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <servers>
    <server>
      <id>kedos-maven</id>
      <configuration>
        <httpHeaders>
          <property>
            <name>Deploy-Token</name>
            <value>YOUR_GITLAB_DEPLOY_TOKEN</value>
          </property>
        </httpHeaders>
      </configuration>
    </server>
  </servers>
</settings>
```

Then run:
```bash
cd thymeleaf-spring5
mvn clean deploy

cd ../thymeleaf-spring6
mvn clean deploy
```
