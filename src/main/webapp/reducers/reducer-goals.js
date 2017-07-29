import { FETCH_GOALS, CREATE_GOAL } from '../actions/index';
import _ from 'lodash';

export default function(state = [], action) {
  switch (action.type) {
    case FETCH_GOALS:
      return _.sortBy(action.payload.data, "name");
    case CREATE_GOAL:
      return _.sortBy([ ...state, action.payload.data ], "name");
    default:
      return state;
    }
}
