const { Client } = require("@stomp/stompjs");
const SockJS = require("sockjs-client");

const USERS = 100;

const DOCUMENT_ID = "doc-1";

let totalMessagesSent = 0;
let totalMessagesReceived = 0;

for (let i = 0; i < USERS; i++) {

    const userId = `user-${i}`;

    const client = new Client({

        webSocketFactory: () =>
            new SockJS(
                "http://localhost:8080/ws"
            ),

        reconnectDelay: 5000,

        debug: () => {}
    });

    client.onConnect = () => {

        console.log(
            `${userId} connected`
        );

        client.subscribe(
            `/topic/document/${DOCUMENT_ID}`,

            () => {

                totalMessagesReceived++;
            }
        );

        client.publish({

            destination:
                "/app/join",

            body: JSON.stringify({

                documentId:
                DOCUMENT_ID,

                userId:
                userId
            })
        });

        setInterval(() => {

            const message = {

                documentId:
                DOCUMENT_ID,

                userId:
                userId,

                operation:
                    "INSERT",

                position:
                    0,

                value:
                    "A",

                version:
                    0
            };

            client.publish({

                destination:
                    "/app/edit",

                body:
                    JSON.stringify(
                        message
                    )
            });

            totalMessagesSent++;

        }, 1000);
    };

    client.activate();
}

setInterval(() => {

    console.log(
        "\n===================="
    );

    console.log(
        "Messages Sent:",
        totalMessagesSent
    );

    console.log(
        "Messages Received:",
        totalMessagesReceived
    );

}, 5000);