# Senior SWE HLD Revision Notes
# Google Docs + Slack/Teams Chat

---

# 1. Google Docs / Real-Time Collaborative Document Editor

## Functional Requirements

- Create document
- Read document
- Edit document
- Delete document
- Share document
- Permission management
- Concurrent editing
- Auto-save
- Version history
- Presence (who is editing)
- Cursor visibility

---

## Non Functional Requirements

- High availability
- Low sync latency (<200ms ideal)
- No data loss
- Strong consistency within a document session
- Horizontal scalability
- Recovery after crashes

---

# Capacity Thinking

Important realization:

```text
WRITE != Save Document

WRITE == Edit Operation
```

Example:

```text
1M DAU

500 keystrokes/day/user

500M edits/day

~5.7K write ops/sec average
```

Peak can be much higher.

---

# Core Services

```text
Client

    |
    v

Collaboration Service

    |
    +-----------+
    |           |
    v           v

Redis       Kafka

    |           |
    |           v

    |      Persistence
    |         Service

    |           |
    |           |
    v           v

Presence    PostgreSQL

                |
                v

             S3/Blob
```

---

# Heart #1 : Session Ownership

Most Important Concept

```text
Document 123

       |
       v

      CS2
```

Every document gets:

```text
Single Collaboration Owner
```

Benefits:

- Single ordering point
- Easier OT implementation
- No cross-node synchronization
- Easier conflict resolution

---

Without Session Ownership

```text
UserA -> CS1

UserB -> CS3
```

Both nodes modify document.

Very difficult.

---

With Session Ownership

```text
UserA -> CS1 -> CS2

UserB -> CS3 -> CS2
```

All edits processed in:

```text
Same Collaboration Session
```

---

# Heart #2 : Storage vs Playground

Many candidates confuse this.

S3 is NOT the editor.

---

## S3

Stores:

```text
Snapshots
Versions
Exports
```

Examples:

```text
doc-v1
doc-v2
doc-v3
```

---

## Collaboration Service

Stores:

```text
Current Active Document
```

This is the playground.

---

## Redis

Stores:

```text
Presence
Cursor Positions
Session Ownership
Heartbeats
```

Not document durability.

---

# Heart #3 : OT vs CRDT

## OT

Choose for:

```text
Google Docs
Centralized architecture
Mostly online users
```

Benefits:

```text
Small metadata

Low memory

Central coordination fits naturally
```

---

## CRDT

Choose for:

```text
Offline-first

P2P systems

Multi-region active-active
```

Benefits:

```text
Automatic convergence

Works disconnected
```

Cost:

```text
Higher storage

More metadata

More complexity
```

---

Interview Answer

```text
Google Docs -> OT

Offline-first editor -> CRDT
```

---

# Heart #4 : Edit Event != Version

Wrong

```text
Every Keystroke

= New Version
```

Correct

```text
Every Keystroke

= Edit Event
```

Example

```text
Version 100

Edit 101
Edit 102
Edit 103
Edit 104

Version 101
```

---

# Auto-Save

Not a client API.

Internal process.

Trigger:

```text
Every 30 seconds
```

or

```text
Every 5000 edits
```

---

Flow

```text
Current State

      |

      v

Snapshot Generator

      |

      v

S3
```

---

# Heart #5 : Kafka Recovery

Every edit becomes:

```text
Event
```

Example

```text
Insert A

Delete B

Insert C
```

Stored in Kafka.

---

Recovery

```text
Load Snapshot

      +

Replay Kafka Events

      =

Latest State
```

---

# Edit Flow (Most Important Flow)

```text
User Types Character

       |

WebSocket

       |

Session Owner

       |

Apply OT

       |

Assign Sequence

       |

Publish Kafka Event

       |

Broadcast To Editors

       |

Update Presence Data

       |

Periodic Snapshot
```

---

# Presence Service

Stores

```text
User Online/Offline

Cursor Position

Current Session

Heartbeat
```

Example:

```text
doc123

userA -> line 10
userB -> line 25
userC -> line 40
```

Redis key:

```text
documentId
```

---

# Metadata Tables

## Document

```text
document_id
owner_id
title
latest_version
created_at
```

---

## Permission

```text
document_id
user_id
permission
```

---

## Version

```text
version_id
document_id
snapshot_url
created_at
```

---

## Edit Event

```text
event_id
document_id
position
operation
user_id
sequence_no
```

---

# Typical Interview Traps

### Why not update S3 directly?

```text
High latency

No real-time collaboration
```

---

### Why not store full document every keystroke?

```text
Storage explosion
```

---

### Why Kafka?

```text
Durability

Replay

Event sourcing
```

---

### Why session ownership?

```text
Deterministic ordering
```

---

---

# 2. Slack / Teams Chat System

---

# Functional Requirements

- 1:1 chat
- Group chat
- Channels
- Online presence
- Read receipts
- Message history
- Message delivery
- Offline message sync

---

# Non Functional Requirements

- High availability
- Message durability
- Low latency
- Message ordering
- Horizontal scaling

---

# High Level Design

```text
Client

   |

WebSocket

   |

Chat Service

   |

Kafka

   |

Message Service

   |

PostgreSQL/Cassandra

   |

Object Storage
```

---

# Heart #1 : Ordering

Most important concept.

DO NOT say:

```text
Global Clock
```

Reason:

```text
Clock Drift

Network Delays
```

No strict guarantee.

---

Correct Solution

```text
Conversation Ordering
```

---

Example

```text
chat123

  |

Kafka Partition 20
```

Partition key:

```text
chatId
```

---

Benefits

```text
msg1

msg2

msg3
```

Ordering guaranteed.

---

Interview Answer

```text
Ordering is guaranteed
within a conversation,
not globally.
```

---

# Heart #2 : Conversation Ownership

Equivalent of:

```text
Session Ownership
```

from Google Docs.

---

Example

```text
chat123

      |

Kafka Partition 10
```

All messages for:

```text
chat123
```

flow through same partition.

---

# Fan-Out Models

---

## Fan-Out-On-Write

One message replicated for every user.

Good for:

```text
1:1 chats

small groups
```

---

Example

```text
10 users

1 message

10 writes
```

Acceptable.

---

## Fan-Out-On-Read

Store message once.

Readers pull later.

Good for:

```text
100K members

1M members
```

channels.

---

Example

```text
Announcement Channel
```

Do not create:

```text
1M writes
```

per message.

---

# Heart #3 : Hybrid Strategy

Interview Favourite

```text
Small Groups
     ↓
Fan-Out-On-Write

Large Channels
     ↓
Fan-Out-On-Read
```

---

# Presence Service

Stores:

```text
Online

Offline

Last Seen

WebSocket Session
```

NOT responsible for:

```text
Message Delivery
```

---

# Read Receipts

Store

```text
user_id

channel_id

last_read_message_id
```

Example

```text
last_read = 105
```

Unread:

```text
106 onwards
```

---

# Delivery Flow

```text
Sender

   |

WebSocket

   |

Chat Service

   |

Kafka

   |

Message Persistence

   |

Receiver WebSocket
```

---

# Offline User

Message still written to:

```text
Kafka

DB
```

When user reconnects:

```text
Fetch messages

where

messageId > lastRead
```

---

# Data Model

## Conversation

```text
conversation_id
type
created_at
```

---

## Message

```text
message_id
conversation_id
sender_id
payload
created_at
sequence_no
```

---

## Membership

```text
conversation_id
user_id
```

---

# Typical Interview Traps

### Why not global sequence?

```text
Too much contention

Global bottleneck
```

---

### Why not timestamps?

```text
Clock drift

Out-of-order arrivals
```

---

### Why Kafka?

```text
Ordering

Durability

Replay
```

---

# Hearts to Remember

## Google Docs

```text
Session Ownership

OT over CRDT

Edit Event != Version

Kafka Edit Log

Snapshot + Replay

S3 != Playground
```

---

## Slack / Teams

```text
Conversation Ordering

chatId -> Kafka Partition

Small Group -> Fan-Out-On-Write

Large Channel -> Fan-Out-On-Read

Presence != Delivery

Ordering Within Conversation Only
```