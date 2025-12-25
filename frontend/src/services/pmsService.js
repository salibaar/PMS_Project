import api from './api';

export const planService = {
  // Get all plans with pagination
  getAllPlans: async (page = 0, size = 10, sortBy = 'createdAt', sortDir = 'DESC') => {
    const response = await api.get('/plans', {
      params: { page, size, sortBy, sortDir }
    });
    return response.data;
  },

  // Get plan by ID
  getPlanById: async (id) => {
    const response = await api.get(`/plans/${id}`);
    return response.data;
  },

  // Create new plan
  createPlan: async (planData) => {
    const response = await api.post('/plans', planData);
    return response.data;
  },

  // Update plan
  updatePlan: async (id, planData) => {
    const response = await api.put(`/plans/${id}`, planData);
    return response.data;
  },

  // Delete plan
  deletePlan: async (id) => {
    await api.delete(`/plans/${id}`);
  },

  // Get plans by year
  getPlansByYear: async (year, page = 0, size = 10) => {
    const response = await api.get(`/plans/year/${year}`, {
      params: { page, size }
    });
    return response.data;
  },

  // Search plans
  searchPlans: async (keyword, page = 0, size = 10) => {
    const response = await api.get('/plans/search', {
      params: { keyword, page, size }
    });
    return response.data;
  },
};

export const objectiveService = {
  // Get objective by ID
  getObjectiveById: async (id) => {
    const response = await api.get(`/objectives/${id}`);
    return response.data;
  },

  // Create new objective
  createObjective: async (objectiveData) => {
    const response = await api.post('/objectives', objectiveData);
    return response.data;
  },

  // Update objective
  updateObjective: async (id, objectiveData) => {
    const response = await api.put(`/objectives/${id}`, objectiveData);
    return response.data;
  },

  // Delete objective
  deleteObjective: async (id) => {
    await api.delete(`/objectives/${id}`);
  },

  // Get objectives by plan ID
  getObjectivesByPlanId: async (planId) => {
    const response = await api.get(`/objectives/plan/${planId}`);
    return response.data;
  },

  // Get child objectives
  getChildObjectives: async (parentId) => {
    const response = await api.get(`/objectives/${parentId}/children`);
    return response.data;
  },

  // Get breakthrough objectives
  getBreakthroughObjectives: async (planId) => {
    const response = await api.get(`/objectives/plan/${planId}/breakthrough`);
    return response.data;
  },
};

export const keyResultService = {
  // Get key result by ID
  getKeyResultById: async (id) => {
    const response = await api.get(`/key-results/${id}`);
    return response.data;
  },

  // Create new key result
  createKeyResult: async (keyResultData) => {
    const response = await api.post('/key-results', keyResultData);
    return response.data;
  },

  // Update key result
  updateKeyResult: async (id, keyResultData) => {
    const response = await api.put(`/key-results/${id}`, keyResultData);
    return response.data;
  },

  // Delete key result
  deleteKeyResult: async (id) => {
    await api.delete(`/key-results/${id}`);
  },

  // Get key results by objective ID
  getKeyResultsByObjectiveId: async (objectiveId) => {
    const response = await api.get(`/key-results/objective/${objectiveId}`);
    return response.data;
  },
};
