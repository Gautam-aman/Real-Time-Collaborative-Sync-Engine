import http from 'k6/http';

export const options = {
    vus: 50,
    duration: '60s'
};

export default function () {

    const payload = JSON.stringify({
        documentId: "1",
        userId: "load-test-user",
        operation: "INSERT",
        position: 0,
        value: "A",
        version: 0
    });

    http.post(
        'http://localhost:8080/internal/edit',
        payload,
        {
            headers: {
                'Content-Type': 'application/json'
            }
        }
    );
}
