import React from 'react';
import { Link } from 'react-router-dom';

function HomePage() {
  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <h1 style={styles.title}>🚩 Hệ Thống Quản Lý Kế Hoạch</h1>
        <p style={styles.subtitle}>Performance Management System (PMS)</p>
        
        <div style={styles.description}>
          <h2 style={styles.sectionTitle}>Tính năng chính:</h2>
          <ul style={styles.featureList}>
            <li>✅ Quản lý Kế hoạch (Plans) theo năm</li>
            <li>✅ Quản lý Nhiệm vụ (Objectives) với cấu trúc phân cấp</li>
            <li>✅ Quản lý Mục tiêu then chốt (Key Results)</li>
            <li>✅ Hỗ trợ nhiệm vụ đột phá (Breakthrough Objectives)</li>
            <li>✅ Tìm kiếm và lọc dữ liệu</li>
            <li>✅ Phân trang và sắp xếp</li>
          </ul>
        </div>

        <div style={styles.actions}>
          <Link to="/plans" style={styles.primaryButton}>
            📋 Xem Danh Sách Kế Hoạch
          </Link>
          <Link to="/plans/create" style={styles.secondaryButton}>
            ➕ Tạo Kế Hoạch Mới
          </Link>
        </div>

        <div style={styles.info}>
          <p>📌 <strong>Phase 1:</strong> Foundation & Core CRUD</p>
          <p>🔧 <strong>Version:</strong> 1.0.0</p>
          <p>🗓️ <strong>Release:</strong> December 2024</p>
        </div>
      </div>
    </div>
  );
}

const styles = {
  container: {
    maxWidth: '800px',
    margin: '40px auto',
    padding: '0 20px',
  },
  card: {
    backgroundColor: 'white',
    borderRadius: '12px',
    padding: '40px',
    boxShadow: '0 4px 12px rgba(0,0,0,0.1)',
  },
  title: {
    color: '#2c3e50',
    marginBottom: '10px',
    fontSize: '32px',
    textAlign: 'center',
  },
  subtitle: {
    textAlign: 'center',
    color: '#7f8c8d',
    fontSize: '18px',
    marginBottom: '30px',
  },
  description: {
    marginBottom: '30px',
  },
  sectionTitle: {
    color: '#34495e',
    fontSize: '20px',
    marginBottom: '15px',
  },
  featureList: {
    lineHeight: '2',
    fontSize: '16px',
    color: '#555',
  },
  actions: {
    display: 'flex',
    gap: '15px',
    marginBottom: '30px',
    flexWrap: 'wrap',
  },
  primaryButton: {
    flex: 1,
    textDecoration: 'none',
    backgroundColor: '#3498db',
    color: 'white',
    padding: '15px 30px',
    borderRadius: '8px',
    textAlign: 'center',
    fontSize: '16px',
    fontWeight: 'bold',
    transition: 'background 0.3s',
    minWidth: '200px',
  },
  secondaryButton: {
    flex: 1,
    textDecoration: 'none',
    backgroundColor: '#27ae60',
    color: 'white',
    padding: '15px 30px',
    borderRadius: '8px',
    textAlign: 'center',
    fontSize: '16px',
    fontWeight: 'bold',
    transition: 'background 0.3s',
    minWidth: '200px',
  },
  info: {
    backgroundColor: '#f0f9ff',
    padding: '20px',
    borderRadius: '8px',
    color: '#555',
    lineHeight: '1.8',
  },
};

export default HomePage;
