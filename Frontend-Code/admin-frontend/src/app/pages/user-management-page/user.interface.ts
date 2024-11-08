export interface User {
  id: string;
  name: string;
  email: string;
  organization: string;
  role: 'nsp' | 'facilitator';
  status: 'active' | 'pending' | 'suspended';
  lastLogin: Date;
}
