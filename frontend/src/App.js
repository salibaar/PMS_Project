import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import PlansListPage from './pages/PlansListPage';
import PlanDetailPage from './pages/PlanDetailPage';
import CreatePlanPage from './pages/CreatePlanPage';
import HomePage from './pages/HomePage';

function App() {
  return (
    <Router>
      <div className="App">
        <nav style={styles.nav}>
          <div style={styles.navContainer}>
            <Link to="/" style={styles.brand}>
              <span style={styles.brandIcon}>🚩</span>
              <span>Hệ Thống Quản Lý Kế Hoạch (PMS)</span>
            </Link>
            <div style={styles.navLinks}>
              <Link to="/plans" style={styles.navLink}>📋 Kế Hoạch</Link>
              <Link to="/plans/create" style={styles.navLink}>➕ Tạo Mới</Link>
            </div>
          </div>
        </nav>

        <main style={styles.main}>
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/plans" element={<PlansListPage />} />
            <Route path="/plans/create" element={<CreatePlanPage />} />
            <Route path="/plans/:id" element={<PlanDetailPage />} />
          </Routes>
        </main>

        <footer style={styles.footer}>
          <p>© 2025 Planning Management System - Version 1.0</p>
        </footer>
      </div>
    </Router>
  );
}

const styles = {
  nav: {
    backgroundColor: '#2c3e50',
    color: 'white',
    padding: '15px 0',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  },
  navContainer: {
    maxWidth: '1200px',
    margin: '0 auto',
    padding: '0 20px',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  brand: {
    textDecoration: 'none',
    color: 'white',
    fontSize: '20px',
    fontWeight: 'bold',
    display: 'flex',
    alignItems: 'center',
    gap: '10px',
  },
  brandIcon: {
    fontSize: '24px',
  },
  navLinks: {
    display: 'flex',
    gap: '20px',
  },
  navLink: {
    textDecoration: 'none',
    color: 'white',
    padding: '8px 16px',
    borderRadius: '4px',
    transition: 'background 0.3s',
    fontSize: '14px',
  },
  main: {
    minHeight: 'calc(100vh - 120px)',
    backgroundColor: '#f4f6f8',
    padding: '20px',
  },
  footer: {
    backgroundColor: '#2c3e50',
    color: 'white',
    textAlign: 'center',
    padding: '20px',
    marginTop: 'auto',
  },
};

export default App;