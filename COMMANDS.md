# Command Reference

## Local development

```bash
# Start only the PostgreSQL container, in the background.
docker compose up -d postgres

# Check that the container is actually running.
docker compose ps

# Wipe all tasks and reset the id counter back to 1
# demo starts clean. Run this right before recording.
docker exec -it student-productivity-db psql -U student_app -d student_productivity -c "TRUNCATE TABLE tasks RESTART IDENTITY;"

# Open a psql session inside the running container, useful for poking around manually instead of running one command at a time.
docker exec -it student-productivity-db psql -U student_app -d student_productivity

# From inside psql: view every task, sorted by id.
SELECT * FROM tasks ORDER BY id;

# From inside psql: leave the session.
\q
```

## Freeing a stuck port

Use this if the app refuses to start with a "port already in use" error. It's usually because an earlier run is still going in the background.

```powershell
# Find whatever process is listening on port 7070.
netstat -ano | findstr :7070

# Stop that process, using the PID from the line above.
taskkill /PID <that number> /F
```

## Connecting to AWS

Use this any time you need to get back onto your EC2 server or copy a new build up to it.

```powershell
# Open a terminal session on the EC2 server. ServerAliveInterval keeps the connection alive on flaky networks.
ssh -o ServerAliveInterval=30 -i student-api-key.pem ec2-user@<EC2-PUBLIC-IP>

# Copy a newly built JAR up to the server's home folder.
scp -i student-api-key.pem target\student-productivity-cloud-api-1.0-SNAPSHOT.jar ec2-user@<EC2-PUBLIC-IP>:~
```

Find the current public IP in the EC2 console under your instance's details.

## On the EC2 server

Use this if you ever need to reinstall Java, restart the service, check why it stopped running or reset the RDS database before a demo. Run these inside the SSH session.

```bash
# Install Java 21.
sudo dnf install -y java-21-amazon-corretto-headless

# Confirm the installed version.
java -version

# Check whether the background service is currently running.
sudo systemctl status student-api

# Restart the service.
sudo systemctl restart student-api

# Watch the service's live log output which helps me with debugging.
# Press Ctrl + C to stop watching, this does not stop the service itself.
journalctl -u student-api -f

# Install a PostgreSQL client so you can query RDS directly from here.
sudo dnf install -y postgresql15

# Wipe all tasks in RDS and reset the id counter.
psql -h <your-RDS-endpoint> -U student_app -d student_productivity -c "TRUNCATE TABLE tasks RESTART IDENTITY;"
```

If you ever need to recreate the background service from scratch, here is the full setup:

```bash
# Store the database settings in a file the service will read.
sudo tee /etc/student-api.env > /dev/null <<'ENV'
DB_HOST=<your-rds-endpoint>
DB_PORT=5432
DB_NAME=student_productivity
DB_USER=student_app
DB_PASSWORD=your-password
ENV

# Lock the file down so only the owner can read it.
sudo chmod 600 /etc/student-api.env

# Define the service itself.
sudo tee /etc/systemd/system/student-api.service > /dev/null <<'UNIT'
[Unit]
Description=Student Productivity API
After=network.target

[Service]
User=ec2-user
EnvironmentFile=/etc/student-api.env
ExecStart=/usr/bin/java -jar /home/ec2-user/student-productivity-cloud-api-1.0-SNAPSHOT.jar
Restart=always

[Install]
WantedBy=multi-user.target
UNIT

# Reload systemd so it notices the new service file then start it and set it to also start on every future boot.
sudo systemctl daemon-reload
sudo systemctl enable --now student-api
```

## Testing the deployed API

Use this to confirm the live AWS deployment works.

```powershell
# Replace with your instance's current public IP address.
Invoke-RestMethod -Method Get -Uri http://<EC2-PUBLIC-IP>:7070/tasks

Invoke-RestMethod -Method Post -Uri http://<EC2-PUBLIC-IP>:7070/tasks -ContentType "application/json" -Body '{"title":"Study AWS","description":"Finish the project"}'
```
