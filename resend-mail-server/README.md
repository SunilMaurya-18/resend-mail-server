# resend-mail-server

A minimal Spring Boot server whose only purpose is sending email through
[Resend](https://resend.com). No database, no frontend, no user system,
no JWT/OAuth.

## What it does

Exposes exactly one endpoint, `POST /api/mail/send`, which:

1. Sends an email through the Resend API, using `content` as the HTML
   body and `MAIL_FROM` as the sender.
2. Returns a simple JSON success/failure response. Resend API keys and
   internal exception details are never returned to the client.

The endpoint requires no caller authentication - no `Authorization`
header, no shared secret. Restrict access at the network/proxy layer if
you need it.

## Requirements

- Java 17+
- Maven 3.9+
- A Resend account and API key

## Environment variables

| Variable         | Description                                    |
|-------------------|--------------------------------------------------|
| `RESEND_API_KEY`  | Your Resend API key                              |
| `MAIL_FROM`       | Sender address used for every outgoing email     |

Set them before running, for example:

```bash
export RESEND_API_KEY=re_xxxxxxxxxxxxxxxxxxxxxxxx
export MAIL_FROM="Your App <notifications@yourdomain.com>"
```

## Running with Maven

```bash
mvn clean package
mvn spring-boot:run
```

The server starts on `http://localhost:8080`.

## Deploying to Render

The included `Dockerfile` is a multi-stage build (Maven build stage, slim
JRE runtime stage) ready for Render's Docker deployment.

1. Push this project to a Git repository.
2. In Render, create a **New Web Service** → **Build and deploy from a
   Dockerfile**, pointing at this repo (Dockerfile at the repo root).
3. Under **Environment**, add:
   - `RESEND_API_KEY`
   - `MAIL_FROM`
4. Render automatically injects `PORT`; `application.properties` already
   binds to it (`server.port=${PORT:8080}`), so no extra config is needed.
5. Deploy. `GET /` and `GET /health` both return `{"status":"ok"}`, so
   Render's default root-path health check works as-is.

To build and run the image locally first:

```bash
docker build -t resend-mail-server .
docker run -p 8080:8080 \
  -e RESEND_API_KEY=re_xxxxxxxxxxxxxxxxxxxxxxxx \
  -e MAIL_FROM="Your App <notifications@yourdomain.com>" \
  resend-mail-server
```

## Example request

```bash
curl -X POST http://localhost:8080/api/mail/send \
  -H "Content-Type: application/json" \
  -d '{
    "to": "receiver@example.com",
    "subject": "Test email",
    "content": "<h1>Hello</h1><p>This is a test email.</p>"
  }'
```

## Example responses

**Success** — `200 OK`

```json
{
  "success": true
}
```

**Invalid request data** (missing/blank field, invalid email) — `400 Bad Request`

```json
{
  "success": false,
  "error": "Invalid request data"
}
```

**Unknown path** — `404`, **wrong method** — `405`, **wrong content type** —
`415`. Every response is JSON; the HTML whitelabel error page is disabled and
the `Accept` header is ignored.

**Resend rejected or could not be reached** — `502 Bad Gateway`

```json
{
  "success": false,
  "error": "Failed to send email"
}
```

## Tests

```bash
mvn test
```

Tests mock `MailService`, so they run without a real `RESEND_API_KEY` and
never make a real network call. They cover:

- A valid request reaches `MailService.sendEmail(...)`
- Invalid request data returns `400`

## Project structure

```text
resend-mail-server/
├── pom.xml
├── Dockerfile
├── .dockerignore
├── README.md
└── src/
    ├── main/
    │   ├── java/com/example/mail/
    │   │   ├── MailApplication.java
    │   │   ├── controller/MailController.java
    │   │   ├── dto/SendMailRequest.java
    │   │   ├── dto/MailResponse.java
    │   │   ├── exception/GlobalExceptionHandler.java
    │   │   └── service/MailService.java
    │   └── resources/application.properties
    └── test/java/com/example/mail/
        └── controller/MailControllerTest.java
```
