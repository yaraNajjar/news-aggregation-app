# News Aggregation Application

## Overview
This project aims to develop a microservice-based application that aggregates news and technology updates based on user preferences. The system fetches the latest news, selects the most interesting news using AI based on user preferences, and sends this information to users via email.

## Microservices
1. **User Service**: Manages user registration and preferences.
2. **News Service**: Fetches and processes news based on user preferences.
3. **Email Service**: Sends email notifications to users with news summaries.
4. **AI Service**: Selects interesting news and generates summaries using AI.

## Technologies Used
- **Spring Boot**: For building the microservices.
- **MongoDB**: As the database for storing user and preferences data.
- **Kafka**: For message queueing between services.
- **Docker**: For containerizing the microservices.
- **Dapr**: For service communication.
- **Postman**: For testing the APIs.

## Running the Application
1. Clone the repository:
   ```bash
   git clone https://github.com/yaraNajjar/news-aggregation-app.git
   cd news-aggregation-app
