// src/pages/Feedback.jsx
import React, { useState } from 'react';
import { submitFeedback } from '../services/api';
import { toast } from 'react-toastify';
import './Feedback.css';

function Feedback() {
  const [form, setForm] = useState({ message: '', rating: 0 });
  const [hoveredStar, setHoveredStar] = useState(0);
  const [loading, setLoading] = useState(false);
  const [submitted, setSubmitted] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (form.rating === 0) { toast.warning('Please select a rating.'); return; }
    if (!form.message.trim()) { toast.warning('Please write your feedback.'); return; }
    setLoading(true);
    try {
      await submitFeedback(form);
      toast.success('Thank you for your feedback!');
      setSubmitted(true);
    } catch {
      toast.error('Failed to submit feedback. Try again.');
    } finally {
      setLoading(false);
    }
  };

  if (submitted) return (
    <div className="feedback-page">
      <div className="feedback-success">
        <div className="success-icon">🎉</div>
        <h2>Thank You!</h2>
        <p>Your feedback helps us improve GoBuddy for everyone.</p>
        <button className="btn-primary" onClick={() => { setForm({ message: '', rating: 0 }); setSubmitted(false); }}>
          Submit Another
        </button>
      </div>
    </div>
  );

  return (
    <div className="feedback-page">
      <div className="page-header">
        <div className="container">
          <h1>💬 Share Your Feedback</h1>
          <p>Tell us about your experience with GoBuddy</p>
        </div>
      </div>

      <div className="container">
        <div className="feedback-card card">
          <form onSubmit={handleSubmit}>
            <div className="rating-section">
              <label className="rating-label">How would you rate GoBuddy?</label>
              <div className="stars">
                {[1, 2, 3, 4, 5].map((star) => (
                  <button
                    key={star}
                    type="button"
                    className={`star ${star <= (hoveredStar || form.rating) ? 'active' : ''}`}
                    onMouseEnter={() => setHoveredStar(star)}
                    onMouseLeave={() => setHoveredStar(0)}
                    onClick={() => setForm({ ...form, rating: star })}
                  >★</button>
                ))}
              </div>
              <div className="rating-text">
                {['', 'Poor', 'Fair', 'Good', 'Very Good', 'Excellent'][(hoveredStar || form.rating)] || 'Tap to rate'}
              </div>
            </div>

            <div className="form-group">
              <label>Your Feedback</label>
              <textarea
                rows={5}
                placeholder="Tell us what you liked, what could be improved, or any suggestions..."
                value={form.message}
                onChange={(e) => setForm({ ...form, message: e.target.value })}
                className="feedback-textarea"
              />
            </div>

            <button type="submit" className="btn-primary feedback-submit" disabled={loading}>
              {loading ? 'Submitting…' : '📤 Submit Feedback'}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}

export default Feedback;
