import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { ReferenceLine, ResponsiveContainer, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';
import _ from 'lodash';


class CustomTooltip extends Component {

  render() {
    const { active } = this.props;

    if (active) {
      if (this.props.payload) {
        const { amount, percent } = this.props.payload[0].payload;
        return (
          <div className="custom-tooltip">
            <p className="tooltip-label">{`${percent.toFixed(0)}% left of $${amount}`}</p>
          </div>
        );
      }
    }

    return null;
  }
}

CustomTooltip.propTypes = {
  type: PropTypes.string,
  payload: PropTypes.array,
  label: PropTypes.string,
}

const CustomizedLabel = function(props) {
  const { x, y, width, index, stroke, data, value } = props;
  const content = `$${data[index].saved.toFixed(0)}`;
  const shouldShow = (value != 0) || data[index].saved === 0;
  if (shouldShow) return <text x={x+ width + 10} y={y} dy={-4} fill={stroke} fontSize={10} textAnchor="right">{content}</text>;
  else return null;
}

class MonthProgress extends Component {
  render() {
    const dataMin = _.reduce(this.props.budgets, function(min, d) {
      const {saved, amount} = d
      if (amount && saved) {
        const percent = saved * 100 / amount;
        return Math.min(percent, min);
      } else return min;
    }, 0);
    const data = _.sortBy(_.flatten(this.props.budgets.map(({name, saved, amount}) => {
      if (amount || saved) {
        const percent = saved * 100 / amount;
        const extrapos = percent > 100 ? 100 : 0
        const extraneg = saved < 0 && !amount ? dataMin : 0
        const posp = (!extrapos && saved > 0) ? percent : 0;
        const negp = (!extraneg && saved < 0) ? percent : 0;
        return [{
          name,
          saved: saved / 100,
          amount: amount / 100,
          extraneg,
          extrapos,
          posp,
          negp,
          percent
        }]
      } else {
        return [];
      }
    })), ({percent}) => { return percent });
    return (
        <ResponsiveContainer width="100%" height={800}>
          <BarChart
            barSize={20}
            barCategoryGap="100%"
            data={data}
            layout="vertical"
            margin={{top: 5, right: 0, left:125, bottom: 5}}>
            <XAxis type="number" hide={true} tickLine={false} axisLine={false} tick={false} domain={['dataMin - 1', 110]}/>
            <YAxis type="category" dataKey="name" hide={true} tickLine={false} interval={0} />
            <ReferenceLine x={0} stroke="#404E4D" />
            <ReferenceLine x={101} stroke="#404E4D" strokeDasharray="3 3" />
            <Tooltip content={<CustomTooltip />}/>
            <Bar stackId="stack" dataKey="posp" fill="#9BC53D" label={<CustomizedLabel data={data} />} />
            <Bar stackId="stack" dataKey="negp" fill="#C3423F" label={<CustomizedLabel data={data} />} />
            <Bar stackId="stack" dataKey="extrapos" fill="#6c892a" label={<CustomizedLabel data={data} />} />
            <Bar stackId="stack" dataKey="extraneg" fill="#61211f" label={<CustomizedLabel data={data} />} />
          </BarChart>
        </ResponsiveContainer>
    );
  }
}

function mapStateToProps({ budgets }){
  return { budgets };
}

export default connect(mapStateToProps)(MonthProgress);
