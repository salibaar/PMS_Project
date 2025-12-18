import React, { useState } from 'react';

const SmartPlanning = () => {
  const [isBreakthrough, setIsBreakthrough] = useState(false);
  const [taskContent, setTaskContent] = useState('');

  const handleSave = async () => {
    // Tự động lấy địa chỉ API
    const API_URL = 'http://localhost:8080/api/v1';
    
    try {
      const response = await fetch(`${API_URL}/planning/objectives`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          planId: 2026,
          content: taskContent,
          isBreakthrough: isBreakthrough,
          keyResults: []
        })
      });

      if (response.ok) {
        alert("✅ LƯU THÀNH CÔNG! Hệ thống đã ghi nhận.");
        setTaskContent('');
      } else {
        alert("❌ Lỗi: Nội dung quá ngắn hoặc không hợp lệ.");
      }
    } catch (e) {
      alert("⚠️ Lỗi kết nối: " + e.message);
    }
  };

  return (
    <div style={{ padding: '40px', fontFamily: 'Arial', backgroundColor: '#f4f6f8', minHeight: '100vh' }}>
      <div style={{ maxWidth: '800px', margin: '0 auto', background: 'white', padding: '30px', borderRadius: '12px', boxShadow: '0 4px 12px rgba(0,0,0,0.1)' }}>
        
        <h1 style={{ color: '#2c3e50' }}>🚩 Lập Kế Hoạch 2026</h1>
        
        <div style={{ marginBottom: '20px' }}>
          <label style={{ display: 'block', marginBottom: '8px', fontWeight: 'bold' }}>Nhiệm vụ:</label>
          <textarea 
            value={taskContent}
            onChange={(e) => setTaskContent(e.target.value)}
            placeholder="VD: Chuyển đổi số toàn diện..."
            style={{ width: '100%', padding: '12px', borderRadius: '8px', border: '1px solid #ccc', minHeight: '80px' }}
          />
        </div>

        {/* NÚT GẠT MÀU CAM */}
        <div 
          onClick={() => setIsBreakthrough(!isBreakthrough)}
          style={{ 
            padding: '15px', 
            border: isBreakthrough ? '2px solid #ff6b00' : '2px solid #eee',
            backgroundColor: isBreakthrough ? '#fff8f0' : '#f9f9f9',
            borderRadius: '8px', cursor: 'pointer', display: 'flex', gap: '15px', marginBottom: '30px'
          }}
        >
          <div style={{ width: '50px', height: '26px', background: isBreakthrough ? '#ff6b00' : '#ccc', borderRadius: '20px', position: 'relative' }}>
            <div style={{ width: '20px', height: '20px', background: 'white', borderRadius: '50%', position: 'absolute', top: '3px', left: isBreakthrough ? '26px' : '4px', transition: '0.3s' }}/>
          </div>
          <div>
            <strong style={{ color: isBreakthrough ? '#ff6b00' : '#666' }}>
              {isBreakthrough ? 'NHIỆM VỤ ĐỘT PHÁ (Ưu tiên)' : 'Nhiệm vụ thường xuyên'}
            </strong>
          </div>
        </div>

        <button onClick={handleSave} style={{ background: '#27ae60', color: 'white', border: 'none', padding: '12px 30px', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}>
          LƯU NHIỆM VỤ
        </button>
      </div>
    </div>
  );
};

export default SmartPlanning;