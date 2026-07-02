import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 100,
    duration: '60s'
};

export default function () {

    const response = http.get(
        'http://localhost:8080/documents/1'
    );

    check(response, {
        'status is 200': (r) => r.status === 200
    });

    sleep(1);
}