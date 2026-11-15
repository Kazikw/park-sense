import { useEffect, useState } from 'react';
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/v1';

function App() {
  const [zones, setZones] = useState([]);
  const [zonesLoading, setZonesLoading] = useState(true);
  const [zonesError, setZonesError] = useState(null);

  const [query, setQuery] = useState('');
  const [recommendation, setRecommendation] = useState('');
  const [aiLoading, setAiLoading] = useState(false);
  const [aiError, setAiError] = useState(null);

  const fetchZones = async () => {
    try {
      setZonesLoading(true);
      setZonesError(null);
      const response = await axios.get(`${API_BASE_URL}/zones`);
      setZones(response.data);
    } catch (error) {
      setZonesError('Nie udało się pobrać listy stref parkingowych. Upewnij się, że backend działa na porcie 8080.');
    } finally {
      setZonesLoading(false);
    }
  };

  useEffect(() => {
    fetchZones();
  }, []);

  const handleAskAi = async (e) => {
    e.preventDefault();
    if (!query.trim()) {
      setAiError('Wpisz pytanie do asystenta AI.');
      return;
    }

    try {
      setAiLoading(true);
      setAiError(null);
      const response = await axios.post(`${API_BASE_URL}/ai/recommend`, {
        query: query.trim(),
      });
      setRecommendation(response.data.recommendation);
    } catch (error) {
      const backendMessage =
        error.response?.data?.validationErrors?.query ||
        error.response?.data?.message ||
        'Wystąpił błąd podczas komunikacji z asystentem AI.';
      setAiError(backendMessage);
    } finally {
      setAiLoading(false);
    }
  };

  const getProgressBarColor = (score) => {
    if (score >= 70) return 'bg-green-500';
    if (score >= 40) return 'bg-yellow-500';
    return 'bg-red-500';
  };

  const getBadgeStyle = (score) => {
    if (score >= 70) return 'bg-green-100 text-green-800 border-green-200';
    if (score >= 40) return 'bg-yellow-100 text-yellow-800 border-yellow-200';
    return 'bg-red-100 text-red-800 border-red-200';
  };

  return (
    <div className="min-h-screen bg-slate-50 text-slate-800">
      {/* Nagłówek */}
      <header className="bg-white border-b border-slate-200 shadow-sm">
        <div className="max-w-6xl mx-auto px-4 py-5 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <span className="text-3xl">🅿️</span>
            <div>
              <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Park-Sense</h1>
              <p className="text-sm text-slate-500">Inteligentny system predykcji miejsc parkingowych</p>
            </div>
          </div>
          <button
            onClick={fetchZones}
            className="px-4 py-2 text-sm font-medium text-slate-700 bg-slate-100 hover:bg-slate-200 rounded-lg transition"
          >
            Odśwież dane
          </button>
        </div>
      </header>

      <main className="max-w-6xl mx-auto px-4 py-8 space-y-10">
        {/* Sekcja 1: Zapytaj AI, gdzie zaparkować */}
        <section className="bg-white rounded-2xl shadow-md border border-slate-200 p-6 md:p-8">
          <div className="mb-4">
            <h2 className="text-xl font-bold text-slate-900 flex items-center gap-2">
              <span>🤖</span> Zapytaj AI, gdzie zaparkować
            </h2>
            <p className="text-sm text-slate-500 mt-1">
              Opisz dokąd jedziesz lub na czym Ci zależy, a nasz asystent przeanalizuje obłożenie stref.
            </p>
          </div>

          <form onSubmit={handleAskAi} className="flex flex-col sm:flex-row gap-3">
            <input
              type="text"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              placeholder="Np. Jadę do centrum na szybkie zakupy, gdzie najłatwiej zaparkuję?"
              className="flex-1 rounded-xl border border-slate-300 px-4 py-3 text-slate-900 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            />
            <button
              type="submit"
              disabled={aiLoading}
              className="px-6 py-3 rounded-xl font-semibold text-white bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 transition shadow-sm cursor-pointer"
            >
              {aiLoading ? 'Analizowanie...' : 'Zapytaj AI'}
            </button>
          </form>

          {aiError && (
            <div className="mt-4 p-4 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm">
              {aiError}
            </div>
          )}

          {recommendation && (
            <div className="mt-5 p-5 rounded-xl bg-blue-50 border border-blue-200 text-slate-800">
              <p className="text-xs font-semibold uppercase tracking-wider text-blue-700 mb-1">
                Rekomendacja Asystenta AI
              </p>
              <p className="text-base leading-relaxed">{recommendation}</p>
            </div>
          )}
        </section>

        {/* Sekcja 2: Kafelki stref parkingowych */}
        <section>
          <div className="flex items-center justify-between mb-5">
            <h2 className="text-xl font-bold text-slate-900">Dostępne strefy parkingowe</h2>
            <span className="text-sm text-slate-500">Liczba stref: {zones.length}</span>
          </div>

          {zonesLoading ? (
            <div className="p-8 text-center bg-white rounded-2xl border border-slate-200 text-slate-500">
              Ładowanie stref parkingowych...
            </div>
          ) : zonesError ? (
            <div className="p-6 bg-red-50 border border-red-200 rounded-2xl text-red-700">
              {zonesError}
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              {zones.map((zone) => (
                <div
                  key={zone.id}
                  className="bg-white rounded-2xl shadow-sm border border-slate-200 p-6 flex flex-col justify-between hover:shadow-md transition"
                >
                  <div>
                    <div className="flex items-start justify-between gap-2 mb-4">
                      <h3 className="text-lg font-bold text-slate-900">{zone.name}</h3>
                      <span
                        className={`px-2.5 py-1 text-xs font-bold rounded-full border ${getBadgeStyle(
                          zone.probabilityScore
                        )}`}
                      >
                        {zone.probabilityScore}% szans
                      </span>
                    </div>

                    <div className="space-y-2 text-sm text-slate-600 mb-6">
                      <div className="flex justify-between">
                        <span>Zaparkowani użytkownicy aplikacji:</span>
                        <span className="font-semibold text-slate-900">
                          {zone.currentAppUsersParked} / {zone.totalCapacity}
                        </span>
                      </div>
                      <div className="flex justify-between">
                        <span>Typowe obłożenie:</span>
                        <span className="font-semibold text-slate-900">{zone.typicalOccupancy} miejsc</span>
                      </div>
                      <div className="flex justify-between">
                        <span>Całkowita pojemność:</span>
                        <span className="font-semibold text-slate-900">{zone.totalCapacity} miejsc</span>
                      </div>
                    </div>
                  </div>

                  {/* Pasek postępu probabilityScore */}
                  <div>
                    <div className="flex justify-between text-xs font-medium text-slate-500 mb-1.5">
                      <span>Szansa na wolne miejsce</span>
                      <span className="font-bold text-slate-800">{zone.probabilityScore}%</span>
                    </div>
                    <div className="w-full h-3 bg-slate-200 rounded-full overflow-hidden">
                      <div
                        className={`h-full rounded-full transition-all duration-500 ${getProgressBarColor(
                          zone.probabilityScore
                        )}`}
                        style={{ width: `${zone.probabilityScore}%` }}
                      />
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </section>
      </main>
    </div>
  );
}

export default App;
