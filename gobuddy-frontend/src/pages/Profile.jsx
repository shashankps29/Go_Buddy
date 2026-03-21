// src/pages/Profile.jsx
import React, { useState, useEffect } from 'react';
import { getUserProfile, updateUserProfile, getSearchHistory } from '../services/api';
import { toast } from 'react-toastify';
import './Profile.css';

function Profile() {
  const [profile, setProfile]   = useState({ name: '', email: '', phoneNumber: '' });
  const [history, setHistory]   = useState([]);
  const [editing, setEditing]   = useState(false);
  const [loading, setLoading]   = useState(true);

  useEffect(() => {
    Promise.all([getUserProfile(), getSearchHistory()])
      .then(([p, h]) => {
        setProfile(p.data);
        setHistory(h.data || []);
      })
      .catch(() => toast.error('Failed to load profile.'))
      .finally(() => setLoading(false));
  }, []);

  const handleSave = async () => {
    try {
      await updateUserProfile({ name: profile.name, phoneNumber: profile.phoneNumber });
      localStorage.setItem('name', profile.name);
      toast.success('Profile updated!');
      setEditing(false);
    } catch {
      toast.error('Update failed.');
    }
  };

  if (loading) return <div className="loading-spinner"><div className="spinner"></div> Loading profile…</div>;

  return (
    <div className="profile-page">
      <div className="page-header">
        <div className="container">
          <h1>👤 My Profile</h1>
          <p>Manage your account and view travel history</p>
        </div>
      </div>

      <div className="container profile-body">
        {/* Profile Card */}
        <div className="card profile-card">
          <div className="profile-avatar">{profile.name?.[0]?.toUpperCase()}</div>
          <div className="profile-fields">
            <div className="form-group">
              <label>Full Name</label>
              <input type="text" value={profile.name} disabled={!editing}
                onChange={(e) => setProfile({ ...profile, name: e.target.value })} />
            </div>
            <div className="form-group">
              <label>Email Address</label>
              <input type="email" value={profile.email} disabled />
            </div>
            <div className="form-group">
              <label>Phone Number</label>
              <input type="tel" value={profile.phoneNumber || ''} disabled={!editing}
                onChange={(e) => setProfile({ ...profile, phoneNumber: e.target.value })} />
            </div>
          </div>
          <div className="profile-actions">
            {editing ? (
              <>
                <button className="btn-primary" onClick={handleSave}>Save Changes</button>
                <button className="btn-outline" onClick={() => setEditing(false)}>Cancel</button>
              </>
            ) : (
              <button className="btn-primary" onClick={() => setEditing(true)}>✏️ Edit Profile</button>
            )}
          </div>
        </div>

        {/* Search History */}
        <div className="card history-card">
          <h3>🕘 Recent Searches</h3>
          {history.length === 0
            ? <p className="no-history">No search history yet. Start by searching a route!</p>
            : (
              <table className="history-table">
                <thead>
                  <tr><th>From</th><th>To</th><th>Date</th><th>Searched On</th></tr>
                </thead>
                <tbody>
                  {history.map((h, i) => (
                    <tr key={i}>
                      <td>{h.fromLocation}</td>
                      <td>{h.toLocation}</td>
                      <td>{h.travelDate}</td>
                      <td>{new Date(h.createdAt).toLocaleDateString()}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )
          }
        </div>
      </div>
    </div>
  );
}

export default Profile;
