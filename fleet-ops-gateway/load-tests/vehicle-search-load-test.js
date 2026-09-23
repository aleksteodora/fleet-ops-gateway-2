import http from 'k6/http';
import { check } from 'k6';

export const options = {
  vus: 300,
  iterations: 10000,
  thresholds: {
    http_req_duration: ['p(95)<2500'],
    http_req_failed: ['rate<0.05'],
  },
};

export default function () {
  const response = http.get(
    'http://localhost:8080/api/v1/vehicle-search?vin=TESTVIN0000000001',
    {
      headers: {
        'X-User-Id': '1',
        'X-Company-Id': '1',
      },
    }
  );

  check(response, {
    'status is 200': (r) => r.status === 200,
  });
}