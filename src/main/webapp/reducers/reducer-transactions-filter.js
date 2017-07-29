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

export default function(state = { pages: [''] }, action) {
  switch (action.type) {
    case FETCH_TRANSACTIONS:
      const newPages = getNewPages(state.pages, action.payload.data)
      return { pages: newPages };
    default:
      return state;
    }
}
