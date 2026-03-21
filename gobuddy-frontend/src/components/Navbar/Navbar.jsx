// src/components/Navbar/Navbar.jsx
import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import './Navbar.css';

function Navbar() {
  const navigate = useNavigate();
  const location = useLocation();
  const [menuOpen, setMenuOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);

  const token = localStorage.getItem('token');
  const role  = localStorage.getItem('role');
  const name  = localStorage.getItem('name');

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 20);
    window.addEventListener('scroll', onScroll);
    return () => window.removeEventListener('scroll', onScroll);
  }, []);

  const handleLogout = () => {
    localStorage.clear();
    navigate('/login');
  };

  const isActive = (path) => location.pathname === path;

  return (
    <nav className={`navbar ${scrolled ? 'scrolled' : ''}`}>
      <div className="navbar-inner container">
        {/* Logo */}
        <Link to="/" className="navbar-logo">
          <span className="logo-icon">✈</span>
          <span className="logo-text">Go<span className="logo-buddy">Buddy</span></span>
        </Link>

        {/* Desktop Nav Links */}
        <ul className={`navbar-links ${menuOpen ? 'open' : ''}`}>
          <li><Link to="/"          className={isActive('/')         ? 'active' : ''} onClick={() => setMenuOpen(false)}>Home</Link></li>
          <li><Link to="/search"    className={isActive('/search')   ? 'active' : ''} onClick={() => setMenuOpen(false)}>Search</Link></li>
          <li><Link to="/weather"   className={isActive('/weather')  ? 'active' : ''} onClick={() => setMenuOpen(false)}>🌤️ Weather</Link></li>
          <li><Link to="/food"      className={isActive('/food')     ? 'active' : ''} onClick={() => setMenuOpen(false)}>🍽️ Food</Link></li>
          {token && (
            <>
              <li><Link to="/profile"  className={isActive('/profile')  ? 'active' : ''} onClick={() => setMenuOpen(false)}>Profile</Link></li>
              <li><Link to="/feedback" className={isActive('/feedback') ? 'active' : ''} onClick={() => setMenuOpen(false)}>Feedback</Link></li>
            </>
          )}
          {role === 'admin' && (
            <li><Link to="/admin" className={isActive('/admin') ? 'active' : ''} onClick={() => setMenuOpen(false)}>Admin</Link></li>
          )}
        </ul>

        {/* Auth Buttons */}
        <div className="navbar-auth">
          {token ? (
            <div className="user-menu">
              <span className="user-avatar">{name?.[0]?.toUpperCase() || 'U'}</span>
              <span className="user-name">{name}</span>
              <button className="btn-logout" onClick={handleLogout}>Logout</button>
            </div>
          ) : (
            <div className="auth-buttons">
              <Link to="/login"    className="btn-nav-outline">Login</Link>
              <Link to="/register" className="btn-nav-primary">Sign Up</Link>
            </div>
          )}
        </div>

        {/* Hamburger */}
        <button className="hamburger" onClick={() => setMenuOpen(!menuOpen)} aria-label="Toggle menu">
          <span></span><span></span><span></span>
        </button>
      </div>
    </nav>
  );
}

export default Navbar;
