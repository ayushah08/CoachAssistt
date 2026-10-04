# CoachAssist Backend API Reference

This reference lists the client-facing endpoints routed through the API Gateway and the request bodies currently accepted by the backend.

## Base URL and authentication

For local development, send requests to:

```text
http://localhost:8080
```

The API Gateway forwards `/api/v1/admin/**` requests to the Admin Service and the rest of `/api/v1/**` to the Coaching Service. For protected endpoints, send the token returned by the relevant login request:

```http
Authorization: Bearer <token>
Content-Type: application/json
```

Use the matching account role: coaching, student, parent, or admin. GET and DELETE requests below have no JSON body unless stated otherwise. Login and registration passwords must be 8–72 characters where validated.

## Coaching account

### Submit a coaching registration request

`POST /api/v1/coaching/register` — public. This creates a pending request; it does not return a login token. An admin must approve it before the coaching can log in.

```json
{
  "coachingName": "North Star Academy",
  "email": "owner@example.com",
  "coachingAddress": "12 Main Road",
  "coachingOwnerName": "Asha Rao",
  "password": "choose-a-long-password"
}
```

Required: coaching name, email, address, owner name, password. The backend also accepts `Coaching_Address` as an alias for `coachingAddress`, and `CoachingOwnerName` as an alias for `coachingOwnerName`.

### Coaching login

`POST /api/v1/coaching/login` — public. The account must be approved.

```json
{
  "email": "owner@example.com",
  "password": "choose-a-long-password"
}
```

The response includes a signed `token`, `userId`, and `role` (`COACHING`).

## Admin

Admin accounts are provisioned in the Admin Service from `ADMIN_EMAIL` and `ADMIN_PASSWORD` environment variables. There is no public admin registration endpoint.

### Admin login

`POST /api/v1/admin/login` — public.

```json
{
  "email": "admin@example.com",
  "password": "the-configured-admin-password"
}
```

The response contains an admin bearer token with role `ADMIN`.

### Review coaching requests

`GET /api/v1/admin/coaching/requests` — admin token; no body. Returns unapproved coaching requests.

### List all coaching accounts

`GET /api/v1/admin/coachings` — admin token; no body.

### Approve a coaching account

`POST /api/v1/admin/coaching/{id}/approve` — admin token; replace `{id}` with the coaching ID; no body.

### Delete a coaching account

`DELETE /api/v1/admin/coaching/{id}` — admin token; replace `{id}` with the coaching ID; no body. This deletes the coaching account and does not cascade-delete that coaching's student or parent accounts.

### List student details

`GET /api/v1/admin/students` — admin token; no body. Returns student details only, without password hashes or parent records.

### View one student's details

`GET /api/v1/admin/students/{id}` — admin token; replace `{id}` with the student ID; no body. Returns `404` if the student does not exist.

## Student accounts

### Create a student account

`POST /api/v1/student/create` — coaching token. In the current request DTO `coachingName` is required, but the student service sets the saved coaching name from the authenticated coaching token.

```json
{
  "coachingName": "North Star Academy",
  "firstName": "Riya",
  "surname": "Shah",
  "email": "riya@example.com",
  "password": "choose-a-long-password"
}
```

The response includes `studentName` and `studentCode`; use the numeric student code as `studentId` in other requests. `FirstName` and `CoachingName` are also accepted aliases for the camel-case fields.

### Student login

`POST /api/v1/student/login` — public.

```json
{
  "email": "riya@example.com",
  "password": "choose-a-long-password"
}
```

The response includes the student bearer token, `userId`, and role `STUDENT`.

### Remove a student

`DELETE /api/v1/student/{studentCode}` — coaching token; replace `{studentCode}` with the student code; no body. A coaching can remove only its own students. Success returns `204 No Content`.

## Parent accounts

### Create a parent account linked to a student

`POST /api/v1/parents/register` — coaching token. The student must belong to the authenticated coaching.

```json
{
  "name": "Meera Shah",
  "studentId": 123,
  "studentName": "Riya Shah",
  "email": "meera@example.com",
  "password": "choose-a-long-password"
}
```

Required: parent name, student ID, email, password. `studentName` is optional. The coaching name is set from the authenticated coaching account; any coaching name provided by the client is overwritten.

### Parent login

`POST /api/v1/parents/login` — public.

```json
{
  "email": "meera@example.com",
  "password": "choose-a-long-password"
}
```

The response includes a bearer token, `parentId`, linked `studentId`, and role `PARENT`.

### List parent recipients for a student

`GET /api/v1/parents/students/{studentId}/recipients` — coaching token; replace `{studentId}` with the student ID; no body. Returns linked parent IDs, names, and email addresses for targeting a private notice.

## Attendance

### Mark or update a student's attendance for a date

`POST /api/v1/attendance/students/{studentId}` — coaching token; replace `{studentId}` with the student ID.

```json
{
  "date": "2026-10-01",
  "status": "Present"
}
```

`status` is `Present` or `Absent`. One mark is stored per student per date; posting again for that date updates the existing mark. Future dates are rejected.

### View attendance and attendance percentage

`GET /api/v1/attendance/me` — student or parent token; no body. A student receives their own attendance. A parent receives the attendance for the student linked to their account. The response includes marked-day counts, percentage, and records.

## Marks

### View marks for all students in the coaching

`GET /api/v1/marks/students` — coaching token; no body. Returns a summary and records for each student in that coaching.

### View one student's marks

`GET /api/v1/marks/students/{studentId}` — coaching token; replace `{studentId}` with the student ID; no body. A coaching can only view its own students.

### Add an assessment result

`POST /api/v1/marks/students/{studentId}` — coaching token; replace `{studentId}` with the student ID.

```json
{
  "assessmentName": "Unit Test 1",
  "subject": "Mathematics",
  "obtainedMarks": 42,
  "totalMarks": 50,
  "assessmentDate": "2026-10-01",
  "remarks": "Good progress"
}
```

Required: assessment name (up to 120 characters), subject (up to 80 characters), obtained marks (zero or more), total marks (greater than zero), and assessment date. Obtained marks cannot exceed total marks. Remarks are optional and limited to 500 characters. Future assessment dates are rejected.

Overall percentage is calculated as `sum(obtainedMarks) / sum(totalMarks) * 100`, rounded to two decimal places. Each record also includes its own percentage.

### Student or parent views their marks

`GET /api/v1/marks/me` — student or parent token; no body. Returns the student's marks and weighted overall percentage. Parents see marks only for the student linked to their account.

## Notices

### List notices owned by the coaching

`GET /api/v1/notices` — coaching token; no body. Includes public and private notices created by that coaching.

### Create a public notice

`POST /api/v1/notices` — coaching token. Public notices are available to all students in that coaching and their linked parents.

```json
{
  "title": "Holiday",
  "content": "The coaching will be closed on October 2.",
  "visibility": "PUBLIC"
}
```

For a public notice, omit `studentId`, `parentId`, and `recipientType`.

### Create a private notice

`POST /api/v1/notices` — coaching token. The student must belong to the authenticated coaching. Use recipient type `STUDENT`, `PARENT`, or `BOTH`. For `PARENT` and `BOTH`, `parentId` is required and must be linked to the selected student. Get available parent IDs from the parent recipients endpoint above.

```json
{
  "title": "Schedule change",
  "content": "Please arrive at 10:00 AM on Saturday.",
  "visibility": "PRIVATE",
  "recipientType": "BOTH",
  "studentId": 123,
  "parentId": 456,
  "expiresAt": "2026-10-10T23:59:00"
}
```

`expiresAt` is optional and must be a future local date-time in ISO format, for example `2026-10-10T23:59:00`.

### Student or parent views their notices

`GET /api/v1/notices/me` — student or parent token; no body. Returns public notices for the student's coaching and private notices addressed to that student or that particular parent account. Expired notices are omitted.

## Request examples with curl

For protected requests, save the login token and pass it in the `Authorization` header. Example:

```sh
curl -X GET "http://localhost:8080/api/v1/marks/me" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

For POST requests, include `Content-Type: application/json` and use the JSON body shown for that endpoint.

## End-to-end API test sequence

Run this sequence after starting Eureka, the Student and Parent services, Admin, Coaching, and the API Gateway. Use Postman with `{{baseUrl}} = http://localhost:8080`. For every JSON request set `Content-Type: application/json`; for protected requests set `Authorization: Bearer {{adminToken}}` or `Bearer {{coachToken}}` as specified. Capture IDs and tokens from responses before continuing. Use new emails if repeating the sequence against a database that retains prior test data.

### 1. Log in as admin

`POST {{baseUrl}}/api/v1/admin/login` (no bearer token). Use the `ADMIN_EMAIL` and `ADMIN_PASSWORD` configured for the Admin service:

```json
{
  "email": "admin@example.com",
  "password": "configured-admin-password"
}
```

Save `token` from the response as `adminToken`.

### 2. Submit a coaching registration

`POST {{baseUrl}}/api/v1/coaching/register` (no bearer token):

```json
{
  "coachingName": "North Star Academy",
  "email": "coach-test@example.com",
  "coachingAddress": "12 Main Road",
  "coachingOwnerName": "Asha Rao",
  "password": "CoachPass123!"
}
```

This creates a pending request. Save response `coachingId` as `coachingId`.

### 3. Confirm the request appears in admin

`GET {{baseUrl}}/api/v1/admin/coaching/requests` with `adminToken`. No body. Find the registration from step 2 and save its ID as `coachingId`.

### 4. Approve coaching

`POST {{baseUrl}}/api/v1/admin/coaching/{{coachingId}}/approve` with `adminToken`. No body.

### 5. Log in as approved coaching

`POST {{baseUrl}}/api/v1/coaching/login` (no bearer token):

```json
{
  "email": "coach-test@example.com",
  "password": "CoachPass123!"
}
```

Save the response `token` as `coachToken`.

### 6. Create a student

`POST {{baseUrl}}/api/v1/student/create` with `coachToken`:

```json
{
  "coachingName": "North Star Academy",
  "firstName": "Riya",
  "surname": "Shah",
  "email": "student-test@example.com",
  "password": "StudentPass123!"
}
```

Save `studentCode` from the response as `studentId`. It is the numeric ID used by parent, attendance, marks, and notice requests.

### 7. Log in as student

`POST {{baseUrl}}/api/v1/student/login` (no bearer token):

```json
{
  "email": "student-test@example.com",
  "password": "StudentPass123!"
}
```

Save response `token` as `studentToken`.

### 8. Create a parent linked to that student

`POST {{baseUrl}}/api/v1/parents/register` with `coachToken`:

```json
{
  "name": "Meera Shah",
  "studentId": {{studentId}},
  "studentName": "Riya Shah",
  "email": "parent-test@example.com",
  "password": "ParentPass123!"
}
```

Use a numeric value for `studentId` (Postman can substitute the saved variable without quotation marks). Save response `parentId` as `parentId`.

### 9. Log in as parent

`POST {{baseUrl}}/api/v1/parents/login` (no bearer token):

```json
{
  "email": "parent-test@example.com",
  "password": "ParentPass123!"
}
```

Save response `token` as `parentToken`.

### 10. Check the coaching's parent recipients

`GET {{baseUrl}}/api/v1/parents/students/{{studentId}}/recipients` with `coachToken`. No body. Confirm the created parent appears and note the returned parent ID for the private notice test.

### 11. Mark attendance

`POST {{baseUrl}}/api/v1/attendance/students/{{studentId}}` with `coachToken`:

```json
{
  "date": "2026-10-03",
  "status": "Present"
}
```

Use a date that is today or earlier, and use exactly `Present` or `Absent`. Repeat with another date/status if you want a meaningful percentage.

### 12. Add a marks record

`POST {{baseUrl}}/api/v1/marks/students/{{studentId}}` with `coachToken`:

```json
{
  "assessmentName": "Unit Test 1",
  "subject": "Mathematics",
  "obtainedMarks": 42,
  "totalMarks": 50,
  "assessmentDate": "2026-10-03",
  "remarks": "Good progress"
}
```

### 13. Create a public notice

`POST {{baseUrl}}/api/v1/notices` with `coachToken`:

```json
{
  "title": "Holiday",
  "content": "The coaching will be closed on October 10.",
  "visibility": "PUBLIC"
}
```

### 14. Create a private notice for the student and parent

`POST {{baseUrl}}/api/v1/notices` with `coachToken`. `parentId` must belong to the selected student; use the ID from step 10/parent login:

```json
{
  "title": "Schedule change",
  "content": "Please arrive at 10:00 AM on Saturday.",
  "visibility": "PRIVATE",
  "recipientType": "BOTH",
  "studentId": {{studentId}},
  "parentId": {{parentId}},
  "expiresAt": "2026-10-30T23:59:00"
}
```

### 15. Verify student and parent views

All requests below have no body:

| Method and URL | Bearer token | Expected view |
| --- | --- | --- |
| `GET {{baseUrl}}/api/v1/attendance/me` | `studentToken` | Student's attendance and percentage |
| `GET {{baseUrl}}/api/v1/attendance/me` | `parentToken` | Linked student's attendance and percentage |
| `GET {{baseUrl}}/api/v1/marks/me` | `studentToken` | Student's marks and overall percentage |
| `GET {{baseUrl}}/api/v1/marks/me` | `parentToken` | Linked student's marks and overall percentage |
| `GET {{baseUrl}}/api/v1/notices/me` | `studentToken` | Public coaching notice and notices addressed to student |
| `GET {{baseUrl}}/api/v1/notices/me` | `parentToken` | Public coaching notice and notices addressed to parent |

### 16. Verify coaching views

All requests use `coachToken` and have no body except the marks POST already shown:

| Method and URL | Purpose |
| --- | --- |
| `GET {{baseUrl}}/api/v1/marks/students` | List student marks summaries |
| `GET {{baseUrl}}/api/v1/marks/students/{{studentId}}` | One student's marks |
| `GET {{baseUrl}}/api/v1/notices` | List notices created by this coaching |

### 17. Verify admin views

All requests use `adminToken` and have no body:

| Method and URL | Purpose |
| --- | --- |
| `GET {{baseUrl}}/api/v1/admin/coachings` | List approved coaching accounts |
| `GET {{baseUrl}}/api/v1/admin/students` | List student details only |
| `GET {{baseUrl}}/api/v1/admin/students/{{studentId}}` | View one student's details |

### Optional destructive checks

Do these only after the other checks, because they change persistent data:

- `DELETE {{baseUrl}}/api/v1/student/{{studentId}}` with `coachToken` removes that student; no body.
- `DELETE {{baseUrl}}/api/v1/admin/coaching/{{coachingId}}` with `adminToken` removes that coaching; no body.

Do not delete the coaching before finishing student/parent/attendance/marks/notice checks.

## Service-only routes

The student and parent services also expose internal routes such as `/student/register`, `/parent/register`, `/marks/me`, and `/notices/me`. The API Gateway does not route those paths directly. Frontends should use the `/api/v1/...` paths in this guide instead.

The local service-to-port mapping is:

| Service folder | Service role | Default port |
|---|---|---:|
| `Api-Gateway` | Client-facing gateway | 8080 |
| `Coaching` | Coaching accounts and API proxy | 8081 |
| `Admin` | Admin login, approvals, deletion, student details | 8084 |
| `Student` | Student accounts, attendance, marks, notices | 8082 |
| `Parent` | Parent accounts | 8083 |

Health checks are direct service URLs (the gateway does not proxy them):

| Service | Health URL |
|---|---|
| Coaching Service | `GET http://localhost:8081/actuator/health` |
| Admin Service | `GET http://localhost:8084/actuator/health` |
| Student service | `GET http://localhost:8082/actuator/health` |
| Parent service | `GET http://localhost:8083/actuator/health` |
