import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { planService } from '../services/pmsService';

function PlansListPage() {
  const [plans, setPlans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchKeyword, setSearchKeyword] = useState('');

  useEffect(() => {
    loadPlans();
  }, [page]);

  const loadPlans = async () => {
    try {
      setLoading(true);
      const response = await planService.getAllPlans(page, 10);
      setPlans(response.plans || []);
      setTotalPages(response.totalPages || 0);
      setError(null);
    } catch (err) {
      setError('Không thể tải danh sách kế hoạch. Vui lòng kiểm tra kết nối backend.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!searchKeyword.trim()) {
      loadPlans();
      return;
    }
    
    try {
      setLoading(true);
      const response = await planService.searchPlans(searchKeyword, 0, 10);
      setPlans(response.plans || []);
      setTotalPages(response.totalPages || 0);
      setPage(0);
    } catch (err) {
      setError('Tìm kiếm thất bại');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Bạn có chắc muốn xóa kế hoạch này?')) return;
    
    try {
      await planService.deletePlan(id);
      loadPlans();
    } catch (err) {
      alert('Xóa thất bại: ' + err.message);
    }
  };

  const getStatusBadge = (status) => {
    const statusStyles = {
      DRAFT: { bg: '#95a5a6', text: 'Nháp' },
      ACTIVE: { bg: '#3498db', text: 'Đang thực hiện' },
      COMPLETED: { bg: '#27ae60', text: 'Hoàn thành' },
      ARCHIVED: { bg: '#7f8c8d', text: 'Lưu trữ' },
    };
    const style = statusStyles[status] || statusStyles.DRAFT;
    return <span style={{...styles.badge, backgroundColor: style.bg}}>{style.text}</span>;
  };

  if (loading) return <div style={styles.loading}>⏳ Đang tải...</div>;
  if (error) return <div style={styles.error}>{error}</div>;

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <h1 style={styles.title}>📋 Danh Sách Kế Hoạch</h1>
        <Link to="/plans/create" style={styles.createButton}>
          ➕ Tạo Kế Hoạch Mới
        </Link>
      </div>

      <form onSubmit={handleSearch} style={styles.searchForm}>
        <input
          type="text"
          placeholder="🔍 Tìm kiếm theo tiêu đề..."
          value={searchKeyword}
          onChange={(e) => setSearchKeyword(e.target.value)}
          style={styles.searchInput}
        />
        <button type="submit" style={styles.searchButton}>Tìm kiếm</button>
        {searchKeyword && (
          <button 
            type="button" 
            onClick={() => {setSearchKeyword(''); loadPlans();}}
            style={styles.clearButton}
          >
            Xóa
          </button>
        )}
      </form>

      {plans.length === 0 ? (
        <div style={styles.empty}>
          <p>📭 Chưa có kế hoạch nào</p>
          <Link to="/plans/create" style={styles.emptyLink}>Tạo kế hoạch đầu tiên</Link>
        </div>
      ) : (
        <>
          <div style={styles.plansList}>
            {plans.map((plan) => (
              <div key={plan.id} style={styles.planCard}>
                <div style={styles.planHeader}>
                  <h3 style={styles.planTitle}>{plan.title}</h3>
                  {getStatusBadge(plan.status)}
                </div>
                
                <div style={styles.planMeta}>
                  <span>📅 Năm: {plan.year}</span>
                  <span>🗓️ {new Date(plan.createdAt).toLocaleDateString('vi-VN')}</span>
                </div>
                
                {plan.description && (
                  <p style={styles.planDesc}>{plan.description}</p>
                )}
                
                <div style={styles.planActions}>
                  <Link to={`/plans/${plan.id}`} style={styles.viewButton}>
                    👁️ Xem chi tiết
                  </Link>
                  <button 
                    onClick={() => handleDelete(plan.id)}
                    style={styles.deleteButton}
                  >
                    🗑️ Xóa
                  </button>
                </div>
              </div>
            ))}
          </div>

          {totalPages > 1 && (
            <div style={styles.pagination}>
              <button 
                disabled={page === 0}
                onClick={() => setPage(page - 1)}
                style={styles.pageButton}
              >
                ← Trước
              </button>
              <span style={styles.pageInfo}>
                Trang {page + 1} / {totalPages}
              </span>
              <button 
                disabled={page >= totalPages - 1}
                onClick={() => setPage(page + 1)}
                style={styles.pageButton}
              >
                Sau →
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
}

const styles = {
  container: {
    maxWidth: '1200px',
    margin: '0 auto',
    padding: '20px',
  },
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '30px',
    flexWrap: 'wrap',
    gap: '15px',
  },
  title: {
    color: '#2c3e50',
    margin: 0,
  },
  createButton: {
    textDecoration: 'none',
    backgroundColor: '#27ae60',
    color: 'white',
    padding: '12px 24px',
    borderRadius: '8px',
    fontWeight: 'bold',
  },
  searchForm: {
    display: 'flex',
    gap: '10px',
    marginBottom: '30px',
    flexWrap: 'wrap',
  },
  searchInput: {
    flex: 1,
    minWidth: '250px',
    padding: '12px',
    borderRadius: '8px',
    border: '1px solid #ddd',
    fontSize: '14px',
  },
  searchButton: {
    padding: '12px 24px',
    backgroundColor: '#3498db',
    color: 'white',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
    fontWeight: 'bold',
  },
  clearButton: {
    padding: '12px 24px',
    backgroundColor: '#95a5a6',
    color: 'white',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
  },
  plansList: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fill, minmax(350px, 1fr))',
    gap: '20px',
  },
  planCard: {
    backgroundColor: 'white',
    borderRadius: '12px',
    padding: '20px',
    boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
  },
  planHeader: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'start',
    marginBottom: '15px',
  },
  planTitle: {
    margin: 0,
    color: '#2c3e50',
    fontSize: '18px',
    flex: 1,
  },
  badge: {
    padding: '4px 12px',
    borderRadius: '12px',
    color: 'white',
    fontSize: '12px',
    fontWeight: 'bold',
  },
  planMeta: {
    display: 'flex',
    gap: '15px',
    fontSize: '14px',
    color: '#7f8c8d',
    marginBottom: '15px',
  },
  planDesc: {
    color: '#555',
    fontSize: '14px',
    marginBottom: '15px',
    lineHeight: '1.5',
  },
  planActions: {
    display: 'flex',
    gap: '10px',
  },
  viewButton: {
    flex: 1,
    textDecoration: 'none',
    textAlign: 'center',
    padding: '10px',
    backgroundColor: '#3498db',
    color: 'white',
    borderRadius: '6px',
    fontSize: '14px',
  },
  deleteButton: {
    padding: '10px 20px',
    backgroundColor: '#e74c3c',
    color: 'white',
    border: 'none',
    borderRadius: '6px',
    cursor: 'pointer',
    fontSize: '14px',
  },
  pagination: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    gap: '20px',
    marginTop: '30px',
  },
  pageButton: {
    padding: '10px 20px',
    backgroundColor: '#3498db',
    color: 'white',
    border: 'none',
    borderRadius: '6px',
    cursor: 'pointer',
    fontSize: '14px',
  },
  pageInfo: {
    fontSize: '16px',
    color: '#555',
  },
  loading: {
    textAlign: 'center',
    padding: '50px',
    fontSize: '20px',
    color: '#555',
  },
  error: {
    textAlign: 'center',
    padding: '50px',
    fontSize: '18px',
    color: '#e74c3c',
    backgroundColor: '#ffebee',
    borderRadius: '8px',
  },
  empty: {
    textAlign: 'center',
    padding: '50px',
    backgroundColor: 'white',
    borderRadius: '12px',
  },
  emptyLink: {
    display: 'inline-block',
    marginTop: '20px',
    textDecoration: 'none',
    backgroundColor: '#27ae60',
    color: 'white',
    padding: '12px 24px',
    borderRadius: '8px',
  },
};

export default PlansListPage;
