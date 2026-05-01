export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  phone?: string;
  gender?: string;
  role: 'CUSTOMER' | 'CORPORATE';
  storeName?: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  userId: string;
  email: string;
  role: string;
}

export interface JwtPayload {
  sub: string;
  exp: number;
  iat: number;
}

export interface ChatSession {
  sessionId: string;
  title: string;
  createdAt: string;
  updatedAt: string;
  messageCount: number;
}

export interface ChatAskResponse {
  sessionId: string;
  question: string;
  answer: string;
  sqlQuery: string;
  visualizationCode: string;
  hasVisualization: boolean;
}
