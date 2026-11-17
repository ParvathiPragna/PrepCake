# HLD MASTER NOTES - NFRs & Architecture Thinking

The biggest mistake in HLD interviews is treating NFRs as a checklist.

Strong candidates use NFRs to derive architecture.

Think:

```text
NFR
 ↓
Architecture Constraints
 ↓
Technology Decisions
```

---

# HLD Flow

Always start:

```text
Functional Requirements
        ↓
Non Functional Requirements
        ↓
Scale Estimation
        ↓
API Design
        ↓
Data Model
        ↓
Core Components
        ↓
Hardest Problem
        ↓
Scalability
        ↓
Reliability
        ↓
Observability
```

---

# NFR Framework

Every NFR usually impacts one of:

```text
Availability

Latency

Consistency

Throughput

Durability

Scalability

Reliability

Observability

Security
```

---

# Availability

Question:

```text
How often can the system be down?
```

---

## Availability Targets

### 99%

```text
3.65 days downtime/year
```

Architecture:

```text
Single region usually acceptable
```

---

### 99.9%

```text
8.7 hours downtime/year
```

Architecture:

```text
Load Balancer

Multiple App Instances

Basic Failover
```

---

### 99.99%

```text
~52 minutes downtime/year
```

Architecture:

```text
Multi-AZ

Primary + Replica DB

Auto Healing

Rolling Deployments
```

---

### 99.999%

```text
~5 minutes downtime/year
```

Architecture:

```text
Multi Region

Active Active

Cross Region Replication

Disaster Recovery
```

---

# Availability Drives

When interviewer says:

```text
99.99% Availability
```

Think:

```text
How many copies of my system must exist?
```

Not:

```text
How many servers?
```

---

# Latency

Question:

```text
How fast should the response be?
```

Latency largely determines:

```text
Cache vs DB

Sync vs Async

CDN vs Origin
```

---

# Latency Budget Thinking

Suppose:

```text
P99 < 100 ms
```

Possible budget:

```text
Network           20ms

App Processing    10ms

DB Query          50ms

Buffer            20ms
```

DB reads are fine.

---

Suppose:

```text
P99 < 20 ms
```

Budget:

```text
Network           10ms

App Processing     5ms

Storage            5ms
```

Now:

```text
Need Redis

Need Cache

Need CDN
```

---

# Quick Latency Cheatsheet

## <10ms

Usually:

```text
In-memory

Redis

Local Cache
```

---

## 10-50ms

Usually:

```text
Redis

Read Replicas

Optimized Queries
```

---

## 50-200ms

Usually:

```text
Primary DB

Read Replica

ElasticSearch
```

---

## >1 sec

Usually:

```text
Async Processing

Kafka

Background Jobs
```

---

# Latency Drives

When interviewer says:

```text
100ms response time
```

Think:

```text
Where should data come from?
```

---

# Consistency

Question:

```text
Can stale data be tolerated?
```

---

## Strong Consistency

Examples:

```text
Seat Booking

Payment

Inventory
```

Need:

```text
DB

Transactions

Locks
```

Correctness > Latency.

---

## Eventual Consistency

Examples:

```text
Notification

Analytics

Recommendation
```

Can tolerate:

```text
Small Delay
```

Need:

```text
Kafka

Async Processing
```

---

# Consistency Drives

Ask:

```text
Who is source of truth?
```

---

# Throughput

Question:

```text
How much traffic?
```

---

Examples:

```text
100 req/sec
```

Simple architecture.

---

```text
100K req/sec
```

Need:

```text
Partitioning

Kafka

Caching

Horizontal Scale
```

---

# Throughput Drives

Ask:

```text
Do we need Kafka?

Do we need partitioning?
```

---

# Durability

Question:

```text
Can data be lost?
```

---

Low Durability Example:

```text
User Presence

Typing Indicator
```

No issue.

---

High Durability Example:

```text
Payments

Orders

Documents
```

Need:

```text
Persistent Storage

Replication

Replay
```

---

# Durability Drives

Ask:

```text
Can I recover the state after a crash?
```

---

# Scalability

Question:

```text
Can traffic grow 10x?
```

---

Techniques:

```text
Horizontal Scaling

Sharding

Partitioning

Caching
```

---

# Reliability

Question:

```text
What happens when something fails?
```

---

Always discuss:

```text
Retry

Timeout

Circuit Breaker

DLQ

Fallback
```

---

# Observability

Question:

```text
How do we know the system is healthy?
```

---

Must Mention

```text
Metrics

Logs

Tracing

Alerting
```

---

Golden Metrics

```text
Latency

Traffic

Errors

Saturation
```

---

# Architecture Decisions From NFRs

## Requirement

```text
99.99 Availability
```

Implies:

```text
Multiple Instances

Multi AZ

Replica DB
```

---

## Requirement

```text
Latency < 50ms
```

Implies:

```text
Redis

Read Replica

CDN
```

---

## Requirement

```text
No Data Loss
```

Implies:

```text
Replication

Kafka

Persistent Storage
```

---

## Requirement

```text
100K Messages/sec
```

Implies:

```text
Kafka

Partitioning

Horizontal Scaling
```

---

## Requirement

```text
Strong Consistency
```

Implies:

```text
Transactional DB

Locks

Synchronous Flow
```

---

# Senior SWE Interview Answer Pattern

Instead of saying:

```text
Latency < 100 ms

Availability 99.99%
```

Say:

```text
Given the 99.99% availability target, I would
use multiple stateless instances behind a load
balancer and a multi-AZ database deployment.

Given the sub-100ms latency target, reads will
likely need to come from Redis or read replicas
rather than directly from heavily loaded primary
databases.
```

This is the difference between:

```text
Reciting NFRs
```

and

```text
Using NFRs to drive architecture.
```

---

# Mental Cheat Sheet

Availability
↓

```text
How many copies of system?
```

---

Latency
↓

```text
Where should data come from?
```

---

Consistency
↓

```text
Who is source of truth?
```

---

Throughput
↓

```text
Do I need Kafka/partitioning?
```

---

Durability
↓

```text
How do I recover after crash?
```

---

Reliability
↓

```text
What happens when dependencies fail?
```

---

Observability
↓

```text
How do I debug production?
```

---

# Final Senior SWE Heart

Every HLD eventually becomes answering these 6 questions:

```text
1. Where is data stored?

2. Where is data cached?

3. How is data partitioned?

4. How is data recovered?

5. How is data ordered?

6. What happens when things fail?
```

If you answer those six convincingly, most Senior SWE HLD interviews go very well.