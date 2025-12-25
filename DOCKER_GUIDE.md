# ApexAuth Docker Deployment Guide

## Prerequisites

- Docker installed ([Get Docker](https://docs.docker.com/get-docker/))
- Docker Compose installed (comes with Docker Desktop)

## Quick Start

### 1. Clone the repository
```bash
git clone <your-repo>
cd apexauth
```

### 2. Run with Docker Compose
```bash
docker-compose up -d
```

That's it! 🎉

The service will be available at `http://localhost:8080`

---

## Detailed Commands

### Build and Start Services
```bash
# Build and start in detached mode
docker-compose up -d

# View logs
docker-compose logs -f

# View logs for specific service
docker-compose logs -f apexauth
```

### Stop Services
```bash
# Stop containers (data preserved)
docker-compose stop

# Stop and remove containers (data preserved in volumes)
docker-compose down

# Stop and remove everything (including volumes - DELETES DATA!)
docker-compose down -v
```

### Rebuild After Code Changes
```bash
# Rebuild and restart
docker-compose up -d --build
```

---

## Verify Deployment

### 1. Check if containers are running
```bash
docker ps
```

You should see:
- `apexauth-postgres`
- `apexauth-service`

### 2. Check logs
```bash
docker-compose logs apexauth
```

Look for: `Started ApexauthApplication`

### 3. Test the API
```bash
# Health check
curl http://localhost:8080/actuator/health

# Register a user
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"identifier":"test@example.com","password":"password123"}'
```

### 4. Access Swagger UI
Open browser: `http://localhost:8080/swagger-ui/index.html`

---

## Configuration

### Environment Variables

Edit `docker-compose.yml` to change:

```yaml
environment:
  JWT_SECRET: "your-secret-key"
  JWT_EXPIRATION_MS: 900000  # 15 minutes
  CORS_ALLOWED_ORIGINS: http://localhost:3000
```

---

## Database Access

### Connect to PostgreSQL
```bash
docker exec -it apexauth-postgres psql -U knox -d apexauth_db
```

### View tables
```sql
\dt
SELECT * FROM users;
\q
```

---

## Deployment to Cloud

### Deploy to Railway

1. Push to GitHub
2. Go to [Railway.app](https://railway.app)
3. Create new project from GitHub repo
4. Railway auto-detects Docker Compose
5. Deploy! 🚀

### Deploy to Render

1. Push to GitHub
2. Go to [Render.com](https://render.com)
3. Create new Web Service
4. Select Docker
5. Deploy! 🚀

### Deploy to AWS ECS

```bash
# Build and push to ECR
docker build -t apexauth .
docker tag apexauth:latest <aws-account>.dkr.ecr.us-east-1.amazonaws.com/apexauth:latest
docker push <aws-account>.dkr.ecr.us-east-1.amazonaws.com/apexauth:latest
```

---

## Troubleshooting

### Port already in use
```bash
# Change port in docker-compose.yml
ports:
  - "8081:8080"  # Host:Container
```

### Database connection failed
```bash
# Check if postgres is healthy
docker-compose ps

# Restart services
docker-compose restart
```

### View container logs
```bash
docker-compose logs -f apexauth
```

### Access container shell
```bash
docker exec -it apexauth-service sh
```

---

## Production Checklist

Before deploying to production:

- [ ] Change `JWT_SECRET` to strong random value
- [ ] Update `CORS_ALLOWED_ORIGINS` to your domain
- [ ] Set `SPRING_PROFILE=prod`
- [ ] Use environment-specific database
- [ ] Enable HTTPS/SSL
- [ ] Set up monitoring (Prometheus, Grafana)
- [ ] Configure automated backups
- [ ] Set up logging aggregation
- [ ] Configure health checks
- [ ] Set resource limits (CPU, memory)

---

## Resource Limits (Production)

Add to `docker-compose.yml`:

```yaml
apexauth:
  # ...existing config...
  deploy:
    resources:
      limits:
        cpus: '0.5'
        memory: 512M
      reservations:
        cpus: '0.25'
        memory: 256M
```

---

## Backup Database

```bash
# Backup
docker exec apexauth-postgres pg_dump -U knox apexauth_db > backup.sql

# Restore
cat backup.sql | docker exec -i apexauth-postgres psql -U knox apexauth_db
```

---

## Scaling

### Multiple instances (Load Balancing)
```bash
docker-compose up -d --scale apexauth=3
```

Add Nginx load balancer in front.

---

## Monitoring

### View container stats
```bash
docker stats
```

### Health check endpoint
```bash
curl http://localhost:8080/actuator/health
```

---

## Clean Up

### Remove all containers and volumes
```bash
docker-compose down -v
```

### Remove unused images
```bash
docker image prune -a
```

---

## Next Steps

1. Deploy to production platform (Railway/Render/AWS)
2. Set up CI/CD (GitHub Actions)
3. Configure monitoring and alerts
4. Set up automated backups
5. Add SSL/TLS certificates

---

**Congratulations! Your microservice is now containerized and production-ready!** 🎉
