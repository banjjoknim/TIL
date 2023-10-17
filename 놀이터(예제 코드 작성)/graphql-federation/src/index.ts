// The ApolloServer constructor requires two parameters: your schema
// definition and your set of resolvers.
import {startStandaloneServer} from "@apollo/server/standalone";
import {ApolloServer} from "@apollo/server";
import typeDefs from "./typeDefs.js";
import resolvers from "./resolvers.js";

const server = new ApolloServer({
    typeDefs,
    resolvers,
});

// Passing an ApolloServer instance to the `startStandaloneServer` function:
//  1. creates an Express app
//  2. installs your ApolloServer instance as middleware
//  3. prepares your app to handle incoming requests
const {url} = await startStandaloneServer(server, {
    listen: {port: 4000},
});

console.log(`🚀  Server ready at: ${url}`);
