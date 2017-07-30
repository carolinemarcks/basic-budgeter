import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { fetchHistory } from '../actions';
import _ from 'lodash';
import NetWorth from './networth';
import Deltas from './deltas';
import MonthProgress from './month-progress';
import { ResponsiveContainer, ComposedChart, Bar, BarChart, Line,
  XAxis, YAxis, CartesianGrid, Tooltip, Legend} from 'recharts';

const monthNames = ["Jan", "Feb ", "Mar", "Apr", "May", "Jun",
  "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
];
class History extends Component {
  componentDidMount() {
    this.props.fetchHistory();
  }

  render() {
    return (
      <div>
        <table className="history">
          <tbody>
            <tr>
              <td>
                <h4>Net Worth Tracking</h4>
                <NetWorth />
              </td>
              <td>
                <h4>Monthly Deltas</h4>
                <Deltas />
              </td>
            </tr>
          </tbody>
        </table>
        <MonthProgress />
      </div>
    );
  }
}

function mapStateToProps({ history }){
  return { history };
}

function mapDispatchToProps(dispatch) {
  return bindActionCreators({ fetchHistory }, dispatch);
}

export default connect(mapStateToProps, mapDispatchToProps)(History);
