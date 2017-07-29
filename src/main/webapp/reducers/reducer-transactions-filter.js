import { FETCH_TRANSACTIONS } from '../actions/index';

function getNewPages(statePages, data) {
    const page = data.prev;
    const pageLocation = _.indexOf(statePages, page);
    if (pageLocation == 0){
      return statePages;
    } else if (pageLocation == -1) {
      return [ page, ...statePages ];
    } else {
      return _.drop(statePages, pageLocation);
    }
}

export default function(state = { pages: [''], filters: {} }, action) {
  switch (action.type) {
    case FETCH_TRANSACTIONS:
      const basePages = _.isEqual(action.meta.filters, state.filters) ? state.pages : ['']
      const newPages = getNewPages(basePages, action.payload.data)
      return { pages: newPages, filters: action.meta.filters };
    default:
      return state;
    }
}
