# Architecture

## System Overview
```mermaid
graph TB
    Client[Client/Ad Server] --> LB[Load Balancer]
    LB --> AS1[Auction Service 1]
    LB --> AS2[Auction Service 2]
    LB --> AS3[Auction Service N]
    
    AS1 --> Redis[(Redis Cache<br/>User Profiles)]
    AS2 --> Redis
    AS3 --> Redis
    
    AS1 --> CPP[C++ Bid Evaluator<br/>Fast Path]
    AS2 --> CPP
    
    AS1 --> DP[Differential Privacy<br/>Layer]
    AS2 --> DP
    
    subgraph "Auction Flow"
        AS1 --> Filter[1. Filter Bids]
        Filter --> Score[2. Score & Rank]
        Score --> Price[3. Second-Price<br/>Calculation]
    end