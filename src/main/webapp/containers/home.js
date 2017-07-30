import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { fetchHistory } from '../actions';
import _ from 'lodash';
import NetWorth from './networth';
import Deltas from './deltas';
import Predictions from './predictions';
import MonthProgress from './month-progress';
import { ResponsiveContainer, ComposedChart, Bar, BarChart, Line,
  XAxis, YAxis, CartesianGrid, Tooltip, Legend} from 'recharts';

const monthNames = ["Jan", "Feb ", "Mar", "Apr", "May", "Jun",
  "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
];
class Home extends Component {
  componentDidMount() {
    this.props.fetchHistory();
  }

  render() {
    return (
      <div>
        <table className="home-history">
          <tbody>
            <tr>
              <td className="graph">
                <h4>Net Worth Tracking</h4>
                <NetWorth />
              </td>
              <td className="graph">
                <h4>Monthly Deltas</h4>
                <Deltas />
              </td>
            </tr>
          </tbody>
        </table>
        <br/>
        <table className="home-month">
          <tbody>
            <tr>
              <td>
                <h4>Current Budget Standings</h4>
                <MonthProgress />
              </td>
              <td className="predictions">
                <h4>Predictions</h4>
                <Predictions />
              </td>
            </tr>
          </tbody>
        </table>
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

export default connect(mapStateToProps, mapDispatchToProps)(Home);
