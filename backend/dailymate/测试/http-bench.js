const BASE = "http://localhost:8080";
const USERNAME = "Test11";
const PASSWORD = "Test11";
const USER_ID = "19";
const ITERATIONS = 100;

function nowMs() {
  return Number(process.hrtime.bigint()) / 1e6;
}

function p95(values) {
  if (!values.length) return 0;
  const sorted = [...values].sort((a, b) => a - b);
  const idx = Math.ceil(sorted.length * 0.95) - 1;
  return sorted[Math.max(0, idx)];
}

async function login() {
  const res = await fetch(`${BASE}/api/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username: USERNAME, password: PASSWORD }),
  });
  const json = await res.json().catch(() => ({}));
  return { status: res.status, token: json?.data?.token };
}

async function timedRequest(name, url, options) {
  const start = nowMs();
  const res = await fetch(url, options);
  const duration = nowMs() - start;
  return { name, status: res.status, duration };
}

(async () => {
  const auth = await login();
  if (!auth.token) {
    console.error("Login failed. Status:", auth.status);
    process.exit(1);
  }

  const headers = { Authorization: `Bearer ${auth.token}` };

  const tests = [
    {
      name: "todo_list",
      url: `${BASE}/api/todo/list?userId=${USER_ID}`,
      options: { headers },
    },
    {
      name: "bill_list",
      url: `${BASE}/api/bill/list?userId=${USER_ID}`,
      options: { headers },
    },
    {
      name: "bill_stat_month",
      url: `${BASE}/api/bill/stat/month?userId=${USER_ID}&year=2024&month=5`,
      options: { headers },
    },
  ];

  const results = {};
  for (const t of tests) {
    results[t.name] = { durations: [], success: 0, total: 0 };
  }

  for (let i = 0; i < ITERATIONS; i++) {
    for (const t of tests) {
      const r = await timedRequest(t.name, t.url, t.options);
      results[t.name].durations.push(r.duration);
      results[t.name].total += 1;
      if (r.status >= 200 && r.status < 300) {
        results[t.name].success += 1;
      }
    }
  }

  console.log(`Iterations: ${ITERATIONS}`);
  for (const [name, data] of Object.entries(results)) {
    const avg = data.durations.reduce((a, b) => a + b, 0) / data.durations.length;
    const p95v = p95(data.durations);
    const successRate = (data.success / data.total) * 100;
    console.log(
      `${name}: avg=${avg.toFixed(2)}ms, p95=${p95v.toFixed(2)}ms, success=${successRate.toFixed(1)}%`
    );
  }
})();

