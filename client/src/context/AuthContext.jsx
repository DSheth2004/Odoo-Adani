import React, { createContext, useContext, useMemo, useState } from 'react';
import { loginRequest, signupRequest } from '../lib/api';

const AuthContext = createContext(null);

function readStoredAuth() {
  try {
    const searchParams = new URLSearchParams(window.location.search);
    const urlToken = searchParams.get('token');
    const urlRole = searchParams.get('role');
    const urlEmail = searchParams.get('email');
    const urlName = searchParams.get('name');

    if (urlToken) {
      const user = {
        email: urlEmail || '',
        role: urlRole || 'employee',
        full_name: urlName || 'OAuth User'
      };
      localStorage.setItem('mms_token', urlToken);
      localStorage.setItem('mms_user', JSON.stringify(user));
      // Clean query params from URL without reload
      window.history.replaceState({}, document.title, window.location.pathname);
      return { token: urlToken, user };
    }
  } catch (e) {}

  const token = localStorage.getItem('mms_token');
  const userRaw = localStorage.getItem('mms_user');
  const user = userRaw ? JSON.parse(userRaw) : null;
  return { token, user };
}

export function AuthProvider({ children }) {
  const stored = readStoredAuth();
  const [token, setToken] = useState(stored.token);
  const [user, setUser] = useState(stored.user);

  const isAuthenticated = !!token;

  const login = async ({ email, password }) => {
    const data = await loginRequest({ email, password });
    localStorage.setItem('mms_token', data.token);
    localStorage.setItem('mms_user', JSON.stringify(data.user));
    // Trigger a one-time role popup after successful login.
    sessionStorage.setItem('mms_show_role_popup', '1');
    setToken(data.token);
    setUser(data.user);
    return data.user;
  };

  const signup = async ({ full_name, email, password }) => {
    const data = await signupRequest({ full_name, email, password });
    localStorage.setItem('mms_token', data.token);
    localStorage.setItem('mms_user', JSON.stringify(data.user));
    // Trigger a one-time role popup after successful signup (auto-login).
    sessionStorage.setItem('mms_show_role_popup', '1');
    setToken(data.token);
    setUser(data.user);
    return data.user;
  };

  const setAuthData = ({ token, user }) => {
    localStorage.setItem('mms_token', token);
    localStorage.setItem('mms_user', JSON.stringify(user));
    sessionStorage.setItem('mms_show_role_popup', '1');
    setToken(token);
    setUser(user);
  };

  const logout = () => {
    localStorage.removeItem('mms_token');
    localStorage.removeItem('mms_user');
    setToken(null);
    setUser(null);
  };

  const value = useMemo(
    () => ({ token, user, isAuthenticated, login, signup, setAuthData, logout }),
    [token, user, isAuthenticated]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
