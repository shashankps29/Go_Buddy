// src/pages/SearchResults.jsx
import React, { useState, useEffect, useCallback } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { searchTravel } from '../services/api';
import './SearchResults.css';

const TYPE_ICONS = { BUS: '🚌', TRAIN: '🚆', FLIGHT: '✈️' };
const TYPE_LABELS = { BUS: 'Bus', TRAIN: 'Train', FLIGHT: 'Flight' };

function ResultCard({ item }) {
  return (
    <div className={`result-card result-card--${item.type.toLowerCase()}`}>
      <div className="result-left">
        <div className="result-type-badge">
          <span>{TYPE_ICONS[item.type]}</span>
          <span>{TYPE_LABELS[item.type]}</span>
        </div>
        <div className="result-provider">{item.providerName}</div>
      </div>

      <div className="result-timing">
        <div className="time-block">
          <div className="time">{item.departureTime || '—'}</div>
          <div className="city">{item.from}</div>
        </div>
        <div className="duration-block">
          <div className="duration-line"></div>
          <div className="duration-text">{item.duration}</div>
        </div>
        <div className="time-block">
          <div className="time">{item.arrivalTime || '—'}</div>
          <div className="city">{item.to}</div>
        </div>
      </div>

      <div className="result-right">
        <div className="result-price">₹{item.price?.toLocaleString('en-IN')}</div>
        <div className="result-per">per person</div>
        <a href={item.redirectUrl} target="_blank" rel="noreferrer" className="book-btn">
          Book Now ↗
        </a>
      </div>
    </div>
  );
}

function SearchResults() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const from = searchParams.get('from') || '';
  const to   = searchParams.get('to')   || '';
  const date = searchParams.get('date') || '';
  const type = searchParams.get('type') || 'ALL';

  const [results, setResults]   = useState([]);
  const [loading, setLoading]   = useState(false);
  const [error, setError]       = useState('');
  const [activeFilter, setActiveFilter] = useState('ALL');
  const [sortBy, setSortBy]     = useState('price');

  const fetchResults = useCallback(async () => {
    if (!from || !to || !date) return;
    setLoading(true);
    setError('');
    try {
      const res = await searchTravel({ from, to, date, type });
      setResults(res.data || []);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to fetch results. Please try again.');
      setResults([]);
    } finally {
      setLoading(false);
    }
  }, [from, to, date, type]);

  useEffect(() => { fetchResults(); }, [fetchResults]);

  const filtered = results
    .filter((r) => activeFilter === 'ALL' || r.type === activeFilter)
    .sort((a, b) => {
      if (sortBy === 'price')    return a.price - b.price;
      if (sortBy === 'duration') return a.duration?.localeCompare(b.duration);
      return 0;
    });

  const counts = {
    ALL:    results.length,
    BUS:    results.filter((r) => r.type === 'BUS').length,
    TRAIN:  results.filter((r) => r.type === 'TRAIN').length,
    FLIGHT: results.filter((r) => r.type === 'FLIGHT').length,
  };

  return (
    <div className="results-page">
      {/* Header bar */}
      <div className="results-header">
        <div className="container results-header-inner">
          <div className="route-summary">
            <span className="city-from">{from}</span>
            <span className="route-arrow">→</span>
            <span className="city-to">{to}</span>
            <span className="route-date">{new Date(date).toDateString()}</span>
          </div>
          <button className="modify-btn" onClick={() => navigate('/')}>✏️ Modify Search</button>
        </div>
      </div>

      <div className="container results-body">
        {/* Sidebar Filters */}
        <aside className="filter-sidebar">
          <h3 className="filter-title">Filter by Mode</h3>
          {['ALL', 'BUS', 'TRAIN', 'FLIGHT'].map((f) => (
            <button
              key={f}
              className={`filter-btn ${activeFilter === f ? 'active' : ''}`}
              onClick={() => setActiveFilter(f)}
            >
              <span>{f === 'ALL' ? '🌐' : TYPE_ICONS[f]}</span>
              <span>{f === 'ALL' ? 'All Modes' : TYPE_LABELS[f]}</span>
              <span className="filter-count">{counts[f]}</span>
            </button>
          ))}

          <h3 className="filter-title" style={{ marginTop: '24px' }}>Sort By</h3>
          <select className="sort-select" value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
            <option value="price">Lowest Price</option>
            <option value="duration">Shortest Duration</option>
          </select>
        </aside>

        {/* Results */}
        <main className="results-main">
          {loading && (
            <div className="loading-spinner">
              <div className="spinner"></div>
              <span>Searching across Bus, Train & Flight APIs...</span>
            </div>
          )}
          {error && !loading && (
            <div className="error-box">⚠️ {error}</div>
          )}
          {!loading && !error && filtered.length === 0 && (
            <div className="empty-state">
              <div style={{ fontSize: 56 }}>🔍</div>
              <h3>No results found</h3>
              <p>Try changing the travel mode or date.</p>
            </div>
          )}
          {!loading && filtered.map((item, i) => (
            <ResultCard key={i} item={item} />
          ))}
        </main>
      </div>
    </div>
  );
}

export default SearchResults;
