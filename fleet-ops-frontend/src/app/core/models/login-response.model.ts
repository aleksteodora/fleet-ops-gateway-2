export interface LoginResponse {
  userId: number;
  companyId: number | null;
  role: 'ADMIN' | 'COMPANY_USER';
}