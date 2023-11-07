import React from "react";
import {useSubscription} from "@apollo/client";
import {SUBSCRIBE_PICKUP_BOOK_NOTI_CHANNEL} from "../graphql/subscriptions";

function NotiAboutBookOfPickedUpPage() {
  const {data, loading} = useSubscription(SUBSCRIBE_PICKUP_BOOK_NOTI_CHANNEL);

  if (loading) {
    return <p>Loading...</p>;
  }

  return (
      <div>
        <h2>Notification About Book of Picked Up</h2>
        {data && data.subscribePickupBookNotiChannel ? (
            <p>{data.subscribePickupBookNotiChannel}</p>
        ) : (
            <p>There is No Book Picked Up!</p>
        )}
      </div>
  );
}

export default NotiAboutBookOfPickedUpPage;
