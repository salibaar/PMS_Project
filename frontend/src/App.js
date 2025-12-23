import React, { useState } from 'react';

const SmartPlanning = () => {
  const [isBreakthrough, setIsBreakthrough] = useState(false);
  const [taskContent, setTaskContent] = useState('');
  const [showHelp, setShowHelp] = useState(false);

  const handleSave = async () => {
    // Tự động lấy địa chỉ API từ biến môi trường
    const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api/v1';
    
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
        
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
          <h1 style={{ color: '#2c3e50', margin: 0 }}>🚩 Lập Kế Hoạch 2026</h1>
          <button 
            onClick={() => setShowHelp(!showHelp)}
            style={{ 
              background: '#3498db', 
              color: 'white', 
              border: 'none', 
              padding: '8px 16px', 
              borderRadius: '6px', 
              cursor: 'pointer',
              fontSize: '14px'
            }}
          >
            {showHelp ? '✕ Đóng' : '❓ Hướng dẫn'}
          </button>
        </div>

        {showHelp && (
          <div style={{ 
            background: '#e8f4f8', 
            border: '2px solid #3498db', 
            borderRadius: '8px', 
            padding: '20px', 
            marginBottom: '25px' 
          }}>
            <h3 style={{ marginTop: 0, color: '#2c3e50' }}>📖 Cách sử dụng:</h3>
            <ol style={{ lineHeight: '1.8', paddingLeft: '20px' }}>
              <li><strong>Nhập nhiệm vụ:</strong> Gõ mô tả nhiệm vụ vào ô văn bản (tối thiểu 10 ký tự)</li>
              <li><strong>Chọn loại nhiệm vụ:</strong> Click vào nút gạt để đánh dấu nhiệm vụ đột phá (màu cam) hoặc nhiệm vụ thường xuyên (màu xám)</li>
              <li><strong>Lưu nhiệm vụ:</strong> Click nút "LƯU NHIỆM VỤ" màu xanh lá</li>
              <li><strong>Kết quả:</strong> Thông báo thành công sẽ hiện lên và form sẽ được xóa sạch</li>
            </ol>
            <p style={{ marginBottom: 0, fontSize: '14px', color: '#555' }}>
              💡 <strong>Lưu ý:</strong> Nhiệm vụ đột phá là những mục tiêu quan trọng, ưu tiên cao cần đạt được trong năm.
            </p>
          </div>
        )}
        
        <div style={{ marginBottom: '20px' }}>
          <label style={{ display: 'block', marginBottom: '8px', fontWeight: 'bold' }}>
            Nhiệm vụ: <span style={{ color: '#e74c3c', fontSize: '14px' }}>(Tối thiểu 10 ký tự)</span>
          </label>
          <textarea 
            value={taskContent}
            onChange={(e) => setTaskContent(e.target.value)}
            placeholder="VD: Chuyển đổi số toàn diện trong hoạt động quản lý và điều hành"
            style={{ 
              width: '100%', 
              padding: '12px', 
              borderRadius: '8px', 
              border: '1px solid #ccc', 
              minHeight: '100px',
              fontSize: '15px',
              fontFamily: 'Arial'
            }}
          />
          <div style={{ fontSize: '12px', color: '#888', marginTop: '5px' }}>
            {taskContent.length} ký tự {taskContent.length >= 10 ? '✅' : '(cần thêm ' + (10 - taskContent.length) + ')'}
          </div>
        </div>

        <div style={{ marginBottom: '10px' }}>
          <label style={{ display: 'block', marginBottom: '8px', fontWeight: 'bold' }}>
            Loại nhiệm vụ:
          </label>
        </div>

        {/* NÚT GẠT MÀU CAM */}
        <div 
          onClick={() => setIsBreakthrough(!isBreakthrough)}
          style={{ 
            padding: '15px', 
            border: isBreakthrough ? '2px solid #ff6b00' : '2px solid #eee',
            backgroundColor: isBreakthrough ? '#fff8f0' : '#f9f9f9',
            borderRadius: '8px', cursor: 'pointer', display: 'flex', gap: '15px', marginBottom: '30px',
            transition: 'all 0.3s'
          }}
        >
          <div style={{ width: '50px', height: '26px', background: isBreakthrough ? '#ff6b00' : '#ccc', borderRadius: '20px', position: 'relative' }}>
            <div style={{ width: '20px', height: '20px', background: 'white', borderRadius: '50%', position: 'absolute', top: '3px', left: isBreakthrough ? '26px' : '4px', transition: '0.3s' }}/>
          </div>
          <div>
            <strong style={{ color: isBreakthrough ? '#ff6b00' : '#666' }}>
              {isBreakthrough ? '🔥 NHIỆM VỤ ĐỘT PHÁ (Ưu tiên cao)' : '📋 Nhiệm vụ thường xuyên'}
            </strong>
            <div style={{ fontSize: '13px', color: '#666', marginTop: '4px' }}>
              {isBreakthrough 
                ? 'Nhiệm vụ quan trọng, cần ưu tiên hoàn thành' 
                : 'Click để chuyển thành nhiệm vụ đột phá'}
            </div>
          </div>
        </div>

        <button 
          onClick={handleSave} 
          disabled={taskContent.length < 10}
          style={{ 
            background: taskContent.length >= 10 ? '#27ae60' : '#95a5a6', 
            color: 'white', 
            border: 'none', 
            padding: '14px 40px', 
            borderRadius: '6px', 
            cursor: taskContent.length >= 10 ? 'pointer' : 'not-allowed', 
            fontWeight: 'bold',
            fontSize: '16px',
            width: '100%'
          }}
        >
          {taskContent.length >= 10 ? '💾 LƯU NHIỆM VỤ' : '⚠️ Nhập ít nhất 10 ký tự để lưu'}
        </button>

        <div style={{ marginTop: '20px', padding: '15px', background: '#f0f9ff', borderRadius: '8px', fontSize: '13px', color: '#555' }}>
          <strong>ℹ️ Trạng thái:</strong> Backend đang chạy tại http://localhost:8080 | 
          Dữ liệu được lưu vào PostgreSQL database
        </div>
      </div>
    </div>
  );
};

export default SmartPlanning;