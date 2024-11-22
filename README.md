

Build Docker Images
To build Docker images for your services, run:


docker-compose build
This command reads the Dockerfile and builds the Docker image for your application.
It will also build images for other services like Kafka if specified in docker-compose.yml.
Run Docker Compose to Start All Services
After building the images, start all services with:

docker-compose up

This command will launch all the services defined in docker-compose.yml, such as the Java application, Kafka, and Zookeeper.
Run in Detached Mode (Optional)
If you want the services to run in the background, use:

docker-compose up -d


The -d flag stands for "detached mode." This keeps the services running without blocking your terminal.
Verify That Containers Are Running
To verify if the containers are running, use the following command:

docker ps

This command lists all running Docker containers, and you should see containers for your application, Kafka, Zookeeper, and any other services defined.
Access the Swagger UI for API Documentation
Once all the services are running, you can access the Swagger UI by opening a web browser and navigating to:

http://localhost:1616/swagger-ui/index.html

This URL will display the Swagger interface where you can explore your application's REST APIs.