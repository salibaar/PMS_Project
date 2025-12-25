import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { planService } from '../services/pmsService';

function CreatePlanPage() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    year: new Date().getFullYear(),
    title: '',
    description: '',
    status: 'DRAFT',
  });
  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const validateForm = () => {
    const newErrors = {};
    if (!formData.title.trim()) {
      newErrors.title = 'Tiêu đề không được để trống';
    } else if (formData.title.length > 200) {
      newErrors.title = 'Tiêu đề tối đa 200 ký tự';
    }
    
    if (formData.year < 2020 || formData.year > 2100) {
      newErrors.year = 'Năm phải từ 2020 đến 2100';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    try {
      setLoading(true);
      await planService.createPlan(formData);
      alert('✅ Tạo kế hoạch thành công!');
      navigate('/plans');
    } catch (err) {
      alert('❌ Lỗi: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <h1 style={styles.title}>➕ Tạo Kế Hoạch Mới</h1>
        
        <form onSubmit={handleSubmit} style={styles.form}>
          <div style={styles.formGroup}>
            <label style={styles.label}>
              Năm <span style={styles.required}>*</span>
            </label>
            <input
              type="number"
              name="year"
              value={formData.year}
              onChange={handleChange}
              style={styles.input}
              min="2020"
              max="2100"
              required
            />
            {errors.year && <span style={styles.errorText}>{errors.year}</span>}
          </div>

          <div style={styles.formGroup}>
            <label style={styles.label}>
              Tiêu đề <span style={styles.required}>*</span>
            </label>
            <input
              type="text"
              name="title"
              value={formData.title}
              onChange={handleChange}
              style={styles.input}
              placeholder="VD: Kế hoạch phát triển kinh tế xã hội năm 2025"
              maxLength={200}
              required
            />
            <span style={styles.charCount}>{formData.title.length}/200 ký tự</span>
            {errors.title && <span style={styles.errorText}>{errors.title}</span>}
          </div>

          <div style={styles.formGroup}>
            <label style={styles.label}>Mô tả</label>
            <textarea
              name="description"
              value={formData.description}
              onChange={handleChange}
              style={{...styles.input, minHeight: '120px', resize: 'vertical'}}
              placeholder="Mô tả chi tiết về kế hoạch..."
              maxLength={5000}
            />
            <span style={styles.charCount}>{formData.description.length}/5000 ký tự</span>
          </div>

          <div style={styles.formGroup}>
            <label style={styles.label}>Trạng thái</label>
            <select
              name="status"
              value={formData.status}
              onChange={handleChange}
              style={styles.input}
            >
              <option value="DRAFT">Nháp</option>
              <option value="ACTIVE">Đang thực hiện</option>
              <option value="COMPLETED">Hoàn thành</option>
              <option value="ARCHIVED">Lưu trữ</option>
            </select>
          </div>

          <div style={styles.actions}>
            <button 
              type="submit" 
              style={styles.submitButton}
              disabled={loading}
            >
              {loading ? '⏳ Đang lưu...' : '💾 Tạo Kế Hoạch'}
            </button>
            <button 
              type="button" 
              onClick={() => navigate('/plans')}
              style={styles.cancelButton}
            >
              Hủy
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

const styles = {
  container: {
    maxWidth: '800px',
    margin: '0 auto',
    padding: '20px',
  },
  card: {
    backgroundColor: 'white',
    borderRadius: '12px',
    padding: '40px',
    boxShadow: '0 4px 12px rgba(0,0,0,0.1)',
  },
  title: {
    color: '#2c3e50',
    marginBottom: '30px',
  },
  form: {
    display: 'flex',
    flexDirection: 'column',
    gap: '20px',
  },
  formGroup: {
    display: 'flex',
    flexDirection: 'column',
    gap: '8px',
  },
  label: {
    fontWeight: 'bold',
    color: '#2c3e50',
    fontSize: '14px',
  },
  required: {
    color: '#e74c3c',
  },
  input: {
    padding: '12px',
    borderRadius: '8px',
    border: '1px solid #ddd',
    fontSize: '14px',
    fontFamily: 'Arial, sans-serif',
  },
  charCount: {
    fontSize: '12px',
    color: '#7f8c8d',
    textAlign: 'right',
  },
  errorText: {
    color: '#e74c3c',
    fontSize: '12px',
  },
  actions: {
    display: 'flex',
    gap: '15px',
    marginTop: '20px',
  },
  submitButton: {
    flex: 2,
    padding: '14px',
    backgroundColor: '#27ae60',
    color: 'white',
    border: 'none',
    borderRadius: '8px',
    fontSize: '16px',
    fontWeight: 'bold',
    cursor: 'pointer',
  },
  cancelButton: {
    flex: 1,
    padding: '14px',
    backgroundColor: '#95a5a6',
    color: 'white',
    border: 'none',
    borderRadius: '8px',
    fontSize: '16px',
    cursor: 'pointer',
  },
};

export default CreatePlanPage;
