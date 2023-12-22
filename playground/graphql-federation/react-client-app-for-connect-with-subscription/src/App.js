import {ApolloProvider} from "@apollo/client";
import {BrowserRouter as Router, Route, Switch} from "react-router-dom";
import React from "react";
import client from "./graphql/apollo";

import "./App.css";
import NotiAboutBookOfPickedUpPage from "./pages/NotiAboutBookOfPickedUpPage";

function App() {
  return (
    <ApolloProvider client={client}>
      <Router>
        <Switch>
          <Route path="*" component={NotiAboutBookOfPickedUpPage} />
        </Switch>
      </Router>
    </ApolloProvider>
  );
}

export default App;
