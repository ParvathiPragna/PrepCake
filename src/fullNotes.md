Senior SWE Interview Handbook (Markdown)
Focus Areas: PostgreSQL, Kafka, Kubernetes, Observability, Auth/IAM, Spring/JPA, AWS Platform Engineering, HLD, Production Debugging, Distributed Systems
PostgreSQL
Core Concepts
MVCC (Multi-Version Concurrency Control)
Allows readers and writers to operate concurrently.
Plain Text
Transaction A reads old version.
 
Transaction B updates row.
 
Transaction A continues seeing old snapshot.
Heart:
Plain Text
MVCC = Snapshot Isolation
HOT Update
Heap Only Tuple.
Applicable when:
Plain Text
Indexed Columns Unchanged
Example:
SQL
UPDATE users
SET last_login = now();
Benefits:
Plain Text
No index updates
 
Lower write amplification
 
Faster updates
Vacuum
Responsible for:
Plain Text
Dead tuple cleanup
 
Space reuse
 
Transaction ID management
Problems:
Plain Text
Table Bloat
 
Wraparound
Indexing
Common Types:
Plain Text
BTree
GIN
GiST
BRIN
Interview Focus:
Plain Text
Composite Indexes
 
Column Order
 
Selectivity
Interview Hearts
Plain Text
MVCC
 
↓
 
Snapshot Visibility
Plain Text
HOT Update
 
↓
 
No Index Update
Plain Text
Vacuum
 
↓
 
Dead Tuple Cleanup
Common Traps
Plain Text
Serializable != Locks Everything
 
Index Exists != Index Used
 
More Indexes != Better Performance
Production Debugging
Check:
Plain Text
Slow Queries
 
Missing Indexes
 
Connection Saturation
 
Replication Lag
Kafka
Ordering
Guaranteed:
Plain Text
Within Partition
Not guaranteed:
Plain Text
Across Partitions
Partition Key
Business Decision.
Good examples:
Plain Text
chatId
 
documentId
 
orderId
 
paymentId
Producer Idempotency
Uses:
Plain Text
Producer ID
 
+
Sequence Number
Benefits:
Plain Text
No duplicate writes during retries
Kafka Transactions
Provides:
Plain Text
Atomic Multi-Topic Writes
 
Atomic Offset Commit
EOS Truth
Plain Text
Kafka EOS
 
!=
 
Business EOS
You still need:
Plain Text
Idempotency Key
 
Unique Constraint
 
Dedup Logic
Interview Hearts
Plain Text
Ordering
=
Partition
Plain Text
Partition Count
=
Parallelism Ceiling
Plain Text
Consumer Lag
=
Usually Consumer Problem
Production Debugging
Growing Lag?
Check:
Plain Text
DB Latency
 
External API Latency
 
GC
 
Rebalancing
 
Slow Consumers
Kubernetes
Requests vs Limits
Requests:
Plain Text
Scheduling
Limits:
Plain Text
Runtime Enforcement
Readiness
Question:
Plain Text
Can Receive Traffic?
Failure:
Plain Text
Removed From Service
Liveness
Question:
Plain Text
Should Restart?
Failure:
Plain Text
Pod Restarted
Deployment
Used For:
Plain Text
Stateless Services
Examples:
Plain Text
API
 
Workers
 
Notification Service
StatefulSet
Used For:
Plain Text
Kafka
 
Mongo
 
Postgres
Provides:
Plain Text
Stable Identity
 
Stable Hostname
 
Stable Storage
Interview Hearts
Plain Text
Readiness
 
!=
 
Liveness
Plain Text
Running Pod
 
!=
 
Healthy App
Production Debugging
Check:
Plain Text
OOMKill
 
CrashLoopBackOff
 
Node Pressure
 
Readiness Probe
Observability
Three Pillars
Metrics
Tell me:
Plain Text
Something is wrong.
Logs
Tell me:
Plain Text
What happened.
Traces
Tell me:
Plain Text
Where it happened.
Prometheus
Role:
Plain Text
Metrics Collection
 
Metrics Storage
Grafana
Role:
Plain Text
Visualization
 
Dashboards
OpenTelemetry
Role:
Plain Text
Tracing
 
Instrumentation
Concepts:
Plain Text
Trace
 
Span
Fluent Bit
Role:
Plain Text
Log Collection
 
Log Shipping
Golden Signals
Plain Text
Latency
 
Traffic
 
Errors
 
Saturation
Production Flow
Plain Text
Grafana
 
↓
 
Prometheus
 
↓
 
OpenTelemetry
 
↓
 
Logs
Authentication & IAM
Authentication
Question:
Plain Text
Who are you?
Authorization
Question:
Plain Text
What can you do?
JWT
Provides:
Plain Text
Stateless Authentication
Contains:
Plain Text
Claims
 
Roles
 
Scopes
OAuth2
Purpose:
Plain Text
Delegated Authorization
OIDC
Purpose:
Plain Text
Identity Layer on OAuth2
IAM
Manages:
Plain Text
Users
 
Roles
 
Policies
 
Permissions
Service Accounts
Used For:
Plain Text
Machine Identity
mTLS
Normal TLS:
Plain Text
Client Trusts Server
mTLS:
Plain Text
Client Trusts Server
 
+
Server Trusts Client
Interview Hearts
Plain Text
OAuth2
 
Authorization
Plain Text
OIDC
 
Authentication
Plain Text
IAM Roles
 
>
Access Keys
Spring & JPA
Dirty Checking
Managed Entity Modified:
Java
user.setName("ABC");
Update generated automatically.
No save() required.
Managed vs Detached
Managed:
Plain Text
Dirty checking active
Detached:
Plain Text
Dirty checking inactive
Flush
Plain Text
Send SQL
Commit
Plain Text
Persist Transaction
Heart
Plain Text
Flush
 
!=
 
Commit
N+1 Problem
Example:
Java
orders
 
↓
 
customer
Result:
Plain Text
1 Query
 
+
N Queries
Solutions:
Plain Text
JOIN FETCH
 
@EntityGraph
Optimistic Locking
Uses:
Java
@Version
Prevents:
Plain Text
Lost Updates
Without DB Locks.
Common Traps
Plain Text
Self-invoked @Transactional
 
LazyInitializationException
 
Flush != Commit
 
save() != Commit
AWS Platform Engineering
IAM Roles
Preferred Over:
Plain Text
Access Key
 
Secret Key
STS
Provides:
Plain Text
Temporary Credentials
AssumeRole
Used For:
Plain Text
Cross Account Access
Idempotency
Prevent:
Plain Text
Duplicate Resource Creation
Store:
Plain Text
requestId
Retry Strategy
Always:
Plain Text
Retry
 
↓
 
Backoff
 
↓
 
Jitter
Never:
Plain Text
Immediate Infinite Retry
Reconciliation
Heart:
Plain Text
Desired State
 
!=
 
Actual State
Example:
Plain Text
Mongo says ACTIVE
 
AWS says FAILED
Synchronizer reconciles.
High-Level Design Hearts
Google Docs
Plain Text
Session Ownership
 
↓
 
Deterministic Ordering
 
↓
 
OT
Chat System
Plain Text
chatId
 
↓
 
Partition
Ordering:
Plain Text
Within Conversation
Notification System
Plain Text
Request
 
↓
 
Channel Fanout
 
↓
 
Email
SMS
Push
Features:
Plain Text
Retry
 
DLQ
 
Channel Isolation
Analytics Platform
Plain Text
Raw Events
 
↓
 
Aggregation
 
↓
 
Dashboard Store
Dashboard:
Plain Text
Read Aggregates
 
Not Raw Events
Distributed Scheduler
Plain Text
Claim
 
↓
 
Lease
 
↓
 
Heartbeat
 
↓
 
Reclaim
 
↓
 
Fencing Token
Atomic Claim
SQL
FOR UPDATE SKIP LOCKED
Lease Expiry
Allows:
Plain Text
Worker Crash Recovery
Fencing Token
Prevents:
Plain Text
Stale Worker Updates
Production Debugging Handbook
Incident Rule #1
Plain Text
Deployment
 
=
 
Suspect #1
Especially if incident begins shortly after rollout.
High Latency + Low CPU
Possible Causes:
Plain Text
Thread Pool Exhaustion
 
DB Pool Exhaustion
 
External Dependency
 
Slow Query
 
Cache Miss Storm
Dashboard Investigation Order
Plain Text
Metrics
 
↓
 
Recent Deployment
 
↓
 
Dependency Health
 
↓
 
Tracing
 
↓
 
Logs
 
↓
 
Thread Dump
JVM Checklist
Check:
Plain Text
Heap
 
Metaspace
 
Direct Memory
 
Thread Stacks
 
GC
Connection Pool Checklist
Check:
Plain Text
Active Connections
 
Waiting Threads
 
Slow Queries
 
Connection Leaks
Final 5-Minute Revision
PostgreSQL
Plain Text
MVCC
 
HOT
 
Vacuum
 
Indexes
Kafka
Plain Text
Ordering
 
Partitions
 
Consumer Lag
 
EOS != Business EOS
Kubernetes
Plain Text
Requests
 
Limits
 
Readiness
 
Liveness
 
StatefulSet
Observability
Plain Text
Metrics
 
Logs
 
Traces
 
Golden Signals
JPA
Plain Text
Dirty Checking
 
Flush != Commit
 
N+1
 
Optimistic Locking
AWS
Plain Text
IAM Roles
 
STS
 
AssumeRole
 
Idempotency
 
Reconciliation
HLD Hearts
Plain Text
Google Docs -> Session Ownership
 
Chat -> Conversation Ownership
 
Notifications -> Retry + DLQ
 
Analytics -> Aggregation
 
Scheduler -> Lease + Fencing
Senior SWE Mindset
Always answer:
Plain Text
How does it scale?
 
How does it fail?
 
How does it recover?
 
How do I observe it?
 
How do I prevent duplicates?
 
How do I debug it at 3 AM?