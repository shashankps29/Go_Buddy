// src/pages/AdminDashboard.jsx
import React, { useState, useEffect } from 'react';
import { getAllUsers, getDashboardStats, deleteUser, getAllFeedback } from '../services/api';
import { toast } from 'react-toastify';
import './AdminDashboard.css';

function StatCard({ icon, label, value, color }) {
  return (
    <div className="stat-card" style={{ borderTop: `4px solid ${color}` }}>
      <div className="stat-card-icon" style={{ background: `${color}18` }}>{icon}</div>
      <div>
        <div className="stat-card-value">{value ?? '—'}</div>
        <div className="stat-card-label">{label}</div>
      </div>
    </div>
  );
}

function AdminDashboard() {
  const [stats, setStats]     = useState({});
  const [users, setUsers]     = useState([]);
  const [feedbacks, setFeedbacks] = useState([]);
  const [tab, setTab]         = useState('overview');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([getDashboardStats(), getAllUsers(), getAllFeedback()])
      .then(([s, u, f]) => {
        setStats(s.data || {});
        setUsers(u.data || []);
        setFeedbacks(f.data || []);
      })
      .catch(() => toast.error('Failed to load admin data.'))
      .finally(() => setLoading(false));
  }, []);

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this user permanently?')) return;
    try {
      await deleteUser(id);
      setUsers(users.filter((u) => u.userId !== id));
      toast.success('User deleted.');
    } catch {
      toast.error('Failed to delete user.');
    }
  };

  if (loading) return (
    <div className="loading-spinner">
      <div className="spinner"></div> Loading dashboard…
    </div>
  );

  return (
    <div className="admin-page">
      <div className="page-header">
        <div className="container">
          <h1>🛡️ Admin Dashboard</h1>
          <p>Manage users, monitor activity, and review feedback</p>
        </div>
      </div>

      <div className="container admin-body">
        {/* Tab Nav */}
        <div className="admin-tabs">
          {['overview', 'users', 'feedback'].map((t) => (
            <button key={t} className={`admin-tab ${tab === t ? 'active' : ''}`} onClick={() => setTab(t)}>
              {{ overview: '📊 Overview', users: '👥 Users', feedback: '💬 Feedback' }[t]}
            </button>
          ))}
        </div>

        {/* Overview */}
        {tab === 'overview' && (
          <div className="overview-grid">
            <StatCard icon="👥" label="Total Users"    value={stats.totalUsers}    color="#1a73e8" />
            <StatCard icon="🔍" label="Total Searches" value={stats.totalSearches} color="#34a853" />
            <StatCard icon="💬" label="Feedbacks"      value={stats.totalFeedbacks} color="#fbbc04" />
            <StatCard icon="⭐" label="Avg Rating"     value={stats.averageRating ? `${stats.averageRating}/5` : '—'} color="#ff6b35" />
          </div>
        )}

        {/* Users Table */}
        {tab === 'users' && (
          <div className="card">
            <h3 className="table-heading">Registered Users ({users.length})</h3>
            <div className="table-wrap">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>#</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Joined</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {users.length === 0 ? (
                    <tr><td colSpan={6} className="empty-row">No users found.</td></tr>
                  ) : users.map((u, i) => (
                    <tr key={u.userId}>
                      <td>{i + 1}</td>
                      <td><strong>{u.name}</strong></td>
                      <td>{u.email}</td>
                      <td>{u.phoneNumber || '—'}</td>
                      <td>{new Date(u.createdAt).toLocaleDateString()}</td>
                      <td>
                        <button className="delete-btn" onClick={() => handleDelete(u.userId)}>
                          🗑 Delete
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* Feedback Table */}
        {tab === 'feedback' && (
          <div className="card">
            <h3 className="table-heading">User Feedbacks ({feedbacks.length})</h3>
            <div className="table-wrap">
              <table className="admin-table">
                <thead>
                  <tr><th>#</th><th>User</th><th>Rating</th><th>Message</th><th>Date</th></tr>
                </thead>
                <tbody>
                  {feedbacks.length === 0 ? (
                    <tr><td colSpan={5} className="empty-row">No feedback submitted yet.</td></tr>
                  ) : feedbacks.map((f, i) => (
                    <tr key={f.feedbackId}>
                      <td>{i + 1}</td>
                      <td>{f.userName || '—'}</td>
                      <td>
                        <span className="stars-display">
                          {'★'.repeat(f.rating)}{'☆'.repeat(5 - f.rating)}
                        </span>
                      </td>
                      <td className="feedback-msg">{f.message}</td>
                      <td>{new Date(f.createdAt).toLocaleDateString()}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default AdminDashboard;
