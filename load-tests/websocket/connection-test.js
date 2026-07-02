import ws from 'k6/ws';

export const options = {
    vus: 100,
    duration: '60s'
};

export default function () {

    ws.connect(
        'ws://localhost:8080/ws/websocket',
        {},

        function (socket) {

            socket.on('open', () => {
                console.log('connected');
            });

            socket.setInterval(() => {
                socket.send('ping');
            }, 1000);
        }
    );
}