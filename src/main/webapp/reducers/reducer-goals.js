import { FETCH_GOALS, CREATE_GOAL } from '../actions/index';
import _ from 'lodash';

export default function(state = [], action) {
  switch (action.type) {
    case FETCH_GOALS:
      return _.orderBy(action.payload.data, ["weight", "name"], ["desc", "asc"]);
    case CREATE_GOAL:
      return _.orderBy([ ...state, action.payload.data ], ["weight", "name"], ["desc", "asc"]);
    default:
      return state;
    }
}
