import {ApolloServer} from '@apollo/server';
import {startStandaloneServer} from '@apollo/server/standalone';
import {ApolloGateway} from '@apollo/gateway';
import {readFileSync} from 'fs';
import {readFile} from "fs/promises";
import {exec} from "child_process";

// const supergraphSdl = readFileSync('./supergraph.graphql').toString();

// Initialize an ApolloGateway instance and pass it
// the supergraph schema as a string
let supergraphUpdate;
const gateway = new ApolloGateway({
    async supergraphSdl({update}) {
        // `update` is a function that we'll save for later use
        supergraphUpdate = update;
        return {
            supergraphSdl: await readFile('./supergraph.graphql', 'utf-8'),
        };
    },
});

// Pass the ApolloGateway to the ApolloServer constructor
const server = new ApolloServer({
    gateway,
});

// 서버 실행 전에 특정 shell 스크립트를 실행한다
const scriptToExecute = './compose_supergraph.sh'; // 실행할 스크립트 파일 이름을 지정한다

exec(scriptToExecute, (error, stdout, stderr) => {
    if (error) {
        console.error(`Error executing script: ${error}`);
        return;
    }
    console.log(`Script output: ${stdout}`);
    console.error(`Script error: ${stderr}`);

    // 스크립트가 성공적으로 실행되면 서버를 시작한다
    startServer();
});

function startServer() {
    startStandaloneServer(server)
        .then(({url}) => {
            console.log(`🚀  Server ready at ${url}`);
        })
        .catch((error) => {
            console.error('Error starting the server:', error);
        });
}

// // Note the top-level `await`!
// const {url} = await startStandaloneServer(server);
// console.log(`🚀  Server ready at ${url}`);
