# Deploy CoachAssist backend on Render

This repository contains Spring Boot services, so use Render's **Docker** runtime (not Node). The root `render.yaml` Blueprint describes five services: Student, Parent, Coaching, Admin, and the public API Gateway. Eureka and Config Server are optional here: the services use explicit URLs, and the service properties read their deployed settings from environment variables.

## Before you deploy

1. Rotate the database password/connection credentials. Database credentials were committed in an application properties file, so deleting them from the current version does not remove them from Git history. Replace the credentials at the database provider and use only the new connection values in Render.
2. Confirm the current code and `render.yaml` are pushed to the GitHub branch you intend to deploy. The Render screenshot shows `master`; Render builds that remote branch, not uncommitted local files.
3. Confirm the PostgreSQL database allows connections from Render. Keep its JDBC URL, username, and password ready; do not paste them in Git or chat.

## Render dashboard flow

1. From the Render dashboard choose **New → Blueprint** and connect `ayush08/CoachAssist`.
2. Select the branch containing this `render.yaml` and create the Blueprint.
3. Render prompts for the `sync: false` variables. Enter the *rotated* database values for each of Student, Parent, Coaching, and Admin. Enter the admin email and a strong admin password for Admin. Enter the exact deployed frontend origin for `FRONTEND_ORIGIN` on the Gateway (for example, `https://your-frontend.example.com`, without a trailing slash).
4. Keep every service in **Oregon**, as set in the Blueprint. This keeps service-to-service traffic in the same Render region.
5. The current Blueprint uses the Free plan for a first smoke test. Create it only if those limitations are acceptable. Free web services can sleep after 15 minutes idle and share 750 monthly instance hours across the workspace; chained requests may wait while a sleeping service starts. For a production backend, choose paid instances and budget for each of the five services before creating them.
6. Watch the Deploys page until all five health checks are live. Open the Gateway's `https://...onrender.com/actuator/health` URL.

## What the Blueprint configures

| Render service | Repo directory | Purpose |
| --- | --- | --- |
| `coachassist-student-api` | `Backend/Student` | Student accounts, attendance, marks, and notices |
| `coachassist-parent-api` | `Backend/Parent` | Parent accounts |
| `coachassist-coaching-api` | `Backend/Coaching` | Coaching API and service calls |
| `coachassist-admin-api` | `Backend/Admin` | Admin login and admin operations |
| `coachassist-gateway` | `Backend/Api-Gateway` | Public API entry point |

All five are Web Services in this smoke-test Blueprint, so they receive public Render URLs. The gateway is the URL the frontend should use. Service-to-service URLs are fixed in the Blueprint to the corresponding Render service names. If Render requires different names because those names are already taken, change the service name and every matching URL in `render.yaml` before creating the Blueprint.

## Test after deployment

Set the Postman collection's `baseUrl` to the Gateway's Render URL, and set its `adminEmail` and `adminPassword` to the values entered above. Run **Main test sequence (run in order)**. Do not run the optional cleanup folder unless you intend to delete its test records.

The Gateway API URL will look like:

```text
https://coachassist-gateway.onrender.com
```

Use that URL for `/api/v1/...` requests. Do not use a service's port or its local `localhost` URL from the browser.

## Local development

The service properties now require `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and `JWT_SECRET`. Set the same database values and the same JWT secret for Coaching, Student, Parent, and Admin. Generate a local JWT secret with:

```powershell
[Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
```

For local runs, the existing port defaults remain in place. Set `ADMIN_EMAIL` and `ADMIN_PASSWORD` on Admin. You can enable local Eureka with `EUREKA_CLIENT_ENABLED=true`; Config Server is not required by this deployment setup.
