# CoachAssist backend account flows

For the complete API list with request bodies and examples, see [API_ENDPOINTS.md](API_ENDPOINTS.md).

## Endpoints

Call the API Gateway on port `8080` from the frontend. It forwards `/api/v1/admin/**` to the Admin Service. The rest of `/api/v1/**` goes to the Coaching Service, which forwards student and parent operations to their services.

| Purpose | Method and path | Access |
|---|---|---|
| Register coaching | `POST /api/v1/coaching/register` | Public |
| Coaching login | `POST /api/v1/coaching/login` | Public |
| Admin login | `POST /api/v1/admin/login` | Public; credentials are provisioned from environment on startup |
| Review coaching requests | `GET /api/v1/admin/coaching/requests` | Admin JWT |
| List all coachings | `GET /api/v1/admin/coachings` | Admin JWT |
| Approve a coaching request | `POST /api/v1/admin/coaching/{id}/approve` | Admin JWT |
| Delete a coaching account | `DELETE /api/v1/admin/coaching/{id}` | Admin JWT |
| List student details | `GET /api/v1/admin/students` | Admin JWT |
| View one student's details | `GET /api/v1/admin/students/{id}` | Admin JWT |
| Create student account | `POST /api/v1/student/create` | Coaching JWT |
| Student login | `POST /api/v1/student/login` | Public |
| Create parent account for a student | `POST /api/v1/parents/register` | Coaching JWT; student must belong to that coaching |
| Parent login | `POST /api/v1/parents/login` | Public |
| List parent recipients for a student | `GET /api/v1/parents/students/{studentId}/recipients` | Coaching JWT; returns safe parent identifiers and contact names |
| Remove a student | `DELETE /api/v1/student/{studentCode}` | Coaching JWT; limited to that coaching's students |
| Mark attendance | `POST /api/v1/attendance/students/{studentId}` | Coaching JWT; limited to that coaching's students |
| View own attendance and percentage | `GET /api/v1/attendance/me` | Student or linked-parent JWT |
| View marks for all own-coaching students | `GET /api/v1/marks/students` | Coaching JWT |
| View one student's marks | `GET /api/v1/marks/students/{studentId}` | Coaching JWT; limited to that coaching's students |
| Add an assessment result | `POST /api/v1/marks/students/{studentId}` | Coaching JWT; limited to that coaching's students |
| View own marks and percentage | `GET /api/v1/marks/me` | Student or linked-parent JWT |
| List coaching notices | `GET /api/v1/notices` | Coaching JWT |
| Create a notice | `POST /api/v1/notices` | Coaching JWT |
| View notices for current student/parent | `GET /api/v1/notices/me` | Student or linked-parent JWT |

All logins use `email` and `password`. Registration passwords must be 8–72 characters. Passwords are stored as BCrypt hashes. Successful logins return a signed bearer token and role. Send it on protected requests as `Authorization: Bearer <token>`.

Coaching registration creates a pending request and does not grant access. Admin approval enables the coaching login. Deleting a coaching removes its coaching account. It does not cascade-delete student or parent accounts. Admin student endpoints return only student identity and timestamps; they do not return password hashes or parent records.

Attendance requests accept `Present` or `Absent` and an ISO date (`YYYY-MM-DD`). A student can have one attendance entry per date; changing the mark for that date updates the existing entry. The percentage is calculated across all dates marked so far as `presentDays / totalDaysMarked * 100`, rounded to two decimals; it is `0` before any dates are marked. The response includes the counts and date-ordered records.

Marks are recorded as assessment entries with a name, subject, obtained marks, total marks, and assessment date. The overall percentage is `sum(obtained marks) / sum(total marks) * 100`, rounded to two decimal places. Students and linked parents can only view that student's marks.

Notices can be `PUBLIC` (visible to every student and linked parent in the coaching) or `PRIVATE` (targeted to one student, a particular linked parent account, or both). Private recipients are checked against the student and parent identities in their login tokens. An optional `expiresAt` hides the notice from student and parent views after that time. For `PARENT` or `BOTH`, include the selected parent's `parentId` (returned at parent login).

Example mark request:

```json
{
  "date": "2026-10-01",
  "status": "Present"
}
```

Example assessment result request:

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

Example private notice request:

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

A public notice uses `"visibility": "PUBLIC"` and omits `recipientType` and `studentId`.

### Request examples

Coaching registration:

```json
{
  "coachingName": "North Star Academy",
  "email": "admin@example.com",
  "coachingAddress": "12 Main Road",
  "coachingOwnerName": "Asha Rao",
  "password": "choose-a-long-password"
}
```

Student creation:

```json
{
  "firstName": "Riya",
  "surname": "Shah",
  "email": "riya@example.com",
  "password": "choose-a-long-password"
}
```

Parent account creation uses a student code returned from student creation:

```json
{
  "name": "Meera Shah",
  "studentId": 123,
  "email": "meera@example.com",
  "password": "choose-a-long-password"
}
```

Login request:

```json
{
  "email": "riya@example.com",
  "password": "choose-a-long-password"
}
```

## Runtime configuration

The services use PostgreSQL and share a JWT signing key. Set these environment variables in each service's launch environment:

- `DB_URL` (defaults to `jdbc:postgresql://localhost:5432/coachassist`)
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`: the same Base64-encoded random secret of at least 32 bytes for the Coaching, Student, Parent, and Admin services
- `JWT_EXPIRATION_MS` (defaults to one hour)
- `ADMIN_EMAIL` and `ADMIN_PASSWORD`: provision the first admin account in the Admin Service. The password is BCrypt-hashed in the database. Set both values; account provisioning does not overwrite an existing admin password.

The API Gateway binds to port `8080`; Coaching Service binds to `8081`; the student, parent, and admin services bind to `8082`, `8083`, and `8084`. The gateway accepts `COACHING_SERVICE_URL` and `ADMIN_SERVICE_URL`; Coaching and Admin Services accept `STUDENT_SERVICE_URL`, and Coaching Service also accepts `PARENT_SERVICE_URL`. Override the service ports with `GATEWAY_PORT`, `PORT`, `STUDENT_SERVICE_PORT`, `PARENT_SERVICE_PORT`, and `ADMIN_SERVICE_PORT` respectively.

The current default `JPA_DDL_AUTO` is `update` to create/update the project tables during local development. Set it to `validate` or use managed migrations for deployed environments.

Generate a suitable signing secret in PowerShell with:

```powershell
[Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
```

Set the generated value as `JWT_SECRET` in the launch environment for the Coaching, Student, Parent, and Admin services. Keep it out of source control.
