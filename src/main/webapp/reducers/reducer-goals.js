import { FETCH_GOALS } from '../actions/index';

export default function(state = [], action) {
  switch (action.type) {
    case FETCH_GOALS:
      return action.payload.data;
    default:
      return state;
    }
}
