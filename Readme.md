# Real-Time Collaborative Sync Engine

A distributed real-time collaborative editing platform built with Spring Boot, WebSockets, Redis, Kafka, and PostgreSQL. The system enables multiple users to edit documents simultaneously while maintaining consistency through Operational Transformation (OT), distributed synchronization, event sourcing, and fault-tolerant recovery mechanisms.

---

## Features

### Real-Time Collaboration

* Multi-user collaborative document editing
* WebSocket + STOMP based communication
* Live document synchronization
* Presence tracking and active user monitoring

### Consistency & Conflict Resolution

* Operational Transformation (OT) engine
* Document versioning
* Concurrent edit handling
* Conflict-free synchronization workflow

### Distributed Architecture

* Consistent hashing for document ownership
* Request routing across nodes
* Redis Pub/Sub for distributed event propagation
* Horizontal scaling support

### Reliability & Recovery

* Kafka-based event sourcing
* Snapshot-based document recovery
* Event replay mechanism
* Fault-tolerant document reconstruction

### Observability

* Spring Boot Actuator
* Prometheus metrics
* Grafana dashboards
* Custom application metrics

### DevOps

* Dockerized deployment
* Docker Compose orchestration
* Kubernetes manifests included
* GitHub Actions CI/CD

---

## Architecture

```text
Client
   |
WebSocket/STOMP
   |
+----------------------+
| Spring Boot Cluster  |
+----------------------+
      |        |
      |        |
    Redis    Kafka
      |        |
      +--------+
           |
      PostgreSQL

Prometheus --> Grafana
```

### Request Flow

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
* Custom Collaborative Load Tests

---

## Project Structure

```text
backend/
docs/
load-tests/
k8s/
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

### Start Application

```bash
mvn spring-boot:run
```

### Access Services

```text
Application : http://localhost:8080
Swagger UI  : http://localhost:8080/swagger-ui/index.html
Prometheus  : http://localhost:9090
Grafana     : http://localhost:3000
```

---

## Load Testing

Collaborative editing load tests are available under:

```text
load-tests/
```

Sample benchmark results:

| Concurrent Users | Messages Sent | Messages Received | Avg Latency |
| ---------------- | ------------- | ----------------- | ----------- |
| 100              | 2,400         | 170,407           | 45 ms       |
| 250              | 6,000         | 435,000           | 72 ms       |
| 500              | 12,000        | 910,000           | 118 ms      |


---

## Monitoring

Metrics exposed through:

```text
/actuator/prometheus
```

Tracked metrics include:

* Active users
* Active documents
* Operations processed
* WebSocket connections
* JVM memory usage
* CPU utilization

---

## Future Improvements

* CRDT-based synchronization engine
* Leader election and failover
* Multi-region deployment
* Distributed service discovery
* Advanced security and authentication
* Automated scaling

---

## Key Learning Outcomes

* Distributed Systems Design
* Real-Time Communication
* Event-Driven Architecture
* Operational Transformation
* Kafka Event Sourcing
* Redis Distributed Messaging
* Observability & Monitoring
* Containerization & Deployment
* Performance Testing & Benchmarking
