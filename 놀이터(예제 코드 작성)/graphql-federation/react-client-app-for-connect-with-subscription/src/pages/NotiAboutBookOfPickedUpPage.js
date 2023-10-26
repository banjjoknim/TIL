import React from "react";
import {useSubscription} from "@apollo/client";
import {NOTIFY_PICKUP_BOOK} from "../graphql/subscriptions";

function NotiAboutBookOfPickedUpPage() {
  const {data, loading} = useSubscription(NOTIFY_PICKUP_BOOK);

  if (loading) {
    return <p>Loading...</p>;
  }

  return (
      <div>
        <h2>Notification About Book of Picked Up</h2>
        {data && data.notifyPickupBook ? (
            <p>{data.notifyPickupBook}</p>
        ) : (
            <p>There is No Book Picked Up!</p>
        )}
      </div>
  );
}

export default NotiAboutBookOfPickedUpPage;
