# resend-mail-server

A minimal Spring Boot server whose only purpose is sending email through
[Resend](https://resend.com). No database, no frontend, no user system,
no JWT/OAuth.

## What it does

Exposes exactly one endpoint, `POST /api/mail/send`, which:

1. Compares the `secret` field in the request against the server-side
   `MAIL_SECRET` environment variable.
2. Returns `401` if the secret is invalid.
3. Otherwise sends an email through the Resend API, using `content` as
   the HTML body and `MAIL_FROM` as the sender.
4. Returns a simple JSON success/failure response. Resend API keys,
   secrets, and internal exception details are never returned to the client.

## Requirements

- Java 17+
- Maven 3.9+
- A Resend account and API key

## Environment variables

| Variable         | Description                                    |
|-------------------|--------------------------------------------------|
| `RESEND_API_KEY`  | Your Resend API key                              |
| `MAIL_SECRET`     | Shared secret that callers must send in `secret` |
| `MAIL_FROM`       | Sender address used for every outgoing email     |

Set them before running, for example:

```bash
export RESEND_API_KEY=re_xxxxxxxxxxxxxxxxxxxxxxxx
export MAIL_SECRET=some-long-random-secret
export MAIL_FROM="Your App <notifications@yourdomain.com>"
```

## Running with Maven

```bash
mvn clean package
mvn spring-boot:run
```

The server starts on `http://localhost:8080`.

## Example request

```bash
curl -X POST http://localhost:8080/api/mail/send \
  -H "Content-Type: application/json" \
  -d '{
    "secret": "some-long-random-secret",
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

**Invalid secret** — `401 Unauthorized`

```json
{
  "success": false,
  "error": "Invalid secret"
}
```

**Invalid request data** (missing/blank field, invalid email) — `400 Bad Request`

```json
{
  "success": false,
  "error": "Invalid request data"
}
```

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

- Invalid secret returns `401`
- A valid request reaches `MailService.sendEmail(...)`
- Invalid request data returns `400`
- `MailService`'s secret comparison logic (valid / invalid / null secret)

## Project structure

```text
resend-mail-server/
├── pom.xml
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
        ├── controller/MailControllerTest.java
        └── service/MailServiceTest.java
```
