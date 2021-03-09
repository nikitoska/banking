#  API for online banking personal account management

Run `docker-compose up` 

Swagger ui on http://localhost:8080/swagger-ui.html

Each service has own Cassandra db.
Only customer service has outside rest endpoints, communicate with other services via 
ActiveMQ Artemis
