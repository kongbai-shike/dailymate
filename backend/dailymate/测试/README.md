# Dailymate Test Scripts

## Prereqs
- Backend running at http://localhost:8080
- User: Test11 / Test11, userId: 19
- Node.js 18+ (for SSE script)
- k6 installed (for load test)

## Install Node deps
```
npm install
```

## Run k6 load test
```
k6 run k6-smoke.js
```

## Run SSE reminder latency test
```
node sse-reminder-latency.js
```

