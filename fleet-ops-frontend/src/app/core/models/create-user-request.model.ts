export interface CreateUserRequest {
  readonly companyId: number | null;
  readonly email: string;
  readonly firstName: string;
  readonly lastName: string;
  readonly role: 'ADMIN' | 'COMPANY_USER';
}