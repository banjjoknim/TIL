# GraphQL Federation SuperGraph Gateway Sample

## 실행 방법

> 1. `GraphQL Federation`를 제공하는 서버를 먼저 실행시킨다. 이때 구동되는 서버는 반드시 `subgraph` 역할을 지원하는 것이어야 하며, 해당 기능의 가능 유무에 대한 내용은 아래 참고 자료
     중에서 [Federation-compatible subgraph implementations](https://www.apollographql.com/docs/federation/building-supergraphs/supported-subgraphs/)
     를 참조하도록 한다.

> 2. 본 프로젝트의 `supergraphql.yaml`의 내용에 1번 단계에서 실행시킨 서버의 `routing_url`, `schema.subgraph_url` 을 지정한다.
     이는 [Rover supergraph commands](https://www.apollographql.com/docs/rover/commands/supergraphs)를 참조하도록 한다.

> 3. 본 프로젝트의 `compose_supergraph.sh` 파일을 실행한다.
>
>```shell
>sh compose_supergraph.sh
>```

> 4. `npm install` 명령을 실행하지 않았다면 해당 명령어를 실행한다.
>
>```shell
> npm install
>```


> 5. 마지막으로 본 프로젝트를 실행한다.
>
>```shell
>npm run start
>```

### 주의사항

- `supergraph.graphql` 파일의 내용이 비어있으면 실행이 되지 않음에 유의할 것.

## 참고 자료

### Node.js

- [module: esnext should append .js to imports #33588](https://github.com/microsoft/TypeScript/issues/33588)
- [Node.js와 ESM](https://jjnooys.medium.com/node-js%EC%99%80-esm-2462af271156)
- [Node.js와 ESM (2) Typescript 프로젝트 ESM으로 변환하기](https://jjnooys.medium.com/node-js%EC%99%80-esm-2-typescript-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-esm%EC%9C%BC%EB%A1%9C-%EB%B3%80%ED%99%98%ED%95%98%EA%B8%B0-7266e8174906)

### Apollo

- [Get started with Apollo Server](https://www.apollographql.com/docs/apollo-server/getting-started)
- [Implementing a gateway with Apollo Server](https://www.apollographql.com/docs/apollo-server/using-federation/apollo-gateway-setup)
- [Schema composition](https://www.apollographql.com/docs/federation/federated-types/composition/#supported-methods)
- [The Rover CLI](https://www.apollographql.com/docs/rover)
- [Rover supergraph commands](https://www.apollographql.com/docs/rover/commands/supergraphs)
- [Federation-compatible subgraph implementations](https://www.apollographql.com/docs/federation/building-supergraphs/supported-subgraphs/)
- [The Apollo Router](https://www.apollographql.com/docs/router/)
- [API Reference: @apollo/gateway](https://www.apollographql.com/docs/apollo-server/using-federation/api/apollo-gateway/)
- [Does Apollo Federation support subscriptions?](https://support.apollographql.com/hc/en-us/articles/5881531249683-Does-Apollo-Federation-support-subscriptions-)
- [Federated subscriptions in GraphOS: real-time data at scale](https://www.apollographql.com/blog/announcement/backend/federated-subscriptions-in-graphos-real-time-data-at-scale/)
- [Apollo Federation: MAS for GraphQL](https://devstarsj.github.io/development/2023/03/12/Apollo.Federation/)
- [join v0.1 - for defining supergraphs which join multiple subgraphs](https://specs.apollo.dev/join/v0.1/)
- [Apollo Library of Technical Specifications](https://specs.apollo.dev/)
- [Learn GraphQL: What is Federated Architecture?](https://graphql.com/learn/federated-architecture/)
- [Subscriptions in Apollo Server](https://www.apollographql.com/docs/apollo-server/data/subscriptions/)
  - [The WebSocket API (WebSockets)](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API) 
  - [Subscriptions - Get real-time updates from your GraphQL server](https://www.apollographql.com/docs/react/data/subscriptions)
