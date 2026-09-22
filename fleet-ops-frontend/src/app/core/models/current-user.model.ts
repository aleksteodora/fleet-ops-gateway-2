export interface CurrentUser {
  readonly userId: number;
  readonly companyId: number | null;
  readonly role: 'ADMIN' | 'COMPANY_USER';
  readonly firstName: string;
  readonly lastName: string;
}