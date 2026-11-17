# Auth, Authentication, Authorization & IAM - Senior SWE Revision Sheet

---

# Big Picture

Every secure system answers 3 questions:

```text
Who are you?

(Authentication)

What are you allowed to do?

(Authorization)

Who can access what resources?

(IAM)
```

---

# Authentication (AuthN)

## Definition

Authentication verifies identity.

Question:

```text
Who are you?
```

Examples:

```text
Username + Password

OTP

Biometrics

Google Login

Microsoft Login
```

---

## Flow

```text
User

↓

Credentials

↓

Authentication Service

↓

Verified Identity
```

---

## Examples

```text
Login

SSO

OAuth Login

LDAP
```

---

## Pros

✅ Prevents unauthorized access

✅ Foundation for all security

✅ Integrates with SSO providers

---

## Cons

❌ Credential management

❌ Password leaks

❌ Session management complexity

---

## Interview Heart

```text
Authentication

=

Identity Verification
```

---

# Authorization (AuthZ)

## Definition

Authorization determines permissions.

Question:

```text
What can you do?
```

---

## Examples

User can:

```text
Read Document
```

but cannot:

```text
Delete Document
```

---

## Flow

```text
Authenticated User

↓

Permission Check

↓

Allow / Deny
```

---

## Pros

✅ Fine-grained access control

✅ Supports least privilege

✅ Protects resources

---

## Cons

❌ Complex permission models

❌ Hard auditing at scale

---

## Interview Heart

```text
Authentication

Who are you?

Authorization

What can you do?
```

---

# Session-Based Authentication

Traditional approach.

---

## Flow

```text
Login

↓

Server Creates Session

↓

Session ID

↓

Browser Cookie

↓

Future Requests
```

---

## Pros

✅ Easy revocation

✅ Simple concept

✅ Mature

---

## Cons

❌ Session storage needed

❌ Sticky session concerns

❌ Harder horizontal scaling
```

---

# JWT (JSON Web Token)

Modern stateless authentication.

---

## Flow

```text
Login

↓

JWT Generated

↓

Client Stores JWT

↓

JWT Sent With Requests

↓

Service Validates JWT
```

---

## Example

```text
Header

Payload

Signature
```

---

## Payload

```json
{
  "userId":"123",
  "role":"ADMIN"
}
```

---

## Pros

✅ Stateless

✅ Easy microservice support

✅ No session storage

✅ Good scalability

---

## Cons

❌ Hard revocation

❌ Token leakage risk

❌ Large payloads

---

## Interview Heart

```text
JWT

=

Stateless Authentication
```

---

# OAuth2

## Definition

Delegated Authorization Framework.

Allows:

```text
Login with Google

Login with GitHub

Login with Microsoft
```

without sharing passwords.

---

## Actors

```text
Resource Owner

Client App

Authorization Server

Resource Server
```

---

## Example

```text
User

↓

Google Login

↓

Authorization Code

↓

Access Token
```

---

## Pros

✅ Industry standard

✅ Third-party access

✅ Secure delegation

---

## Cons

❌ Complex flows

❌ Many grant types

❌ Token management

---

## Interview Heart

```text
OAuth2

=

Delegated Authorization
```

---

# OIDC (OpenID Connect)

## Definition

Identity Layer built on top of OAuth2.

Adds:

```text
Authentication
```

capabilities.

---

OAuth2 answers:

```text
Can this app access the resource?
```

OIDC answers:

```text
Who is the user?
```

---

## Examples

```text
Google Sign-In

Azure AD Login

Okta Login
```

---

## Pros

✅ Standardized user identity

✅ Widely adopted

✅ Works with SSO

---

## Cons

❌ Additional protocol complexity

---

## Interview Heart

```text
OAuth2

Authorization

OIDC

Authentication
```

---

# RBAC (Role Based Access Control)

## Definition

Users receive permissions through roles.

---

Example

```text
Admin

Editor

Viewer
```

---

Instead of:

```text
User -> Permission
```

Use:

```text
User -> Role -> Permissions
```

---

## Pros

✅ Simple

✅ Easy scaling

✅ Common enterprise model

---

## Cons

❌ Exploding number of roles

❌ Complex exceptions

---

## Interview Heart

```text
RBAC

User
 ↓
Role
 ↓
Permission
```

---

# ABAC (Attribute Based Access Control)

Access based on attributes.

---

Example

```text
Department

Location

Time

Classification
```

---

Rule

```text
Department=Finance

can access

Finance Reports
```

---

## Pros

✅ Very flexible

✅ Fine-grained security

---

## Cons

❌ More complex

❌ Harder troubleshooting

---

# IAM (Identity & Access Management)

## Definition

Centralized system for:

```text
Users

Roles

Policies

Permissions

Auditing
```

---

Think:

```text
Who can access what resource?
```

---

Examples

```text
AWS IAM

Azure IAM

GCP IAM
```

---

## Responsibilities

```text
Authentication

Authorization

Role Management

Policy Management

Auditing
```

---

## Interview Heart

```text
IAM

=

Centralized Access Management
```

---

# IAM Components

---

## User

```text
Human

Application

Service
```

---

## Role

Collection of permissions.

Example:

```text
Admin

Developer

Viewer
```

---

## Policy

Actual rules.

Example:

```text
Read S3

Write DynamoDB
```

---

# Service Accounts

## Definition

Machine identity.

Used by:

```text
Pods

Services

Applications
```

instead of humans.

---

Example

```text
Notification Service

↓

Access S3
```

using:

```text
IAM Role
```

not username/password.

---

## Pros

✅ No hardcoded secrets

✅ Rotatable credentials

✅ Least privilege

---

## Interview Heart

```text
Humans

Use Users

Machines

Use Service Accounts
```

---

# Secrets Management

Never store:

```text
Passwords

API Keys

DB Credentials
```

inside code.

---

Use:

```text
K8s Secrets

AWS Secrets Manager

Vault
```

---

## Pros

✅ Rotation

✅ Central management

✅ Better security

---

## Cons

❌ Operational overhead

---

# mTLS (Mutual TLS)

Normal TLS:

```text
Client validates Server
```

---

mTLS:

```text
Client validates Server

Server validates Client
```

---

Very common in:

```text
Microservices

Service Mesh

Zero Trust
```

---

## Pros

✅ Strong service identity

✅ Encryption

✅ Mutual authentication

---

## Cons

❌ Certificate management

❌ Operational complexity

---

## Interview Heart

```text
TLS

Server Auth

mTLS

Server + Client Auth
```

---

# API Gateway Security

Responsibilities:

```text
Authentication

JWT Validation

Rate Limiting

IP Filtering

Routing
```

---

Flow

```text
Client

↓

API Gateway

↓

Backend Services
```

---

# Least Privilege Principle

Always grant:

```text
Minimum Required Permissions
```

Example:

Bad

```text
Admin Access
```

Good

```text
Read Only Access
```

if that's all that's needed.

---

# Security Interview Cheatsheet

## Authentication

```text
Who are you?
```

---

## Authorization

```text
What can you do?
```

---

## OAuth2

```text
Delegated Authorization
```

---

## OIDC

```text
Identity Layer on OAuth2
```

---

## JWT

```text
Stateless Authentication
```

---

## RBAC

```text
Role Based Access
```

---

## IAM

```text
Centralized Identity & Permissions
```

---

## Service Account

```text
Machine Identity
```

---

## mTLS

```text
Mutual Authentication
Between Services
```

---

## Secrets Management

```text
Never hardcode credentials
```

---

# Senior SWE 30-Second Answer

```text
Authentication verifies identity, while authorization determines what
actions a user or service can perform. OAuth2 is commonly used for
delegated authorization, and OIDC extends OAuth2 to provide user identity.
JWTs enable stateless authentication between clients and services.

IAM centralizes identity, roles, and permission management. RBAC is
commonly used to grant permissions through roles, while service accounts
provide identities for applications and pods. Secrets should be managed
through dedicated secret stores, and mTLS is often used to secure
service-to-service communication in microservice environments.
```