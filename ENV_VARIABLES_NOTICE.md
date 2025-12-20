# ⚠️ IMPORTANT: Environment Variables Notice

## Development vs Production

### Development (.env in repository)
The `.env` file in this repository contains **DEVELOPMENT-ONLY** credentials:
- `POSTGRES_PASSWORD=securepassword` - Default dev password
- `APP_JWT_SECRET=XgpwoMzjydGoMT9dXuMuRQoK1dZvUouX2Cqfs5Hyz6o=` - Dev JWT secret

**These are NOT secure for production use.**

### Production (DO NOT commit)

For production deployments:

1. **NEVER use the .env file from this repository**
2. **Set environment variables directly in your deployment environment:**
   - Docker: Use `docker run -e VAR=value` or `docker-compose` with env_file
   - Kubernetes: Use Secrets and ConfigMaps
   - Cloud platforms: Use their secrets management (AWS Secrets Manager, Azure Key Vault, etc.)

3. **Generate new secure values:**
   ```bash
   # Generate strong JWT secret (32 bytes = 256 bits)
   openssl rand -base64 32
   
   # Generate strong database password (20+ characters)
   openssl rand -base64 24
   ```

## Why is .env committed?

The `.env` file is committed to this repository for **convenience in development only**:
- ✅ Makes it easy for developers to get started quickly
- ✅ Ensures consistent development environment
- ✅ Values are clearly marked as non-secure defaults
- ✅ Real production secrets should NEVER be committed

## Production Deployment Checklist

Before deploying to production:

- [ ] Generate new `POSTGRES_PASSWORD` (20+ characters, random)
- [ ] Generate new `APP_JWT_SECRET` (256-bit, random)
- [ ] Set environment variables through secure secrets management
- [ ] Update CORS origins in `PlanningController.java`
- [ ] Enable HTTPS/TLS
- [ ] Disable SQL logging (`spring.jpa.show-sql=false`)
- [ ] Set `spring.jpa.hibernate.ddl-auto=validate`
- [ ] Review SECURITY.md checklist

## Example: Production Deployment with Docker

**DO NOT use the .env file. Set variables directly:**

```bash
docker run \
  -e POSTGRES_PASSWORD="$(openssl rand -base64 24)" \
  -e APP_JWT_SECRET="$(openssl rand -base64 32)" \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://prod-db:5432/pms_prod" \
  your-app-image
```

**Or use a separate production .env file (NOT committed to git):**

```bash
# production.env (NEVER COMMIT THIS)
POSTGRES_PASSWORD=<your-secure-password>
APP_JWT_SECRET=<your-secure-jwt-secret>
POSTGRES_DB=pms_production

# Run with:
docker-compose --env-file production.env up
```

---

**Remember:** The .env file in this repository is for development only. Always use proper secrets management in production.
