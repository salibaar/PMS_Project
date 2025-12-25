import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { planService, objectiveService } from '../services/pmsService';

function PlanDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [plan, setPlan] = useState(null);
  const [objectives, setObjectives] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadPlanDetails();
  }, [id]);

  const loadPlanDetails = async () => {
    try {
      setLoading(true);
      const [planData, objectivesData] = await Promise.all([
        planService.getPlanById(id),
        objectiveService.getObjectivesByPlanId(id),
      ]);
      setPlan(planData);
      setObjectives(objectivesData || []);
      setError(null);
    } catch (err) {
      setError('Không thể tải thông tin kế hoạch');
      console.error(err);
    } finally {
      setLoading(false);
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
  if (error || !plan) return <div style={styles.error}>{error || 'Không tìm thấy kế hoạch'}</div>;

  return (
    <div style={styles.container}>
      <div style={styles.breadcrumb}>
        <Link to="/plans" style={styles.breadcrumbLink}>📋 Kế hoạch</Link>
        <span> / </span>
        <span>{plan.title}</span>
      </div>

      <div style={styles.card}>
        <div style={styles.header}>
          <div>
            <h1 style={styles.title}>{plan.title}</h1>
            <div style={styles.meta}>
              <span>📅 Năm: {plan.year}</span>
              <span>🗓️ Tạo: {new Date(plan.createdAt).toLocaleDateString('vi-VN')}</span>
              <span>🔄 Cập nhật: {new Date(plan.updatedAt).toLocaleDateString('vi-VN')}</span>
            </div>
          </div>
          {getStatusBadge(plan.status)}
        </div>

        {plan.description && (
          <div style={styles.description}>
            <h3 style={styles.sectionTitle}>📝 Mô tả</h3>
            <p style={styles.descriptionText}>{plan.description}</p>
          </div>
        )}

        <div style={styles.objectives}>
          <div style={styles.objectivesHeader}>
            <h3 style={styles.sectionTitle}>🎯 Danh sách Nhiệm vụ ({objectives.length})</h3>
          </div>

          {objectives.length === 0 ? (
            <div style={styles.empty}>
              <p>📭 Chưa có nhiệm vụ nào</p>
              <p style={{fontSize: '14px', color: '#7f8c8d'}}>
                Nhiệm vụ có thể được thêm thông qua API
              </p>
            </div>
          ) : (
            <div style={styles.objectivesList}>
              {objectives.map((obj, index) => (
                <div key={obj.id} style={styles.objectiveCard}>
                  <div style={styles.objectiveHeader}>
                    <span style={styles.objectiveNumber}>#{index + 1}</span>
                    {obj.isBreakthrough && (
                      <span style={styles.breakthroughBadge}>🔥 Đột phá</span>
                    )}
                    <span style={{...styles.statusBadge, ...getObjectiveStatusStyle(obj.status)}}>
                      {getObjectiveStatusText(obj.status)}
                    </span>
                  </div>
                  
                  <p style={styles.objectiveContent}>{obj.content}</p>
                  
                  <div style={styles.progressContainer}>
                    <div style={styles.progressLabel}>
                      Tiến độ: {obj.progress}%
                    </div>
                    <div style={styles.progressBar}>
                      <div style={{...styles.progressFill, width: `${obj.progress}%`}} />
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        <div style={styles.actions}>
          <button onClick={() => navigate('/plans')} style={styles.backButton}>
            ← Quay lại danh sách
          </button>
        </div>
      </div>
    </div>
  );
}

const getObjectiveStatusStyle = (status) => {
  const styles = {
    NOT_STARTED: { backgroundColor: '#95a5a6' },
    IN_PROGRESS: { backgroundColor: '#3498db' },
    COMPLETED: { backgroundColor: '#27ae60' },
    BLOCKED: { backgroundColor: '#e74c3c' },
  };
  return styles[status] || styles.NOT_STARTED;
};

const getObjectiveStatusText = (status) => {
  const texts = {
    NOT_STARTED: 'Chưa bắt đầu',
    IN_PROGRESS: 'Đang thực hiện',
    COMPLETED: 'Hoàn thành',
    BLOCKED: 'Bị chặn',
  };
  return texts[status] || 'Chưa bắt đầu';
};

const styles = {
  container: {
    maxWidth: '1000px',
    margin: '0 auto',
    padding: '20px',
  },
  breadcrumb: {
    marginBottom: '20px',
    fontSize: '14px',
    color: '#7f8c8d',
  },
  breadcrumbLink: {
    color: '#3498db',
    textDecoration: 'none',
  },
  card: {
    backgroundColor: 'white',
    borderRadius: '12px',
    padding: '40px',
    boxShadow: '0 4px 12px rgba(0,0,0,0.1)',
  },
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'start',
    marginBottom: '30px',
  },
  title: {
    color: '#2c3e50',
    marginBottom: '15px',
  },
  meta: {
    display: 'flex',
    gap: '20px',
    fontSize: '14px',
    color: '#7f8c8d',
    flexWrap: 'wrap',
  },
  badge: {
    padding: '8px 16px',
    borderRadius: '12px',
    color: 'white',
    fontSize: '14px',
    fontWeight: 'bold',
  },
  description: {
    marginBottom: '30px',
    padding: '20px',
    backgroundColor: '#f8f9fa',
    borderRadius: '8px',
  },
  sectionTitle: {
    color: '#2c3e50',
    marginBottom: '15px',
    fontSize: '18px',
  },
  descriptionText: {
    color: '#555',
    lineHeight: '1.8',
    margin: 0,
  },
  objectives: {
    marginBottom: '30px',
  },
  objectivesHeader: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '20px',
  },
  objectivesList: {
    display: 'flex',
    flexDirection: 'column',
    gap: '15px',
  },
  objectiveCard: {
    padding: '20px',
    border: '1px solid #e0e0e0',
    borderRadius: '8px',
    backgroundColor: '#fafafa',
  },
  objectiveHeader: {
    display: 'flex',
    gap: '10px',
    marginBottom: '10px',
    flexWrap: 'wrap',
    alignItems: 'center',
  },
  objectiveNumber: {
    fontWeight: 'bold',
    color: '#3498db',
  },
  breakthroughBadge: {
    padding: '4px 12px',
    backgroundColor: '#ff6b00',
    color: 'white',
    borderRadius: '12px',
    fontSize: '12px',
    fontWeight: 'bold',
  },
  statusBadge: {
    padding: '4px 12px',
    borderRadius: '12px',
    color: 'white',
    fontSize: '12px',
    fontWeight: 'bold',
  },
  objectiveContent: {
    color: '#555',
    lineHeight: '1.6',
    marginBottom: '15px',
  },
  progressContainer: {
    marginTop: '10px',
  },
  progressLabel: {
    fontSize: '12px',
    color: '#7f8c8d',
    marginBottom: '5px',
  },
  progressBar: {
    height: '8px',
    backgroundColor: '#e0e0e0',
    borderRadius: '4px',
    overflow: 'hidden',
  },
  progressFill: {
    height: '100%',
    backgroundColor: '#3498db',
    transition: 'width 0.3s',
  },
  actions: {
    display: 'flex',
    gap: '15px',
  },
  backButton: {
    padding: '12px 24px',
    backgroundColor: '#3498db',
    color: 'white',
    border: 'none',
    borderRadius: '8px',
    fontSize: '14px',
    cursor: 'pointer',
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
    padding: '40px',
    backgroundColor: '#f8f9fa',
    borderRadius: '8px',
    color: '#7f8c8d',
  },
};

export default PlanDetailPage;
