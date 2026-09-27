import { Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';

type HistoryPoint = { timestamp: string; rainfall: number; temperature: number };

export function WeatherChart({ data }: { data: HistoryPoint[] }) {
  const chartData = data.map((point) => ({
    date: new Date(point.timestamp).toLocaleDateString('en-IN', { day: 'numeric', month: 'short' }),
    rainfall: point.rainfall,
    temperature: point.temperature,
  }));

  return (
    <div className="chart-wrap">
      <ResponsiveContainer width="100%" height={220}>
        <LineChart data={chartData}>
          <XAxis dataKey="date" tickLine={false} axisLine={false} />
          <YAxis tickLine={false} axisLine={false} width={30} />
          <Tooltip />
          <Line type="monotone" dataKey="rainfall" stroke="#548da8" strokeWidth={3} dot={false} name="Rain mm" />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}
