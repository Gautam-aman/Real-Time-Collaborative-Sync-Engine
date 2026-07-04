# Real-Time Collaborative Sync Engine

A distributed real-time collaborative editing platform built using Spring Boot, WebSockets, Redis, Kafka, PostgreSQL, Prometheus, and Grafana. The system enables multiple users to edit documents simultaneously while maintaining consistency through Operational Transformation (OT), distributed synchronization, event sourcing, and recovery mechanisms.

![Architecture](docs/architecture.png)

---

## Overview

This project simulates the core architecture behind collaborative platforms such as Google Docs and Notion. Multiple users can edit the same document concurrently while the system resolves conflicts, synchronizes updates across nodes, persists events for recovery, and provides real-time visibility through monitoring dashboards.

---

## Features

### Real-Time Collaboration

* Multi-user collaborative editing
* WebSocket + STOMP communication
* Live document synchronization
* Presence tracking and active user monitoring
* Instant update broadcasting

### Consistency & Conflict Resolution

* Operational Transformation (OT) engine
* Document versioning
* Concurrent edit handling
* Conflict resolution workflow

### Distributed Architecture

* Consistent hashing for document ownership
* Request routing across nodes
* Redis Pub/Sub for distributed synchronization
* Horizontal scaling support

### Reliability & Recovery

* Kafka-based event sourcing
* Snapshot-based recovery
* Event replay mechanism
* Fault-tolerant document reconstruction

### Observability

* Spring Boot Actuator
* Prometheus metrics collection
* Grafana dashboards
* Custom business metrics

### DevOps

* Dockerized deployment
* Docker Compose orchestration
* Kubernetes manifests included
* GitHub Actions CI/CD

---

## Architecture

```text
                    +----------------+
                    |     Client     |
                    +----------------+
                            |
                    WebSocket/STOMP
                            |
        +--------------------------------------+
        |      Spring Boot Cluster             |
        |                                      |
        |  Node-1   Node-2   Node-3           |
        +--------------------------------------+
              |          |          |
              +----------+----------+
                         |
                  Redis Pub/Sub
                         |
                   Kafka Topic
                         |
                    PostgreSQL

           Prometheus --------> Grafana
```

### Edit Flow

```text
User Edit
    |
/app/edit
    |
Document Controller
    |
Operational Transformation
    |
Kafka Event Store
    |
Redis Pub/Sub
    |
WebSocket Broadcast
    |
Connected Users
```

---

## Tech Stack

### Backend

* Java 21
* Spring Boot
* Spring WebSocket
* Spring Data JPA
* Spring Actuator

### Messaging & Streaming

* Redis Pub/Sub
* Apache Kafka

### Database

* PostgreSQL

### Monitoring

* Prometheus
* Grafana

### DevOps

* Docker
* Docker Compose
* Kubernetes
* GitHub Actions

### Testing

* k6
* Custom WebSocket Load Testing Suite

---

## Project Structure

```text
backend/
│
├── src/
├── Dockerfile
├── pom.xml
│
docs/
│
├── architecture.png
├── grafana-dashboard.png
└── load-test-results.png
│
load-tests/
│
├── collaborative/
│   └── collab-edit-test.js
│
├── results/
│   └── benchmark-results.md
│
├── document-read-test.js
└── document-write-test.js
│
k8s/
│
docker-compose.yml
README.md
```

---

## Running Locally

### Clone Repository

```bash
git clone https://github.com/Gautam-aman/Real-Time-Collaborative-Sync-Engine
cd sync-engine
```

### Start Infrastructure

```bash
docker compose up -d
```

### Start Node 1

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=node1
```

### Start Node 2

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=node2
```

### Start Node 3

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=node3
```

---

## Services

| Service     | URL                                         |
| ----------- | ------------------------------------------- |
| Application | http://localhost:8080                       |
| Swagger UI  | http://localhost:8080/swagger-ui/index.html |
| Prometheus  | http://localhost:9090                       |
| Grafana     | http://localhost:3000                       |

---

## Monitoring

![Grafana Dashboard](docs/grafana-dashboard.png)

Metrics exposed through:

```text
/actuator/prometheus
```

Tracked metrics:

* Active users
* Active documents
* Operations processed
* WebSocket connections
* JVM memory usage
* CPU utilization

---

## Load Testing

![Load Test Results](docs/load-test-results.png)

Custom collaborative editing benchmark:

### Sample Results

| Concurrent Users | Messages Sent | Messages Received | Avg Latency |
| ---------------- | ------------- | ----------------- | ----------- |
| 100              | 2,400         | 170,407           | 45 ms       |
| 250              | 6,000         | 435,000           | 72 ms       |
| 500              | 12,000        | 910,000           | 118 ms      |



### Run Benchmark

```bash
node load-tests/collaborative/collab-edit-test.js
```

---

## Key Components

### Operational Transformation Engine

Handles concurrent edits while maintaining document consistency across all connected clients.

### Redis Pub/Sub

Enables distributed synchronization and cross-node event propagation.

### Kafka Event Sourcing

Stores document operations for replay, recovery, and auditing.

### Snapshot Recovery

Reduces replay overhead by periodically persisting document state.

### Consistent Hashing

Distributes document ownership across nodes and enables scalable request routing.

---

## CI/CD

GitHub Actions automatically:

* Build project
* Run tests
* Validate codebase
* Package application

Workflow location:

```text
.github/workflows/build.yml
```

---

## Future Improvements

* CRDT-based synchronization engine
* Leader election and failover
* Dynamic service discovery
* Authentication & authorization
* Multi-region deployment
* Automatic scaling policies
* Advanced conflict resolution

---

## Learning Outcomes

* Distributed Systems Design
* Real-Time Communication
* Event-Driven Architecture
* Operational Transformation
* Kafka Event Sourcing
* Redis Distributed Messaging
* Performance Engineering
* Observability & Monitoring
* Containerization & Deployment

---

## License

This project is intended for learning, experimentation, and demonstrating distributed systems concepts.
