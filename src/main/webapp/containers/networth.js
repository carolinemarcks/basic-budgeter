import React, { Component } from 'react';
import { connect } from 'react-redux';
import _ from 'lodash';
import { ResponsiveContainer, ComposedChart, Bar, BarChart, Line,
  XAxis, YAxis, CartesianGrid, Tooltip, Legend} from 'recharts';

const monthNames = ["Jan", "Feb ", "Mar", "Apr", "May", "Jun",
  "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
];
class NetWorth extends Component {
  render() {
    const data = this.props.history.map(({month, net}) => {
      return {
        month: monthNames[month-1],
        net: net / 100
      }
    });

    return (
      <ResponsiveContainer width="100%" height={300}>
        <BarChart barSize={20} data={data} margin={{top: 20, right: 0, bottom: 0, left: 0}}>
         <XAxis dataKey="month"/>
         <YAxis orientation="left" stroke="#404E4D"/>
         <CartesianGrid strokeDasharray="3 3"/>
         <Tooltip/>
         <Bar stackId="stack" dataKey="net" fill="#20A4F3" />
        </BarChart>
      </ResponsiveContainer>
    );
  }
}

function mapStateToProps({ history }){
  return { history };
}

export default connect(mapStateToProps)(NetWorth);
