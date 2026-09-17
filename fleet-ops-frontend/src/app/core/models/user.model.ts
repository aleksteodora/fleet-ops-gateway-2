export interface User {
  readonly id: number;
  readonly email: string;
  readonly firstName: string;
  readonly lastName: string;
  readonly role: 'ADMIN' | 'COMPANY_USER';
  readonly active: boolean;
  readonly companyName: string | null;
  readonly createdAt: string;
  readonly updatedAt: string;
}