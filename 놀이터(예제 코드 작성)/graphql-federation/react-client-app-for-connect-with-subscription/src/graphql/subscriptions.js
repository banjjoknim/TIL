import {gql} from "@apollo/client";

export const NOTIFY_PICKUP_BOOK = gql`
  subscription NotifyPickupBook {
      notifyPickupBook
  }
`
