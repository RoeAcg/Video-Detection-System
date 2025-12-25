# Microservice Port Migration Summary

## Issue
Windows Hyper-V/Docker Desktop reserved port ranges that conflicted with microservice ports:
- **8181-8280** (included 8191, 8192)
- **8437-8936** (included 8194, 8195, 8196)

This prevented services from binding to their configured ports.

## Solution
Migrated all microservice ports to the 9001-9006 range, which is not reserved by Windows.

## Port Mapping

| Service | Old Port | New Port | Status |
|---------|----------|----------|--------|
| auth-service | 8191 | **9001** | ✅ Updated |
| video-service | 8192 | **9002** | ✅ Updated |
| worker-service | 8193 | **9003** | ✅ Updated |
| detection-service | 8194 | **9004** | ✅ Updated |
| websocket-service | 8195 | **9005** | ✅ Updated |
| audit-service | 8196 | **9006** | ✅ Updated |

## Files Modified

### Backend Services
- `auth-service/src/main/resources/application.yml`
- `video-service/src/main/resources/application.yml`
- `worker-service/src/main/resources/application.yml`
- `detection-service/src/main/resources/application.yml`
- `websocket-service/src/main/resources/application.yml`
- `audit-service/src/main/resources/application.yml`

### Frontend
- `frontend/vite.config.js` - Updated all proxy targets

## Startup Instructions

1. **Clean up any existing processes:**
   ```powershell
   .\scripts\cleanup-ports.ps1
   ```

2. **Start services in order (one at a time):**
   - auth-service (9001)
   - video-service (9002)
   - worker-service (9003)
   - detection-service (9004)
   - websocket-service (9005)
   - audit-service (9006)

3. **Start frontend:**
   ```bash
   cd frontend
   npm run dev
   ```

## Verification

Check if ports are listening:
```powershell
netstat -ano | findstr "LISTENING" | findstr ":900"
```

You should see all 6 services listening on ports 9001-9006.

## Notes
- Frontend proxy automatically routes requests to new backend ports
- No code changes required - only configuration updates
- Ports 9001-9006 are not in Windows reserved ranges
