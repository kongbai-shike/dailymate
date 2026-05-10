import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  vus: 10,
  duration: "30s",
  thresholds: {
    http_req_failed: ["rate<0.01"],
    http_req_duration: ["p(95)<200"],
  },
};

const BASE = "http://localhost:8080";
const USERNAME = "Test11";
const PASSWORD = "Test11";
const USER_ID = "19";

function login() {
  const res = http.post(
    `${BASE}/api/auth/login`,
    JSON.stringify({ username: USERNAME, password: PASSWORD }),
    { headers: { "Content-Type": "application/json" } }
  );

  check(res, {
    "login ok": (r) => r.status === 200,
  });

  const body = res.json();
  return body?.data?.token;
}

export default function () {
  const token = login();
  const headers = { Authorization: `Bearer ${token}` };

  let res = http.get(`${BASE}/api/todo/list?userId=${USER_ID}`, { headers });
  check(res, { "todo list ok": (r) => r.status === 200 });

  res = http.get(`${BASE}/api/bill/list?userId=${USER_ID}`, { headers });
  check(res, { "bill list ok": (r) => r.status === 200 });

  res = http.get(
    `${BASE}/api/bill/stat/month?userId=${USER_ID}&year=2024&month=5`,
    { headers }
  );
  check(res, { "bill stat ok": (r) => r.status === 200 });

  sleep(1);
}

