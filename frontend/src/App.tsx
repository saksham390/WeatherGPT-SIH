import { useEffect, useState } from 'react';
import type { FormEvent } from 'react';
import { WeatherChart } from './components/WeatherChart';

type HealthState = 'checking' | 'online' | 'offline';
type Weather = {
  location: string;
  temperature: number;
  feelsLike: number;
  humidity: number;
  windSpeed: number;
  rainfall: number;
  precipitationProbability: number;
  weatherCondition: string;
  demoData: boolean;
};
type Forecast = { date: string; maxTemperature: number; rainfall: number; precipitationProbability: number; weatherCondition: string };
type Alert = { severity: string; title: string; description: string; recommendedActions: string[] };

function App() {
  const [health, setHealth] = useState<HealthState>('checking');
  const [weather, setWeather] = useState<Weather | null>(null);
  const [forecast, setForecast] = useState<Forecast[]>([]);
  const [alerts, setAlerts] = useState<Alert[]>([]);
  const [error, setError] = useState('');
  const [question, setQuestion] = useState('');
  const [answer, setAnswer] = useState('');
  const [chatBusy, setChatBusy] = useState(false);
  const [language, setLanguage] = useState<'en' | 'hi'>('en');
  const [history, setHistory] = useState<{ timestamp: string; rainfall: number; temperature: number }[]>([]);
  const [crop, setCrop] = useState('Wheat');
  const [growthStage, setGrowthStage] = useState('');
  const [soilType, setSoilType] = useState('');
  const [advisory, setAdvisory] = useState<{ weatherForecast: string; potentialImpact: string; recommendedActions: string[]; warning: string } | null>(null);

  useEffect(() => {
    Promise.all([
      fetch('/api/health'),
      fetch('/api/weather/current?location=Delhi'),
      fetch('/api/weather/forecast?location=Delhi&days=5'),
      fetch('/api/weather/alerts?location=Delhi'),
    ])
      .then(async ([healthResponse, weatherResponse, forecastResponse, alertsResponse]) => {
        if (![healthResponse, weatherResponse, forecastResponse, alertsResponse].every((response) => response.ok)) {
          throw new Error('Weather service unavailable');
        }
        setHealth('online');
        setWeather(await weatherResponse.json());
        setForecast(await forecastResponse.json());
        setAlerts(await alertsResponse.json());
      })
      .catch(() => {
        setHealth('offline');
        setError("I couldn't retrieve demo weather data right now. Start the Spring Boot backend and try again.");
      });
  }, []);

  useEffect(() => {
    fetch('/api/weather/history?location=Delhi')
      .then((response) => response.json())
      .then(setHistory)
      .catch(() => setHistory([]));
  }, []);

  async function askWeather(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!question.trim()) return;
    setChatBusy(true);
    setAnswer('');
    try {
      const response = await fetch('/api/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ sessionId: 'dashboard-demo', message: question, language }),
      });
      if (!response.ok) throw new Error('Chat request failed');
      const result = await response.json();
      setAnswer(result.message);
    } catch {
      setAnswer("I couldn't reach the assistant right now. Please try again shortly.");
    } finally {
      setChatBusy(false);
    }
  }

  async function requestAdvisory(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const response = await fetch('/api/advisory/farmer', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ location: 'Delhi', crop, growthStage, soilType, language: 'en' }),
    });
    if (response.ok) setAdvisory(await response.json());
  }

  return (
    <main className="app-shell">
      <section className="hero-panel">
        <p className="eyebrow">Weather intelligence assistant</p>
        <h1>WeatherGPT</h1>
        <p className="hero-copy">Weather signals, safety context, and practical decisions in one calm view.</p>
        <div className="status-row" aria-live="polite">
          <span className={`status-dot status-${health}`} />
          <span>
            {health === 'checking' && 'Connecting to the backend...'}
            {health === 'online' && 'Backend is online'}
            {health === 'offline' && 'Backend is offline'}
          </span>
        </div>
      </section>

      {error && <p className="error-banner">{error}</p>}

      {weather && (
        <section className="dashboard-grid" aria-label="Weather dashboard">
          <article className="weather-panel current-panel">
            <div className="panel-heading"><span className="panel-index">Current conditions</span><span className="source-badge">{weather.demoData ? 'Demo data' : 'Live data'}</span></div>
            <p className="location-label">{weather.location}</p>
            <div className="temperature">{Math.round(weather.temperature)}<span>°C</span></div>
            <p className="condition">{weather.weatherCondition} · feels like {Math.round(weather.feelsLike)}°C</p>
            <div className="metrics"><span><strong>{weather.humidity}%</strong> humidity</span><span><strong>{weather.windSpeed} km/h</strong> wind</span><span><strong>{weather.precipitationProbability}%</strong> rain chance</span></div>
          </article>
          <article className="weather-panel forecast-panel">
            <div className="panel-heading"><span className="panel-index">Five-day outlook</span></div>
            <div className="forecast-list">{forecast.map((day) => <div className="forecast-row" key={day.date}><strong>{new Date(`${day.date}T00:00:00`).toLocaleDateString('en-IN', { weekday: 'short' })}</strong><span>{day.weatherCondition}</span><span>{day.precipitationProbability}% / {day.maxTemperature}°C</span></div>)}</div>
          </article>
          <article className="weather-panel alerts-panel">
            <div className="panel-heading"><span className="panel-index">Active alerts</span></div>
            {alerts.length === 0 ? <p className="quiet-message">No deterministic alerts for this demo snapshot.</p> : alerts.map((alert) => <div className="alert-item" key={alert.title}><strong>{alert.title}</strong><span>{alert.severity}</span><p>{alert.description}</p></div>)}
          </article>
        </section>
      )}

      {!weather && !error && <p className="loading-message">Loading the weather snapshot...</p>}

      <section className="chat-panel" aria-label="Weather chat">
        <div>
          <span className="panel-index">Ask WeatherGPT</span>
          <h2>What do you want to know?</h2>
          <p className="chat-note">With Gemini enabled, LangChain4j can choose the weather tools for the question.</p>
        </div>
        <form className="chat-form" onSubmit={askWeather}>
          <input value={question} onChange={(event) => setQuestion(event.target.value)} placeholder="Will it rain tomorrow in Delhi?" aria-label="Weather question" />
          <select value={language} onChange={(event) => setLanguage(event.target.value as 'en' | 'hi')} aria-label="Response language"><option value="en">English</option><option value="hi">Hindi</option></select>
          <button type="submit" disabled={chatBusy}>{chatBusy ? 'Checking...' : 'Ask'}</button>
        </form>
        {answer && <div className="chat-answer" aria-live="polite"><strong>WeatherGPT</strong><p>{answer}</p></div>}
      </section>

      <section className="weather-panel history-panel" aria-label="Historical rainfall">
        <div className="panel-heading"><span className="panel-index">Rainfall trend</span><span className="source-badge dark-badge">Demo data</span></div>
        {history.length > 0 ? <WeatherChart data={history} /> : <p className="quiet-message">Historical data unavailable.</p>}
      </section>

      <section className="farmer-panel" aria-label="Farmer mode">
        <div><span className="panel-index">Farmer mode</span><h2>Make the forecast useful for a crop</h2><p className="chat-note">This demo combines tomorrow's forecast with cautious agriculture guidance.</p></div>
        <form className="farmer-form" onSubmit={requestAdvisory}>
          <label>Crop<input value={crop} onChange={(event) => setCrop(event.target.value)} /></label>
          <label>Growth stage<input value={growthStage} onChange={(event) => setGrowthStage(event.target.value)} placeholder="Optional" /></label>
          <label>Soil type<input value={soilType} onChange={(event) => setSoilType(event.target.value)} placeholder="Optional" /></label>
          <button type="submit">Prepare advisory</button>
        </form>
        {advisory && <div className="advisory-result"><strong>Forecast</strong><p>{advisory.weatherForecast}</p><strong>Potential impact</strong><p>{advisory.potentialImpact}</p><strong>Recommended actions</strong><ul>{advisory.recommendedActions.map((action) => <li key={action}>{action}</li>)}</ul><small>{advisory.warning}</small></div>}
      </section>

      <section className="content-grid" aria-label="WeatherGPT roadmap">
        <article className="feature-panel accent-sky"><span className="panel-index">Next</span><h2>Ask naturally</h2><p>LangChain4j will connect conversational questions to weather tools.</p></article>
        <article className="feature-panel accent-gold"><span className="panel-index">Then</span><h2>Ground answers</h2><p>RAG will retrieve safety and agriculture guidance from trusted documents.</p></article>
        <article className="feature-panel accent-coral"><span className="panel-index">Always</span><h2>Show the source</h2><p>Demo, live, and rule-generated signals stay clearly distinguished.</p></article>
      </section>
    </main>
  );
}

export default App;
