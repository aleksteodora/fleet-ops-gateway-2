export interface LoginResponse {
  readonly userId: number;
  readonly companyId: number | null;
  readonly role: 'ADMIN' | 'COMPANY_USER';
}